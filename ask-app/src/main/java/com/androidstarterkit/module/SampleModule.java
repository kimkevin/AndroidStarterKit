package com.androidstarterkit.module;

import com.androidstarterkit.ClassParser;
import com.androidstarterkit.CommandException;
import com.androidstarterkit.Extension;
import com.androidstarterkit.command.TabType;
import com.androidstarterkit.command.WidgetType;
import com.androidstarterkit.config.SyntaxConfig;
import com.androidstarterkit.util.FileUtil;
import com.androidstarterkit.util.PrintUtil;

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
  private String layoutPath;

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
      sampleModulePath = FileUtil.linkPathWithSlash(getChildPath(SETTINGS_GRADLE_FILE), findModuleName());
    } catch (FileNotFoundException e) {
      sampleModulePath = getPath();
    }

    javaPath = FileUtil.linkPathWithSlash(sampleModulePath, "src/main/java", applicationId.replaceAll("\\.", "/"));
    layoutPath = FileUtil.linkPathWithSlash(sampleModulePath, "src/main/res/layout");

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
      projectPath = FileUtil.linkPathWithSlash(FileUtil.getRootPath(), DEFAULT_SAMPLE_MODULE_NAME);
    } else {
      projectPath = FileUtil.linkPathWithSlash(projectPath, findAppModuleName(projectPath));
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

          dotPath = FileUtil.getStringBetweenQuotes(line);
        }

        if (isFoundActivity && line.contains("android.intent.action.MAIN")) {
          activityName = FileUtil.getFileNameForDotPath(dotPath) + Extension.JAVA.getName();
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
      moduleFilePath = FileUtil.linkPathWithSlash(javaPath, module.getRelativePathFromJavaDir(moduleFileName), mainActivityName);
    } else {
      moduleFilePath = FileUtil.linkPathWithSlash(javaPath, module.getRelativePathFromJavaDir(moduleFileName), moduleFileName);
    }

    System.out.println(PrintUtil.prefixDash(depth) + "Transfering : " + moduleFileName);

    Scanner scanner;
    try {
      scanner = new Scanner(file);
    } catch (FileNotFoundException e) {
      throw new CommandException(CommandException.FILE_NOT_FOUND, file.getName());
    }
    List<String> lines = new ArrayList<>();
    List<String> addedPackageClasses = new ArrayList<>();

    while (scanner.hasNext()) {
      String line = scanner.nextLine();

      if (depth == 0) {
        line = changeActivityName(moduleFileName, line);
        line = changeFragment(tabType, wigets, line);
      } else {
        line = changeDetailFragmentByArgs(line, wigets);
      }

      line = changePackage(line);

      line = extractClass(line, depth, wigets, addedPackageClasses);
      line = extractLayout(line, depth);

      lines.add(line);
    }

    lines = importClasses(lines, addedPackageClasses);

    FileUtil.writeFile(new File(moduleFilePath), lines);

    if (depth == 0) {
      System.out.println();
    }
  }

  private List<String> importClasses(List<String> lines, List<String> addedPackageClassNames) {
    List<String> importedClassStrings = new ArrayList<>();
    for (int i = 0, li = addedPackageClassNames.size(); i < li; i++) {
      String className = addedPackageClassNames.get(i);
      if (module.getRelativePathFromJavaDir(className + Extension.JAVA.getName()) != null) {
        String importedClassString = SyntaxConfig.IDENTIFIER_IMPORT + " "
            + applicationId + "."
            + module.getRelativePathFromJavaDir(className + Extension.JAVA.getName()) + "."
            + className + ";";

        if (!importedClassStrings.contains(importedClassString)) {
          importedClassStrings.add(importedClassString);
        }
      }
    }

    for (int i = 0, li = lines.size(); i < li; i++) {
      String line = lines.get(i);

      boolean isLastIndex = i + 1 < li;
      if (line.contains(SyntaxConfig.IDENTIFIER_PACKAGE) && isLastIndex) {
        lines.addAll(i + 1, importedClassStrings);
        break;
      }
    }
    return lines;
  }

  private String changeActivityName(String moduleActivityName, String line) {
    return line.replace(FileUtil.removeExtension(moduleActivityName),
        FileUtil.removeExtension(mainActivityName));
  }

  private String changeFragment(TabType tabType, List<WidgetType> wigets, String line) throws CommandException {
    if (tabType != null) {
      return line.replace(DEFAULT_SAMPLE_FRAGMENT_NAME, tabType.getFragmentName());
    } else {
      if (line.contains(DEFAULT_SAMPLE_FRAGMENT_NAME)) {
        return line.replace(DEFAULT_SAMPLE_FRAGMENT_NAME, wigets.get(0).getFragmentName());
      } else {
        return line;
      }
    }
  }

  private String changeDetailFragmentByArgs(String line, List<WidgetType> widgets) {
    if (line.contains("fragmentInfos.add")) {
      return "";
    }

    if (line.contains("List<FragmentInfo> fragmentInfos = new ArrayList<>();") && widgets.size() > 0) {
      final String ADD_FRAGMENT_STRING = "fragmentInfos.add(new FragmentInfo(" + SyntaxConfig.REPLACE_STRING + ".class));";

      final String intent = FileUtil.getIndentOfLine(line);

      for (WidgetType widget : widgets) {
        line += "\n";
        line += intent + ADD_FRAGMENT_STRING.replace(SyntaxConfig.REPLACE_STRING, widget.getFragmentName());
      }
      return line;
    }
    return line;
  }

  private String changePackage(String line) {
    if (line.contains("package")) {
      return line.replace(module.getApplicationId(), getApplicationId());
    }

    return line;
  }

  private String extractClass(String line, int depth, List<WidgetType> widgets, List<String> addedPackageClasses) throws CommandException {
    List<String> classNames = ClassParser.getClassNames(line);
    addedPackageClasses.addAll(classNames);

    classNames = filterClasses(classNames);

    if (classNames.size() > 0) {
      for (String className : classNames) {
        if (module.getRelativePathFromJavaDir(className + Extension.JAVA.getName()) != null) {
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

    return line.replace(module.getApplicationId(), getApplicationId());
  }

  private String extractLayout(String line, int depth) {
    String reg = "R.layout.[A-Za-z0-1_]*";

    Pattern pat = Pattern.compile(reg);
    Matcher matcher = pat.matcher(line);

    while (matcher.find()) {
      final String layoutName = matcher.group()
          .replace(";", "")
          .replace("R.layout.", "");

      try {
        File moduleLayoutFile = module.getChildFile(layoutName, Extension.XML);

        String newLines = "";
        Scanner scanner = new Scanner(moduleLayoutFile);
        while (scanner.hasNext()) {
          String xmlLine = scanner.nextLine()
              .replace(module.getApplicationId(), getApplicationId());

          newLines += xmlLine + "\n";
        }

        FileUtil.writeFile(layoutPath, moduleLayoutFile.getName(), newLines);

        System.out.println(PrintUtil.prefixDash(depth) + "Transfering : " + moduleLayoutFile.getName());

        scanDependencyInLayoutFile(moduleLayoutFile);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return line;
  }

  private void scanDependencyInLayoutFile(File moduleLayoutFile) {
    try {
      Scanner scanner = new Scanner(moduleLayoutFile);

      while (scanner.hasNext()) {
        String line = scanner.nextLine();

        for (String dependencyKey : externalLibrary.getKeys()) {
          if (line.contains(dependencyKey)) {
            buildGradleFile.addDependency(externalLibrary.getInfo(dependencyKey).getLibrary());
            break;
          }
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
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

    File settingsGradleFile = new File(FileUtil.linkPathWithSlash(projectPath,
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
}
