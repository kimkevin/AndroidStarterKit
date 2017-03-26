package com.androidstarterkit.file.directory;

import com.androidstarterkit.SyntaxConstraints;
import com.androidstarterkit.android.api.Extension;
import com.androidstarterkit.android.api.resource.ResourceType;
import com.androidstarterkit.command.AskJson;
import com.androidstarterkit.command.TabType;
import com.androidstarterkit.command.WidgetType;
import com.androidstarterkit.config.AskConfig;
import com.androidstarterkit.config.RemoteModuleConfig;
import com.androidstarterkit.exception.CommandException;
import com.androidstarterkit.file.BuildGradle;
import com.androidstarterkit.file.ProguardRules;
import com.androidstarterkit.file.SettingsGradle;
import com.androidstarterkit.model.Config;
import com.androidstarterkit.model.Layout;
import com.androidstarterkit.model.Module;
import com.androidstarterkit.tool.ClassInfo;
import com.androidstarterkit.tool.ClassParser;
import com.androidstarterkit.tool.ModuleLoader;
import com.androidstarterkit.tool.XmlEditor;
import com.androidstarterkit.util.FileUtils;
import com.androidstarterkit.util.PrintUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class SourceDirectory extends Directory {

  private RemoteDirectory remoteDirectory;

  private String javaPath;
  private String resPath;
  private String layoutPath;

  private BuildGradle projectBuildGradle;
  private ProguardRules proguardRules;

  private XmlEditor xmlEditor;
  private ModuleLoader moduleLoader;

  private List<ClassInfo> transformedClassInfos;

  private TabType tabType;
  private List<Layout> layouts;
  private List<Module> modules;

  private SourceDirectory(String projectPathname, String modulePathname) {
    super(modulePathname
        , new String[]{"java", "gradle", "xml"}
        , new String[]{"build", "libs", "test", "androidTest", "res"});
    transformedClassInfos = new ArrayList<>();

    remoteDirectory = new RemoteDirectory();
    xmlEditor = new XmlEditor(this, remoteDirectory);

    javaPath = FileUtils.linkPathWithSlash(modulePathname, "src/main/java", applicationId.replaceAll("\\.", "/"));
    resPath = FileUtils.linkPathWithSlash(modulePathname, "src/main/res");
    layoutPath = FileUtils.linkPathWithSlash(resPath, ResourceType.LAYOUT.toString());

    projectBuildGradle = new BuildGradle(projectPathname);
    proguardRules = new ProguardRules(modulePathname);

    moduleLoader = new ModuleLoader();
  }

  /**
   * Begin a set source project root path
   *
   * @param pathname
   * @return Source instance which has home path
   */
  public static SourceDirectory load(String pathname) {
    String appPathname;
    if (pathname == null) {
      pathname = FileUtils.getRootPath();
      appPathname = FileUtils.linkPathWithSlash(FileUtils.getRootPath(), AskConfig.DEFAULT_SAMPLE_MODULE_NAME);
    } else {
      SettingsGradle settingsGradleFile = new SettingsGradle(new File(FileUtils.linkPathWithSlash(
          pathname, SettingsGradle.FILE_NAME)));
      appPathname = FileUtils.linkPathWithSlash(pathname, settingsGradleFile.getAppModuleName());
    }

    return new SourceDirectory(pathname, appPathname);
  }

  /**
   * Set tabtype and widget list to transformFile
   * * @param tabType Type for SlidingTabLayout
   * @param layoutCommands Layouts for imported fragments
   * @return Source instance after loading default Activity
   */
  public SourceDirectory with(TabType tabType, List<String> layoutCommands, List<String> moduleCommmands) {
    this.tabType = tabType;

    Gson gson = new Gson();
    AskJson askJson;
    try {
      askJson = gson.fromJson(FileUtils.readFile(
          FileUtils.linkPathWithSlash(remoteDirectory.getRelativePathFromJavaDir(AskJson.FILE_NAME), AskJson.FILE_NAME))
          , AskJson.class);
    } catch (IOException e) {
      throw new CommandException(CommandException.NOT_FOUND_ASK_JSON);
    }

    layouts = new ArrayList<>();
    layouts.addAll(layoutCommands.stream()
        .map(askJson::getLayoutClass)
        .collect(Collectors.toList()));

    modules = new ArrayList<>();
    modules.addAll(moduleCommmands.stream()
        .map(askJson::getModuleClass)
        .collect(Collectors.toList()));

    for (Module module : modules) {
      if (module.getConfigs() != null) {
        for (Config config : module.getConfigs()) {
          if (config.getFileFullName().equals(BuildGradle.FILE_NAME)) {
            if (config.getPath().equals(Config.ROOT_PATH)) {
              projectBuildGradle.setCodeBlocks(config.getCodeBlocks());
              moduleLoader.addCodeGenerator(projectBuildGradle);
            } else if (config.getPath().equals(Config.APP_PATH)) {
              appBuildGradleFile.setCodeBlocks(config.getCodeBlocks());
              moduleLoader.addCodeGenerator(appBuildGradleFile);
            }
          } else if (config.getFileFullName().equals(ProguardRules.FILE_NAME)) {
            proguardRules.setCodeBlocks(config.getCodeBlocks());
            moduleLoader.addCodeGenerator(projectBuildGradle);
          }
        }
      }
    }

    return this;
  }

  /**
   * Get started to transform AndroidManifest for source project
   */
  public void transform() {
    System.out.println("Analyzing Source Project...");
    System.out.println("package : " + androidManifestFile.getPackageName());
    System.out.println("Main Activity : " + androidManifestFile.getMainActivityName());
    System.out.println("src path : " + javaPath);
    System.out.println("layout path : " + layoutPath);
    System.out.println();

    try {
      xmlEditor.importAttrsOfRemoteMainActivity(() -> {
        final String mainActivityName = remoteDirectory.getMainActivityName();
        final File moduleMainActivityFile = remoteDirectory.getChildFile(mainActivityName, Extension.JAVA);

//        transformFile(0, moduleMainActivityFile, tabType, widgets);
      });

      moduleLoader.generateCode();

      System.out.println("Import Successful!");
      System.out.println("Project path : " + getPath());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * If depth is 0, file is main Activity of askModule
   *
   * @param depth
   * @param file
   * @param tabType
   * @param wigets
   */
  private void transformFile(int depth
      , File file
      , TabType tabType
      , List<WidgetType> wigets) throws CommandException {
    String moduleFileName = file.getName();
    String moduleFilePath;

    if (depth == 0) {
      moduleFilePath = FileUtils.linkPathWithSlash(javaPath
          , remoteDirectory.getRelativePathFromJavaDir(moduleFileName)
          , androidManifestFile.getMainActivityName() + Extension.JAVA);
    } else {
      moduleFilePath = FileUtils.linkPathWithSlash(javaPath
          , remoteDirectory.getRelativePathFromJavaDir(moduleFileName)
          , moduleFileName);
    }

    System.out.println(PrintUtils.prefixDash(depth) + moduleFileName);

    Scanner scanner;
    try {
      scanner = new Scanner(file);
    } catch (FileNotFoundException e) {
      throw new CommandException(CommandException.FILE_NOT_FOUND, file.getName());
    }
    List<String> codeLines = new ArrayList<>();
    List<ClassInfo> addedPackageClasses = new ArrayList<>();

    while (scanner.hasNext()) {
      String codeLine = scanner.nextLine();

      codeLine = changePackage(codeLine);

      if (depth == 0) {
        codeLine = changeMainActivityName(codeLine, moduleFileName);
        codeLine = changeMainFragmentByOptions(codeLine, tabType, wigets);
      } else {
        codeLine = importFragments(codeLine, wigets);
      }

      codeLine = importDeclaredClasses(codeLine, depth, wigets, addedPackageClasses);

      xmlEditor.importResourcesForJava(codeLine, depth);

      codeLines.add(codeLine);
    }

    codeLines = defineImportClasses(codeLines, addedPackageClasses);

    FileUtils.writeFile(new File(moduleFilePath), codeLines);

    if (depth == 0) {
      System.out.println();
    }
  }

  private List<String> defineImportClasses(List<String> codeLines, List<ClassInfo> addedPackageClassInfos) {
    List<String> importedClassStrings = new ArrayList<>();
    for (int i = 0, li = addedPackageClassInfos.size(); i < li; i++) {
      String className = addedPackageClassInfos.get(i).getName();
      if (remoteDirectory.getRelativePathFromJavaDir(className + Extension.JAVA.toString()) != null) {
        String importedClassString = SyntaxConstraints.IDENTIFIER_IMPORT + " "
            + applicationId + "."
            + remoteDirectory.getRelativePathFromJavaDir(className + Extension.JAVA.toString()).replaceAll("/", ".") + "."
            + className + ";";

        if (!importedClassStrings.contains(importedClassString)) {
          importedClassStrings.add(importedClassString);
        }
      }
    }

    for (int i = 0, li = codeLines.size(); i < li; i++) {
      String line = codeLines.get(i);

      boolean isLastIndex = i + 1 < li;
      if (line.contains(SyntaxConstraints.IDENTIFIER_PACKAGE) && isLastIndex) {
        codeLines.addAll(i + 1, importedClassStrings);
        break;
      }
    }
    return codeLines;
  }

  private String changeMainActivityName(String codeLine, String moduleActivityName) {
    return codeLine.replace(FileUtils.removeExtension(moduleActivityName)
        , getMainActivityName());
  }

  private String changeMainFragmentByOptions(String codeLine
      , TabType tabType
      , List<WidgetType> wigets) throws CommandException {
    if (tabType != null) {
      return codeLine.replace(RemoteModuleConfig.DEFAULT_MODULE_FRAGMENT_NAME, tabType.getFragmentName());
    } else {
      if (codeLine.contains(RemoteModuleConfig.DEFAULT_MODULE_FRAGMENT_NAME)) {
        return codeLine.replace(RemoteModuleConfig.DEFAULT_MODULE_FRAGMENT_NAME, wigets.get(0).getFragmentName());
      } else {
        return codeLine;
      }
    }
  }

  private String importFragments(String codeLine, List<WidgetType> widgets) {
    if (codeLine.contains("fragmentInfos.set")) {
      return "";
    }

    if (codeLine.contains("List<FragmentInfo> fragmentInfos = new ArrayList<>();") && widgets.size() > 0) {
      final String ADD_FRAGMENT_STRING = "fragmentInfos.set(new FragmentInfo(" + SyntaxConstraints.REPLACE_STRING + ".class));";

      final String intent = FileUtils.getIndentOfLine(codeLine);

      for (WidgetType widget : widgets) {
        codeLine += "\n";
        codeLine += intent + ADD_FRAGMENT_STRING.replace(SyntaxConstraints.REPLACE_STRING, widget.getFragmentName());
      }
      return codeLine;
    }
    return codeLine;
  }

  private String changePackage(String codeLine) {
    if (codeLine.contains("package")) {
      return codeLine.replace(remoteDirectory.getApplicationId(), getApplicationId());
    }

    return codeLine;
  }

  private String importDeclaredClasses(String codeLine
      , int depth
      , List<WidgetType> widgets
      , List<ClassInfo> addedPackageClassInfos) throws CommandException {
    List<ClassInfo> classInfos = ClassParser.extractClasses(codeLine);
    addedPackageClassInfos.addAll(classInfos);

    classInfos = uniquifyClasses(classInfos);

    if (classInfos.size() > 0) {
      for (ClassInfo classInfo : classInfos) {
        if (remoteDirectory.getRelativePathFromJavaDir(classInfo.getName() + Extension.JAVA.toString()) != null) {
          transformedClassInfos.add(classInfo);

          transformFile(depth + 1, remoteDirectory.getChildFile(classInfo.getName(), Extension.JAVA), null, widgets);
        } else {
          for (String dependencyKey : externalLibrary.getKeys()) {
            if (classInfo.getName().equals(dependencyKey)) {
              appBuildGradleFile.addDependency(externalLibrary.getInfo(dependencyKey).getLibrary());
              androidManifestFile.addPermissions(externalLibrary.getInfo(dependencyKey).getPermissions());
              break;
            }
          }
        }
      }
    }

    return codeLine.replace(remoteDirectory.getApplicationId(), getApplicationId());
  }

  private List<ClassInfo> uniquifyClasses(List<ClassInfo> extractedClassInfos) {
    return uniquifyClasses(null, extractedClassInfos);
  }

  private List<ClassInfo> uniquifyClasses(List<ClassInfo> uniquifiedClassInfos, List<ClassInfo> extractedClassInfos) {
    if (uniquifiedClassInfos == null) {
      uniquifiedClassInfos = new ArrayList<>();
    }

    if (extractedClassInfos.size() <= 0) {
      return uniquifiedClassInfos;
    }

    ClassInfo classInfo = extractedClassInfos.get(0);
    boolean isExisted = false;

    for (ClassInfo transformedClassInfo : transformedClassInfos) {
      if (transformedClassInfo.equals(classInfo)) {
        isExisted = true;
      }
    }

    extractedClassInfos.remove(classInfo);

    if (!isExisted) {
      uniquifiedClassInfos.add(classInfo);
    }

    return uniquifyClasses(uniquifiedClassInfos, extractedClassInfos);
  }

  public String getJavaPath() {
    return javaPath;
  }

  public String getResPath() {
    return resPath;
  }

  public String getLayoutPath() {
    return layoutPath;
  }
}
