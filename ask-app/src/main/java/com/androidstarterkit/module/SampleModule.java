package com.androidstarterkit.module;

import com.androidstarterkit.ClassParser;
import com.androidstarterkit.CommandException;
import com.androidstarterkit.Extension;
import com.androidstarterkit.ResourcePattern;
import com.androidstarterkit.ResourceType;
import com.androidstarterkit.command.TabType;
import com.androidstarterkit.command.WidgetType;
import com.androidstarterkit.config.SyntaxConfig;
import com.androidstarterkit.editor.XmlEditor;
import com.androidstarterkit.util.FileUtils;
import com.androidstarterkit.util.PrintUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SampleModule extends Directory {
  public static final String DEFAULT_SAMPLE_ACTIVITY_NAME = "SampleActivity";
  public static final String DEFAULT_SAMPLE_FRAGMENT_NAME = "SampleFragment";
  public static final String DEFAULT_SAMPLE_MODULE_NAME = "ask-sample";

  private AskModule module;
  private String mainActivityName;

  private String javaPath;

  private XmlEditor xmlEditor;

  private List<String> filteredClassNames;

  public SampleModule(String projdectPath) throws CommandException {
    super(projdectPath,
        new String[]{"java", "gradle", "xml"},
        new String[]{"build", "libs", "test", "androidTest", "res"});

    module = new AskModule();
    filteredClassNames = new ArrayList<>();

    mainActivityName = findMainActivty();

    System.out.println("Analyzing Sample Project...");
    System.out.println("applicationId : " + applicationId);
    System.out.println("Main Activity : " + mainActivityName);

    String sampleModulePath;
    try {
      sampleModulePath = FileUtils.linkPathWithSlash(getChildPath(SETTINGS_GRADLE_FILE), findModuleName());
    } catch (FileNotFoundException e) {
      sampleModulePath = getPath();
    }

    javaPath = FileUtils.linkPathWithSlash(sampleModulePath, "src/main/java", applicationId.replaceAll("\\.", "/"));
    String resPath = FileUtils.linkPathWithSlash(sampleModulePath, "src/main/res");
    String layoutPath = FileUtils.linkPathWithSlash(resPath, ResourceType.LAYOUT.toString());

    xmlEditor = new XmlEditor(resPath, getApplicationId(), buildGradleFile, externalLibrary);

    System.out.println("src folder path : " + javaPath);
    System.out.println("layout folder path : " + layoutPath);

    System.out.println();
  }

  /**
   * Begin a set source project root path
   *
   * @param projectPath is the home path of source project
   * @return Source instance which has home path
   */
  public static SampleModule load(String projectPath) throws CommandException {
    if (projectPath == null) {
      projectPath = FileUtils.linkPathWithSlash(FileUtils.getRootPath(), DEFAULT_SAMPLE_MODULE_NAME);
    } else {
      projectPath = FileUtils.linkPathWithSlash(projectPath, findAppModuleName(projectPath));
    }

    return new SampleModule(projectPath);
  }

  /**
   * Ready a load with Source by passing module type
   *
   * @param tabType
   * @param wigets
   * @return Source instance after loading default Activity
   */
  public SampleModule with(TabType tabType, List<WidgetType> wigets) throws CommandException {
    File moduleMainActivity = module.getChildFile(DEFAULT_SAMPLE_ACTIVITY_NAME, Extension.JAVA);

    transfer(0, moduleMainActivity, tabType, wigets);

    System.out.println("Import Successful!");
    return this;
  }

  private String findMainActivty() {
    String activityName = null;
    String dotPath = null;

    try {
      Scanner scanner = new Scanner(getChildFile(ANDROID_MANIFEST_FILE));
      boolean isFoundActivity = false;

      while (scanner.hasNext()) {
        String line = scanner.nextLine();

        if (!isFoundActivity && line.contains("activity")) {
          isFoundActivity = true;

          dotPath = FileUtils.getStringBetweenQuotes(line);
        }

        if (isFoundActivity && line.contains("android.intent.action.MAIN")) {
          activityName = FileUtils.getFileNameForDotPath(dotPath) + Extension.JAVA.toString();
          break;
        }

        if (line.contains("</activity>")) {
          isFoundActivity = false;
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    return activityName;
  }

  private String findModuleName() throws FileNotFoundException {
    String appModuleName = null;
    File settingsGradleFile = getChildFile(SETTINGS_GRADLE_FILE);

    if (settingsGradleFile == null) {
      throw new FileNotFoundException();
    }

    try {
      Scanner scanner = new Scanner(settingsGradleFile);

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine().replace("\'", "").replace(":", "");

        String[] tokens = line.split(" ");
        appModuleName = tokens[1];
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    return appModuleName;
  }

  /**
   * If depth is 0, file is main Activity of module
   *
   * @param depth
   * @param file
   * @param tabType
   * @param wigets
   */
  private void transfer(int depth, File file, TabType tabType, List<WidgetType> wigets) throws CommandException {
    String moduleFileName = file.getName();
    String moduleFilePath;

    if (depth == 0) {
      moduleFilePath = FileUtils.linkPathWithSlash(javaPath, module.getRelativePathFromJavaDir(moduleFileName), mainActivityName);
    } else {
      moduleFilePath = FileUtils.linkPathWithSlash(javaPath, module.getRelativePathFromJavaDir(moduleFileName), moduleFileName);
    }

    System.out.println(PrintUtils.prefixDash(depth) + "Transfering : " + moduleFileName);

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
      codeLine = importXmlResources(codeLine, depth);
      codeLine = importValueElements(codeLine, depth);

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
      if (module.getRelativePathFromJavaDir(className + Extension.JAVA.toString()) != null) {
        String importedClassString = SyntaxConfig.IDENTIFIER_IMPORT + " "
            + applicationId + "."
            + module.getRelativePathFromJavaDir(className + Extension.JAVA.toString()) + "."
            + className + ";";

        if (!importedClassStrings.contains(importedClassString)) {
          importedClassStrings.add(importedClassString);
        }
      }
    }

    for (int i = 0, li = codeLines.size(); i < li; i++) {
      String line = codeLines.get(i);

      boolean isLastIndex = i + 1 < li;
      if (line.contains(SyntaxConfig.IDENTIFIER_PACKAGE) && isLastIndex) {
        codeLines.addAll(i + 1, importedClassStrings);
        break;
      }
    }
    return codeLines;
  }

  private String changeMainActivityName(String codeLine, String moduleActivityName) {
    return codeLine.replace(FileUtils.removeExtension(moduleActivityName),
        FileUtils.removeExtension(mainActivityName));
  }

  private String changeMainFragmentByOptions(String codeLine, TabType tabType, List<WidgetType> wigets) throws CommandException {
    if (tabType != null) {
      return codeLine.replace(DEFAULT_SAMPLE_FRAGMENT_NAME, tabType.getFragmentName());
    } else {
      if (codeLine.contains(DEFAULT_SAMPLE_FRAGMENT_NAME)) {
        return codeLine.replace(DEFAULT_SAMPLE_FRAGMENT_NAME, wigets.get(0).getFragmentName());
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
      final String ADD_FRAGMENT_STRING = "fragmentInfos.add(new FragmentInfo(" + SyntaxConfig.REPLACE_STRING + ".class));";

      final String intent = FileUtils.getIndentOfLine(codeLine);

      for (WidgetType widget : widgets) {
        codeLine += "\n";
        codeLine += intent + ADD_FRAGMENT_STRING.replace(SyntaxConfig.REPLACE_STRING, widget.getFragmentName());
      }
      return codeLine;
    }
    return codeLine;
  }

  private String changePackage(String codeLine) {
    if (codeLine.contains("package")) {
      return codeLine.replace(module.getApplicationId(), getApplicationId());
    }

    return codeLine;
  }

  private String importDeclaredClasses(String codeLine, int depth, List<WidgetType> widgets, List<String> addedPackageClasses) throws CommandException {
    List<String> classNames = ClassParser.getClassNames(codeLine);
    addedPackageClasses.addAll(classNames);

    classNames = filterClasses(classNames);

    if (classNames.size() > 0) {
      for (String className : classNames) {
        if (module.getRelativePathFromJavaDir(className + Extension.JAVA.toString()) != null) {
          filteredClassNames.add(className);

          transfer(depth + 1, module.getChildFile(className, Extension.JAVA), null, widgets);
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

    return codeLine.replace(module.getApplicationId(), getApplicationId());
  }

  private String importXmlResources(String codeLine, int depth) {
    Matcher matcher = ResourcePattern.matcherFileInJava(codeLine);

    while (matcher.find()) {
      final String resourceTypeName = matcher.group(1);
      final String layoutName = matcher.group(2);

      try {
        File moduleLayoutFile = module.getChildFile(layoutName, Extension.XML);
        xmlEditor.importLayout(resourceTypeName, layoutName, this, module);

        System.out.println(PrintUtils.prefixDash(depth) + "Transfering : " + moduleLayoutFile.getName());
      } catch (IOException | NullPointerException e) {
        e.printStackTrace();
      }
    }

    return codeLine;
  }

  private String importValueElements(String codeLine, int depth) {
    Matcher matcher = ResourcePattern.matcherValuesInJava(codeLine);

    while (matcher.find()) {
      final String resourceTypeName = matcher.group(1);
      final String layoutName = matcher.group(2);

      try {
        xmlEditor.importElement(resourceTypeName, layoutName, this, module);
      } catch (IOException | NullPointerException e) {
        e.printStackTrace();
      }
    }
    return codeLine;
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

  private static String findAppModuleName(String projectPath) throws CommandException {
    String appModuleName = null;

    File settingsGradleFile = new File(FileUtils.linkPathWithSlash(projectPath,
        SETTINGS_GRADLE_FILE));

    try {
      Scanner scanner = new Scanner(settingsGradleFile);

      String line = scanner.nextLine();
      String reg = "\\':([A-Za-z0-9_]+)\\'";

      Pattern pat = Pattern.compile(reg);
      Matcher matcher = pat.matcher(line);

      if (matcher.find()) {
        appModuleName = matcher.group(1);
      }
    } catch (FileNotFoundException e) {
      throw new CommandException(CommandException.APP_MODULE_NOT_FOUND, projectPath);
    }

    return appModuleName;
  }

  public String getMainActivityName() {
    return mainActivityName;
  }
}
