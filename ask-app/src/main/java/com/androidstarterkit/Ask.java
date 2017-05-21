package com.androidstarterkit;

import com.androidstarterkit.android.api.Extension;
import com.androidstarterkit.android.api.resource.AttributeContraints;
import com.androidstarterkit.command.CommandParser;
import com.androidstarterkit.command.TabType;
import com.androidstarterkit.config.AskConfig;
import com.androidstarterkit.directory.RemoteDirectory;
import com.androidstarterkit.directory.SourceDirectory;
import com.androidstarterkit.exception.CommandException;
import com.androidstarterkit.file.SettingsGradle;
import com.androidstarterkit.injection.AskJsonBuilder;
import com.androidstarterkit.injection.ModuleLoader;
import com.androidstarterkit.injection.model.AskJson;
import com.androidstarterkit.injection.model.GradleConfig;
import com.androidstarterkit.injection.model.LayoutGroup;
import com.androidstarterkit.injection.model.ManifestConfig;
import com.androidstarterkit.injection.model.Module;
import com.androidstarterkit.injection.model.ModuleGroup;
import com.androidstarterkit.tool.ExecuteShellComand;
import com.androidstarterkit.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Ask {
  static final int env = AskConfig.PRODUCTION;
  static final int output = AskConfig.OUTPUT_PROD_PROJECT;

  public static void main(String[] args) {
    CommandParser commandParser;
    try {
      commandParser = new CommandParser(args);
    } catch (CommandException e) {
      Console.log(e);
      return;
    }

    if (commandParser.hasHelpCommand()) {
      Console.printHelp();
      return;
    }

    String projectPath = commandParser.getPath();
    final TabType tabType = commandParser.getTabType();
    final List<String> layoutCommands = commandParser.getLayoutCommands();
    final List<String> moduleCommands = commandParser.getModuleCommands();

    /*
      * AskConfig.OUTPUT_PROD_PROJECT : output is project
      * AskConfig.OUTPUT_DEV_ASK_SAMPLE : output is ask-sample
      */
    String sourceModuleName;
    if (output == AskConfig.OUTPUT_DEV_ASK_SAMPLE && projectPath == null) {
      projectPath = FileUtils.getRootPath();
      sourceModuleName = AskConfig.DEFAULT_SAMPLE_MODULE_NAME;
    } else {
      if (output == AskConfig.OUTPUT_PROD_PROJECT && projectPath == null) {
        File sampleDirectory = new File(FileUtils.getRootPath() + "/ask-app/AndroidProject");
        File outputDirectory = new File(FileUtils.getRootPath() + "/output/AndroidProject");

        try {
          FileUtils.copyDirectory(sampleDirectory, outputDirectory);
        } catch (IOException e) {
          e.printStackTrace();
        }

        projectPath = outputDirectory.getPath();
      }

      SettingsGradle settingsGradleFile = new SettingsGradle(projectPath);
      sourceModuleName = settingsGradleFile.getAppModuleName();
    }

    RemoteDirectory remoteDirectory = new RemoteDirectory(FileUtils.getRootPath());
    SourceDirectory sourceDirectory = new SourceDirectory(projectPath, sourceModuleName, remoteDirectory);

    System.out.println("Analyzing Source Project...");
    System.out.println("- package: " + sourceDirectory.getAndroidManifestFile().getPackageName());
    System.out.println("- main activity: " + sourceDirectory.getAndroidManifestFile().getMainActivityName());
    System.out.println("- src path: " + sourceDirectory.getJavaPath());
    System.out.println("- layout path: " + sourceDirectory.getLayoutPath());
    System.out.println();

    // Parsing ask json files
    AskJsonBuilder askJsonBuilder = new AskJsonBuilder(FileUtils.getRootPath()
        + "/" + AskConfig.DEFAULT_ASK_APP_NAME);
    askJsonBuilder.addProperty("\\$\\{projectDir\\}", FileUtils.getRootPath());
    askJsonBuilder.addProperty("\\$\\{appDir\\}", sourceDirectory.getPath());
    askJsonBuilder.addProperty("\\$\\{javaDir\\}", sourceDirectory.getJavaPath());
    askJsonBuilder.addProperty("\\$\\{mainDir\\}", sourceDirectory.getMainPath());
    askJsonBuilder.addProperty("<main>", sourceDirectory.getMainActivityName());
    askJsonBuilder.addProperty("<package>", sourceDirectory.getApplicationId());
    AskJson askJson = askJsonBuilder.build();

    // [START] Transform layouts
    List<LayoutGroup> layoutGroups = new ArrayList<>();
    layoutGroups.addAll(layoutCommands.stream()
        .map(askJson::getLayoutGroupByCommand)
        .collect(Collectors.toList()));

    if (layoutGroups.size() > 0) {
      // Change remote repository by layout option
      remoteDirectory.changeMainFragmentByTabType(tabType, layoutGroups);
      remoteDirectory.injectLayoutModulesToFragment(layoutGroups);

      // Build remote repository
      ExecuteShellComand.executeAssemble(FileUtils.getRootPath() + "/assembleModule.sh " + AskConfig.DEFAULT_REMOTE_MODULE_NAME, true);
      System.out.println();

      // Transform files to source repository
      System.out.println("Layout is loading...");
      sourceDirectory.transformAndroidManifest();
      sourceDirectory.takeFileFromRemote(remoteDirectory.getMainActivity(), 0);

      // Recover remote repository
      remoteDirectory.recover();
    }

    // Transform modules
    List<Module> modules = new ArrayList<>();
    modules.addAll(moduleCommands.stream()
        .map(askJson::getModuleByCommand)
        .collect(Collectors.toList()));

    if (modules.size() <= 0) {
      return;
    }

    ModuleLoader moduleLoader = new ModuleLoader();
    moduleLoader.addCodeGenerator(sourceDirectory.getProjectBuildGradle());
    moduleLoader.addCodeGenerator(sourceDirectory.getAppBuildGradleFile());
    moduleLoader.addCodeGenerator(sourceDirectory.getProguardRules());
    moduleLoader.addCodeGenerator(sourceDirectory.getAndroidManifestFile());
    moduleLoader.addCodeGenerator(sourceDirectory.getMainActivity());

    if (askJson.hasApplicationProperty(modules)) {
      if (sourceDirectory.getApplication() == null) {
        sourceDirectory.takeFileFromRemote(remoteDirectory.getApplication(), 0);

        String applicationNameExt = remoteDirectory.getApplication().getName();

        sourceDirectory.getAndroidManifestFile().addApplicationAttribute(AttributeContraints.NAME,
            "." + remoteDirectory.getFilePathFromJavaDir(applicationNameExt).replaceAll("/", ".")
                + FileUtils.removeExtension(applicationNameExt));
        sourceDirectory.getAndroidManifestFile().apply();
      }
      moduleLoader.addCodeGenerator(sourceDirectory.getApplication());

      askJsonBuilder.addProperty("<application>", FileUtils.removeExtension(sourceDirectory.getApplication().getName()));
      askJson = askJsonBuilder.build();

      modules.clear();
      modules.addAll(moduleCommands.stream()
          .map(askJson::getModuleByCommand)
          .collect(Collectors.toList()));
    }

    System.out.println("\nModule is loading...");
    for (Module module : modules) {
      if (module.getClassNames() != null) {
        for (String className : module.getClassNames()) {
          sourceDirectory.takeFileFromRemote(remoteDirectory.getChildFile(className + Extension.JAVA), 0);
        }
      }

      moduleLoader.addManifestConfigs(module.getManifestConfigs());
      moduleLoader.addJavaConfigs(module.getJavaConfigs());
      moduleLoader.addGradleConfigs(module.getGradleConfigs());
    }

    // Add group configs
    List<GradleConfig> groupGradleConfigs = new ArrayList<>();
    List<ManifestConfig> groupManifestConfigs = new ArrayList<>();
    List<String> alertMessages = new ArrayList<>();
    for (String command : moduleCommands) {
      ModuleGroup moduleGroup = askJson.getModuleGroupByCommand(command);

      if (moduleGroup.getGroupGradleConfigs() != null
          && !groupGradleConfigs.containsAll(moduleGroup.getGroupGradleConfigs())) {
        groupGradleConfigs.addAll(moduleGroup.getGroupGradleConfigs());
      }

      if (moduleGroup.getGroupManifestConfigs() != null
          && !groupManifestConfigs.containsAll(moduleGroup.getGroupManifestConfigs())) {
        groupManifestConfigs.addAll(moduleGroup.getGroupManifestConfigs());
      }

      if (moduleGroup.getAlertMessage() != null) {
        if (!alertMessages.contains(moduleGroup.getAlertMessage())) {
          alertMessages.add(moduleGroup.getAlertMessage());
        }
      }
    }

    if (alertMessages.size() > 0) {
      System.out.println();
      for (String message : alertMessages) {
        System.out.println("warning: " + message);
      }
    }

    moduleLoader.addGradleConfigs(groupGradleConfigs);
    moduleLoader.addManifestConfigs(groupManifestConfigs);
    moduleLoader.generateCode();

    System.out.println("\nProject path: " + sourceDirectory.getParentFile().getPath() + "\n");

    // Check config file and show warning
    List<ModuleGroup> distinctModuleGroups = new ArrayList<>();
    for (String command : moduleCommands) {
      ModuleGroup moduleGroup = askJson.getModuleGroupByCommand(command);

      if (!distinctModuleGroups.contains(moduleGroup)) {
        distinctModuleGroups.add(moduleGroup);
      }
    }

    for (ModuleGroup moduleGroup : distinctModuleGroups) {
      if (moduleGroup.getConfigFilenames() != null) {
        for (String filename : moduleGroup.getConfigFilenames()) {
          File configFile = new File(sourceDirectory.getPath() + "/" + filename);
          if (!configFile.exists()) {
            System.out.println("warning: File " + filename + " is missing, download " + moduleGroup.getPage());
          }
        }
      }
    }
  }
}
