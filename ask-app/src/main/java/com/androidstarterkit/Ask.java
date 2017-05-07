package com.androidstarterkit;

import com.androidstarterkit.android.api.Extension;
import com.androidstarterkit.command.CommandParser;
import com.androidstarterkit.command.TabType;
import com.androidstarterkit.config.AskConfig;
import com.androidstarterkit.directory.RemoteDirectory;
import com.androidstarterkit.directory.SourceDirectory;
import com.androidstarterkit.exception.CommandException;
import com.androidstarterkit.file.SettingsGradle;
import com.androidstarterkit.injection.AskJson;
import com.androidstarterkit.injection.ModuleLoader;
import com.androidstarterkit.injection.model.GradleConfig;
import com.androidstarterkit.injection.model.LayoutGroup;
import com.androidstarterkit.injection.model.Module;
import com.androidstarterkit.injection.model.ModuleGroup;
import com.androidstarterkit.tool.ExecuteShellComand;
import com.androidstarterkit.util.FileUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Ask {
  static final int env = AskConfig.PRODUCTION;
  static final int output = AskConfig.OUTPUT_PROJECT;

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

    // initialize CommandParser
    String projectPath = commandParser.getPath();
    final TabType tabType = commandParser.getTabType();
    final List<String> layoutCommands = commandParser.getLayoutCommands();
    final List<String> moduleCommands = commandParser.getModuleCommands();

    if (layoutCommands.size() <= 0) {
      layoutCommands.add("sv");
    }

    /*
      * initialize Modules
      *
      * AskConfig.OUTPUT_PROJECT : output is project
      * AskConfig.OUTPUT_ASK_SAMPLE : output is ask-sample
      */
    String sourceModuleName;
    if (output == AskConfig.OUTPUT_ASK_SAMPLE && projectPath == null) {
      projectPath = FileUtils.getRootPath();
      sourceModuleName = AskConfig.DEFAULT_SAMPLE_MODULE_NAME;
    } else  {
      if (output == AskConfig.OUTPUT_PROJECT && projectPath == null) {
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

    // Parsing ask.json
    Gson gson = new Gson();
    AskJson askJson;
    try {
      askJson = gson.fromJson(FileUtils.readFile(FileUtils.linkPathWithSlash(FileUtils.getRootPath(), "ask-app", AskJson.FILE_NAME))
          , AskJson.class);
    } catch (IOException e) {
      throw new CommandException(CommandException.NOT_FOUND_ASK_JSON);
    }
    askJson.replace(FileUtils.getRootPath(), sourceDirectory.getPath()
        , sourceDirectory.getJavaPath(), sourceDirectory.getMainActivityName(), sourceDirectory.getApplicationId());

    // [START] Transform layouts
    List<LayoutGroup> layoutGroups = new ArrayList<>();
    layoutGroups.addAll(layoutCommands.stream()
        .map(askJson::getLayoutGroupByCommand)
        .collect(Collectors.toList()));

    // Change remote repository by layout option
    remoteDirectory.changeMainFragmentByTabType(tabType, layoutGroups);
    remoteDirectory.injectLayoutModulesToFragment(layoutGroups);

    // Build remote repository
    ExecuteShellComand.executeAssemble(FileUtils.getRootPath() + "/assembleModule.sh " + AskConfig.DEFAULT_REMOTE_MODULE_NAME, true);
    System.out.println();

    // Transform files to source repository
    System.out.println("Layout is loading...");
    sourceDirectory.transform(remoteDirectory.getMainActivity());

    // Recover remote repository
    remoteDirectory.recover();
    // [END]

    ModuleLoader moduleLoader = new ModuleLoader();
    moduleLoader.addCodeGenerator(sourceDirectory.getProjectBuildGradle());
    moduleLoader.addCodeGenerator(sourceDirectory.getAppBuildGradleFile());
    moduleLoader.addCodeGenerator(sourceDirectory.getProguardRules());
    moduleLoader.addCodeGenerator(sourceDirectory.getMainActivity());

    // Transform modules
    List<Module> modules = new ArrayList<>();
    modules.addAll(moduleCommands.stream()
        .map(askJson::getModuleByCommand)
        .collect(Collectors.toList()));

    if (modules.size() > 0) {
      System.out.println("\nModule is loading...");
    }

    for (Module module : modules) {
      for (String className : module.getClassNames()) {
        System.out.println(className + Extension.JAVA);
        sourceDirectory.transformFileFromRemote(0, remoteDirectory.getChildFile(className + Extension.JAVA));
      }

      moduleLoader.addJavaConfigs(module.getJavaConfigs());
      moduleLoader.addGradleConfigs(module.getGradleConfigs());
    }

    // Add group configs
    List<GradleConfig> groupGradleConfigs = new ArrayList<>();
    for (String command : moduleCommands) {
      ModuleGroup moduleGroup = askJson.getModuleGroupByCommand(command);

      if (!groupGradleConfigs.containsAll(moduleGroup.getGroupGradleConfigs())) {
        groupGradleConfigs.addAll(moduleGroup.getGroupGradleConfigs());
      }
    }

    moduleLoader.addGradleConfigs(groupGradleConfigs);
    moduleLoader.generateCode();

    System.out.println("\nProject path: " + sourceDirectory.getPath() + "\n");

    // Check config file and show warning
    List<ModuleGroup> distinctModuleGroups = new ArrayList<>();
    for (String command : moduleCommands) {
      ModuleGroup moduleGroup = askJson.getModuleGroupByCommand(command);

      if (!distinctModuleGroups.contains(moduleGroup)) {
        distinctModuleGroups.add(moduleGroup);
      }
    }

    for (ModuleGroup moduleGroup : distinctModuleGroups) {
      for (String filename : moduleGroup.getConfigFilenames()) {
        File configFile = new File(sourceDirectory.getPath() + "/" + filename);
        if (!configFile.exists()) {
          System.out.println("warning: File " + filename + " is missing, download " + moduleGroup.getPage());
        }
      }
    }
  }
}
