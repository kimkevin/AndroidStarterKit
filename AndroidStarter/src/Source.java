import java.io.*;
import java.util.Scanner;

public class Source {
  private static Source source;
  private AndroidModule module;

  private final String homePath;
  private String sourceDirPath;
  private String layoutDirPath;
  private String appModuleDirPath;

  private String activityName;
  private String applicationId;

  public Source(String homePath) {
    this.homePath = homePath;

    module = new AndroidModule();

    /**
     * Get module's name
     */
    String appModuleName = null;
    File settingsGradleFile = FileUtils.getFileInDirectory(homePath, FileNames.SETTINGS_GRADLE);
    if (settingsGradleFile.exists()) {
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
    }

    if (appModuleName != null) {
      appModuleDirPath = FileUtils.linkPathWithSlash(homePath, appModuleName);
      System.out.println("Success to find module name : " + appModuleName);
    } else {
      System.out.println("Failed to find module name");
      return;
    }

    /**
     * Get package path from build.gradle
     */
    String packagePath = null;

    File buildGradleFile = FileUtils.getFileInDirectory(FileUtils.linkPathWithSlash(homePath, appModuleName), FileNames.BUILD_GRADLE);
    if (buildGradleFile.exists()) {
      try {
        Scanner scanner = new Scanner(buildGradleFile);

        while (scanner.hasNextLine()) {
          String line = scanner.nextLine();

          if (line.contains("applicationId")) {
            applicationId = FileUtils.getStringBetweenQuotes(line);
            packagePath = applicationId.replaceAll("\\.", "/");
            break;
          }
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }

    if (packagePath != null) {
      System.out.println("Success to find package path : " + packagePath);
    } else {
      System.out.println("Failed to find package path");
      return;
    }

    sourceDirPath = FileUtils.linkPathWithSlash(homePath, appModuleName, "src/main/java", packagePath);
    layoutDirPath = FileUtils.linkPathWithSlash(homePath, appModuleName, "src/main/res/layout");

    /**
     * Get name of default Activity
     */
    File sourceDir = new File(sourceDirPath);
    for (int i = 0, li = sourceDir.list().length; i < li; i++) {
      if (sourceDir.list()[i].contains(".java")) {
        activityName = sourceDir.list()[i].replace(".java", "");
      }
    }

    if (activityName == null) {
      System.out.println("Failed to find Default Activity");
    } else {
      System.out.println("Success to find Default Activity : " + activityName);
    }
  }

  /**
   * Begin a set source project root path
   *
   * @param rootPath is the home path of source project
   * @return Source instance which has home path
   */
  public static Source load(String rootPath) {
    if (source == null) {
      synchronized (Source.class) {
        if (source == null) {
          source = new Source(rootPath);
        }
      }
    }

    return source;
  }

  /**
   * Ready a load with Source by passing module type
   *
   * @param widgetType {@link WidgetType} is supported by AndroidModule
   * @return Source instance after loading default Activity
   */
  public Source with(WidgetType widgetType) {
    if (source.homePath == null) {
      throw new NullPointerException("Failed : There is no HOME PATH");
    }

    try {
      String lines = transferActivity(widgetType.getFileName());
      FileUtils.writeFile(FileUtils.getFileInDirectory(source.sourceDirPath, activityName + ".java"), lines);
    } catch (UnsupportedWidgetTypeException e) {
      e.printStackTrace();
    }

    return source;
  }

  /**
   * Copy a module file to a source project
   *
   * @param fileName is a name of module file
   * @return Source instance after copying file to source project
   */
  public Source put(String fileName) {
    try {
      switch (fileName) {
        case FileNames.COFFEE_TYPE:
          FileUtils.copyFile(module.getPath(fileName), source.sourceDirPath, fileName);
          FileUtils.changeAppplicationId(
                  FileUtils.linkPathWithSlash(source.sourceDirPath, fileName),
                  source.applicationId);
          break;
        case FileNames.ACTIVITY_RECYCLERVIEW_XML:
        case FileNames.ACTIVITY_LISTVIEW_XML:
        case FileNames.LAYOUT_LIST_ITEM_XML:
          FileUtils.copyFile(module.getPath(fileName), source.layoutDirPath, fileName);
          break;
        case FileNames.RECYCLERVIEW_ADAPTER:
        case FileNames.LISTVIEW_ADAPTER:
          FileUtils.copyFile(module.getPath(fileName), source.sourceDirPath + "/adapter", fileName);
          FileUtils.changeAppplicationId(
                  FileUtils.linkPathWithSlash(source.sourceDirPath, "adapter", fileName),
                  source.applicationId);
          break;
        case FileNames.BUILD_GRADLE:
          BuildGradleFile buildGradleFile = new BuildGradleFile(source.appModuleDirPath);
          buildGradleFile.addDependency(BuildGradleConfig.RECYCLERVIEW_LIBRARY,
                  BuildGradleConfig.CARDVIEW_LIBRARY);
          break;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return source;
  }

  /**
   * Transfer source Activity to module Activity with source application id and file name
   * @param moduleActivityName the Activity name of module
   * @return the content of activity file
   */
  private String transferActivity(String moduleActivityName) {
    File activityFile = FileUtils.getFileInDirectory(module.getPath(moduleActivityName), moduleActivityName);

    String content = "";
    try {
      Scanner scanner = new Scanner(activityFile);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();

        line = line.replace(AndroidModule.APPLICATION_ID, source.applicationId);
        line = line.replace(moduleActivityName.replace(".java", ""), activityName);
        content += line + "\n";
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return content;
  }
}
