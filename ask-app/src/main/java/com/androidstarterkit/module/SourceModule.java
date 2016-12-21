package com.androidstarterkit.module;

import com.androidstarterkit.config.AskConfig;
import com.androidstarterkit.android.api.Extension;
import com.androidstarterkit.android.api.resource.ResourceType;
import com.androidstarterkit.command.TabType;
import com.androidstarterkit.command.WidgetType;
import com.androidstarterkit.config.RemoteModuleConfig;
import com.androidstarterkit.SyntaxConstraints;
import com.androidstarterkit.exception.CommandException;
import com.androidstarterkit.file.SettingsGradle;
import com.androidstarterkit.tool.ClassParser;
import com.androidstarterkit.tool.XmlEditor;
import com.androidstarterkit.util.FileUtils;
import com.androidstarterkit.util.PrintUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SourceModule extends Directory {

  private RemoteModule remoteModule;

  private String javaPath;
  private String resPath;
  private String layoutPath;

  private XmlEditor xmlEditor;

  private List<String> filteredClassNames;
  private TabType tabType;
  private List<WidgetType> widgets;

  public SourceModule(String modulePathname) {
    super(modulePathname
        , new String[]{"java", "gradle", "xml"}
        , new String[]{"build", "libs", "test", "androidTest", "res"});
    filteredClassNames = new ArrayList<>();

    remoteModule = new RemoteModule();
    xmlEditor = new XmlEditor(this, remoteModule);

    javaPath = FileUtils.linkPathWithSlash(modulePathname, "src/main/java", applicationId.replaceAll("\\.", "/"));
    resPath = FileUtils.linkPathWithSlash(modulePathname, "src/main/res");
    layoutPath = FileUtils.linkPathWithSlash(resPath, ResourceType.LAYOUT.toString());
  }

  /**
   * Begin a set source project root path
   *
   * @param projectPathname is the home path of source project
   * @return Source instance which has home path
   */
  public static SourceModule load(String projectPathname) {
    String appModulePathname;
    if (projectPathname == null) {
      String appModuleName = AskConfig.DEFAULT_SAMPLE_MODULE_NAME;
      appModulePathname = FileUtils.linkPathWithSlash(FileUtils.getRootPath(), appModuleName);
    } else {
      SettingsGradle settingsGradleFile = new SettingsGradle(
          new File(FileUtils.linkPathWithSlash(projectPathname, SettingsGradle.FILE_NAME)));
      String appModuleName = settingsGradleFile.getAppModuleName();
      appModulePathname = FileUtils.linkPathWithSlash(projectPathname, appModuleName);
    }

    return new SourceModule(appModulePathname);
  }

  /**
   * Set tabtype and widget list to transformFile
   *
   * @param tabType Type for SlidingTabLayout
   * @param widgets Widgets for imported fragments
   * @return Source instance after loading default Activity
   */
  public SourceModule with(TabType tabType, List<WidgetType> widgets) {
    this.tabType = tabType;
    this.widgets = widgets;

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
        final String mainActivityName = remoteModule.getMainActivityName();
        final File moduleMainActivityFile = remoteModule.getChildFile(mainActivityName, Extension.JAVA);

        transformFile(0, moduleMainActivityFile, tabType, widgets);
      });

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
          , remoteModule.getRelativePathFromJavaDir(moduleFileName)
          , androidManifestFile.getMainActivityName() + Extension.JAVA);
    } else {
      moduleFilePath = FileUtils.linkPathWithSlash(javaPath
          , remoteModule.getRelativePathFromJavaDir(moduleFileName)
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
    List<String> addedPackageClasses = new ArrayList<>();

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

  private List<String> defineImportClasses(List<String> codeLines, List<String> addedPackageClassNames) {
    List<String> importedClassStrings = new ArrayList<>();
    for (int i = 0, li = addedPackageClassNames.size(); i < li; i++) {
      String className = addedPackageClassNames.get(i);
      if (remoteModule.getRelativePathFromJavaDir(className + Extension.JAVA.toString()) != null) {
        String importedClassString = SyntaxConstraints.IDENTIFIER_IMPORT + " "
            + applicationId + "."
            + remoteModule.getRelativePathFromJavaDir(className + Extension.JAVA.toString()) + "."
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
    if (codeLine.contains("fragmentInfos.add")) {
      return "";
    }

    if (codeLine.contains("List<FragmentInfo> fragmentInfos = new ArrayList<>();") && widgets.size() > 0) {
      final String ADD_FRAGMENT_STRING = "fragmentInfos.add(new FragmentInfo(" + SyntaxConstraints.REPLACE_STRING + ".class));";

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
      return codeLine.replace(remoteModule.getApplicationId(), getApplicationId());
    }

    return codeLine;
  }

  private String importDeclaredClasses(String codeLine
      , int depth
      , List<WidgetType> widgets
      , List<String> addedPackageClasses) throws CommandException {
    List<String> classNames = ClassParser.getClassNames(codeLine);
    addedPackageClasses.addAll(classNames);

    classNames = filterClasses(classNames);

    if (classNames.size() > 0) {
      for (String className : classNames) {
        if (remoteModule.getRelativePathFromJavaDir(className + Extension.JAVA.toString()) != null) {
          filteredClassNames.add(className);

          transformFile(depth + 1, remoteModule.getChildFile(className, Extension.JAVA), null, widgets);
        } else {
          for (String dependencyKey : externalLibrary.getKeys()) {
            if (className.equals(dependencyKey)) {
              buildGradleFile.addDependency(externalLibrary.getInfo(dependencyKey).getLibrary());
              androidManifestFile.addPermissions(externalLibrary.getInfo(dependencyKey).getPermissions());
              break;
            }
          }
        }
      }
    }

    return codeLine.replace(remoteModule.getApplicationId(), getApplicationId());
  }

  private List<String> filterClasses(List<String> classNames) {
    List<String> newFilteredClasses = new ArrayList<>();

    for (String className : classNames) {
      if (!filteredClassNames.contains(className)) {
        newFilteredClasses.add(className);
      }
    }

    return newFilteredClasses;
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
