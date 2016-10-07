package com.androidstarterkit.modules;

import com.androidstarterkit.ClassParser;
import com.androidstarterkit.Extension;
import com.androidstarterkit.UnsupportedWidgetTypeException;
import com.androidstarterkit.cmd.WidgetType;
import com.androidstarterkit.utils.FileUtil;
import com.androidstarterkit.utils.PrintUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SampleModule extends Directory {
  private static final String DEFAULT_SAMPLE_MODULE_NAME = "ask-sample";
  private AskModule module;
  private String mainActivityName;

  private String javaPath;
  private String layoutPath;

  private List<String> importedClasses;

  public SampleModule(String pathname) {
    super(pathname,
        new String[] { "java", "gradle", "xml" },
        new String[] { "build", "libs", "test", "androidTest", "res" });

    module = new AskModule();
    importedClasses = new ArrayList<>();

    mainActivityName = findMainActivty();

    System.out.println("Analyzing Sample Project...");
    System.out.println("applicationId : " + applicationId);
    System.out.println("Main Activity : " + mainActivityName);

    String modulePath;
    try {
      modulePath = FileUtil.linkPathWithSlash(getChildPath(SETTINGS_GRADLE_FILE), findModuleName());
    } catch (FileNotFoundException e) {
      modulePath = getPath();
    }

    javaPath = FileUtil.linkPathWithSlash(modulePath, "src/main/java", applicationId.replaceAll("\\.", "/"));
    layoutPath = FileUtil.linkPathWithSlash(modulePath, "src/main/res/layout");

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
  public static SampleModule load(String projectPath) {
    if (projectPath == null) {
      projectPath = FileUtil.linkPathWithSlash(FileUtil.getRootPath(), DEFAULT_SAMPLE_MODULE_NAME);
    }

    return new SampleModule(projectPath);
  }

  /**
   * Ready a load with Source by passing module type
   *
   * @param widgetType {@link WidgetType} is supported by AndroidModule
   * @return Source instance after loading default Activity
   */
  public SampleModule with(WidgetType widgetType) {
    try {
      File activityFile = module.getChildFile(widgetType.getActivityName(), Extension.JAVA);

      transfer(0, activityFile, widgetType);
    } catch (UnsupportedWidgetTypeException e) {
      e.printStackTrace();
    }

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
      throw new FileNotFoundException("Failed to find module name");
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

  private void transfer(int depth, File file, WidgetType widgetType) {
    String filePath;
    if (widgetType != null) {
      filePath = FileUtil.linkPathWithSlash(javaPath, mainActivityName);
    } else {
      filePath = FileUtil.linkPathWithSlash(javaPath, module.getRelativePathFromJavaDir(file.getName()), file.getName());
    }

    try {
      System.out.println(PrintUtil.prefixDash(depth) + "Transfering file : " + filePath);

      Scanner scanner = new Scanner(file);
      String content = "";

      while (scanner.hasNext()) {
        String line = scanner.nextLine();

        if (widgetType != null) {
          line = changeActivityName(widgetType, line);
        }

        line = changePackage(line);
        line = importClass(line, depth);
        line = importLayout(line, depth);

        content += line + "\n";
      }

      FileUtil.writeFile(new File(filePath), content);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    if (depth == 0) {
      System.out.println();
    }
  }

  private String changeActivityName(WidgetType widgetType, String line) {
    try {
      return line.replace(widgetType.getActivityName(), FileUtil.removeExtension(mainActivityName));
    } catch (UnsupportedWidgetTypeException e) {
      e.printStackTrace();
    }
    return line;
  }

  private String changePackage(String line) {
    if (line.contains("package")) {
      return line.replace(module.getApplicationId(), getApplicationId());
    }

    return line;
  }

  private String importClass(String line, int depth) {
    List<String> classNames = ClassParser.getClassNames(line);

    classNames = filterClasses(classNames);

    if (classNames.size() > 0) {
      for (String className : classNames) {
        if (module.getRelativePathFromJavaDir(className + Extension.JAVA.getName()) != null) {
          importedClasses.add(className);

          transfer(depth + 1, module.getChildFile(className, Extension.JAVA), null);
        } else {
          for (String dependency : buildGradleFile.getDependencyKeys()) {
            if (className.equals(dependency)) {
              buildGradleFile.addDependency(dependency);
              break;
            }
          }
        }
      }
    }

    return line.replace(module.getApplicationId(), getApplicationId());
  }

  private String importLayout(String line, int depth) {
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

        System.out.println(PrintUtil.prefixDash(depth) + "Transfering xml file : " + layoutPath + "/" + moduleLayoutFile.getName());

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

        for (String dependency : buildGradleFile.getDependencyKeys()) {
          if (line.contains(dependency)) {
            buildGradleFile.addDependency(dependency);
            break;
          }
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  private List<String> filterClasses(List<String> classNames) {
    List<String> filteredClasses = new ArrayList<>();

    for (String className : classNames) {
      if (!importedClasses.contains(className)) {
        filteredClasses.add(className);
      }
    }

    return filteredClasses;
  }
}
