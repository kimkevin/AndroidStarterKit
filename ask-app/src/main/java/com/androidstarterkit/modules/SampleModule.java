package com.androidstarterkit.modules;

import com.androidstarterkit.ClassParser;
import com.androidstarterkit.UnsupportedWidgetTypeException;
import com.androidstarterkit.cmd.WidgetType;
import com.androidstarterkit.utils.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SampleModule extends Directory {
  private AskModule module;
  private String mainActivityName;

  private String javaPath;
  private String layoutPath;

  public SampleModule(String pathname) {
    super(pathname, new String[]{"java", "gradle", "xml"},
        new String[]{"build", "libs", "test", "androidTest", "res"});

    module = new AskModule();

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
   * @param rootPath is the home path of source project
   * @return Source instance which has home path
   */
  public static SampleModule load(String rootPath) {
    return new SampleModule(rootPath);
  }

  /**
   * Ready a load with Source by passing module type
   *
   * @param widgetType {@link WidgetType} is supported by AndroidModule
   * @return Source instance after loading default Activity
   */
  public SampleModule with(WidgetType widgetType) {
    try {
      File activityFile = module.getChildFile(widgetType.getActivityName() + JAVA_EXTENSION);

      transfer(activityFile, widgetType);
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
          activityName = FileUtil.getFileNameForDotPath(dotPath) + JAVA_EXTENSION;
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

  private void transfer(File file, WidgetType widgetType) {
    try {
      Scanner scanner = new Scanner(file);
      String content = "";

      while (scanner.hasNext()) {
        String line = scanner.nextLine();

        if (widgetType != null) {
          line = changeActivityName(widgetType, line);
        }

        line = changePackage(line);
        line = importClass(line);
        line = importLayout(line);

        content += line + "\n";
      }

      if (widgetType != null) {
        FileUtil.writeFile(new File(javaPath + "/" + mainActivityName), content);
      } else {
        FileUtil.writeFile(javaPath + module.getRelativePathFromJavaDir(file.getName()), file.getName(), content);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
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

  private String importClass(String line) {
    List<String> classList = ClassParser.getClassNames(line);

    if (classList.size() > 0) {
      for (String key : classList) {
        if (module.getRelativePathFromJavaDir(key + JAVA_EXTENSION) != null) {
          transfer(module.getChildFile(key + JAVA_EXTENSION), null);
        } else {
          for (String dependency : buildGradleFile.getDependencyKeys()) {
            if (key.contains(dependency)) {
              buildGradleFile.addDependency(dependency);
              break;
            }
          }
        }
      }
    }

    return line.replace(module.getApplicationId(), getApplicationId());
  }

  private String importLayout(String line) {
    String reg = "R.layout.[A-Za-z0-1_]*";

    Pattern pat = Pattern.compile(reg);
    Matcher matcher = pat.matcher(line);

    while (matcher.find()) {
      final String layoutName = matcher.group()
          .replace(";", "")
          .replace("R.layout.", "");

      try {
        File moduleLayoutFile = module.getChildFile(layoutName + Directory.XML_EXTENSION);
        FileUtil.copyFile(moduleLayoutFile, layoutPath);

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

        for (String key : buildGradleFile.getDependencyKeys()) {
          if (line.contains(key)) {
            buildGradleFile.addDependency(key);
            break;
          }
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}
