package com.androidstarterkit.modules;

import com.androidstarterkit.BuildGradleConfig;
import com.androidstarterkit.BuildGradleFile;
import com.androidstarterkit.FileNames;
import com.androidstarterkit.FileUtils;
import com.androidstarterkit.UnsupportedWidgetTypeException;

import java.io.*;
import java.util.Scanner;

public class SourceModule {
  private AskModule module;

  private final String homePath;
  private String sourceDirPath;
  private String layoutDirPath;
  private String appModuleDirPath;

  private String activityName;
  private String applicationId;

  public SourceModule(String homePath) {
    this.homePath = homePath;

    module = new AskModule();

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
      appModuleDirPath = FileUtils.linkPathWithSlash(homePath);
      File srcDir = new File(appModuleDirPath, "src");
      if (srcDir.exists() && srcDir.isDirectory()) {
        System.out.println("Success to find src folder");

      } else {
        System.out.println("Failed to find module name");
        return;
      }
    }

    /**
     * Get package path from build.gradle
     */
    String packagePath = null;

    File buildGradleFile = FileUtils.getFileInDirectory(appModuleDirPath, FileNames.BUILD_GRADLE);
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

    sourceDirPath = FileUtils.linkPathWithSlash(appModuleDirPath, "src/main/java", packagePath);
    layoutDirPath = FileUtils.linkPathWithSlash(appModuleDirPath, "src/main/res/layout");

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
  public static SourceModule load(String rootPath) {
    return new SourceModule(rootPath);
  }

  /**
   * Ready a load with Source by passing module type
   *
   * @param widgetType {@link com.androidstarterkit.cmd.WidgetType} is supported by AndroidModule
   * @return Source instance after loading default Activity
   */
  public SourceModule with(com.androidstarterkit.cmd.WidgetType widgetType) {
    if (homePath == null) {
      throw new NullPointerException("Failed : There is no HOME PATH");
    }

    try {
      String lines = transferActivity(widgetType.getActivityName());
      FileUtils.writeFile(FileUtils.getFileInDirectory(sourceDirPath, activityName + ".java"), lines);
    } catch (UnsupportedWidgetTypeException e) {
      e.printStackTrace();
    }

    return this;
  }

  /**
   * Copy a module file to a source project
   *
   * @param fileName is a name of module file
   * @return Source instance after copying file to source project
   */
  public SourceModule put(String fileName) {
    try {
      switch (fileName) {
        case FileNames.COFFEE_TYPE:
        case FileNames.SLIDINGTAB_FRAGMNET:
          FileUtils.copyFile(module.getPath(fileName), sourceDirPath, fileName);
          FileUtils.changeAppplicationId(FileUtils.linkPathWithSlash(sourceDirPath, fileName),
              applicationId,
              module.getApplicationId());
          break;
        case FileNames.ACTIVITY_RECYCLERVIEW_XML:
        case FileNames.ACTIVITY_LISTVIEW_XML:
        case FileNames.LAYOUT_LIST_ITEM_XML:
        case FileNames.FRAGMNET_MAIN_XML:
        case FileNames.TAB_TEXT_LAYOUT_XML:
        case FileNames.TAB_IMAGE_LAYOUT_XML:
          FileUtils.copyFile(module.getPath(fileName), layoutDirPath, fileName);
          break;
        case FileNames.ACTIVITY_SLIDINGTABLAYOUT_XML:
          FileUtils.copyFile(module.getPath(fileName), layoutDirPath, fileName);
          FileUtils.changeAppplicationId(FileUtils.linkPathWithSlash(layoutDirPath, fileName),
              applicationId,
              module.getApplicationId());
          break;
        case FileNames.RECYCLERVIEW_ADAPTER:
        case FileNames.LISTVIEW_ADAPTER:
        case FileNames.SLIDINGICONTAB_ADAPTER:
        case FileNames.SLIDINGTAB_ADAPTER:
          FileUtils.copyFile(module.getPath(fileName), sourceDirPath + "/adapter", fileName);
          FileUtils.changeAppplicationId(FileUtils.linkPathWithSlash(sourceDirPath, "adapter", fileName),
              applicationId,
              module.getApplicationId());
          break;
        case FileNames.SLIDINGTABLAYOUT:
        case FileNames.SLIDINGTABSTRIP:
          FileUtils.copyFile(module.getPath(fileName), sourceDirPath + "/widgets", fileName);
          FileUtils.changeAppplicationId(FileUtils.linkPathWithSlash(sourceDirPath, "widgets", fileName),
              applicationId,
              module.getApplicationId());
          break;
        case FileNames.BUILD_GRADLE:
          BuildGradleFile buildGradleFile = new BuildGradleFile(appModuleDirPath);
          buildGradleFile.addDependency(BuildGradleConfig.RECYCLERVIEW_LIBRARY,
              BuildGradleConfig.CARDVIEW_LIBRARY);
          break;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return this;
  }

  /**
   * Transfer source Activity to module Activity with source application id and file name
   *
   * @param moduleActivityName the Activity name of module
   * @return the content of activity file
   */
  private String transferActivity(String moduleActivityName) {
    File activityFile = module.getFile(moduleActivityName);

    String content = "";
    try {
      Scanner scanner = new Scanner(activityFile);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();

        line = line.replace(module.getApplicationId(), applicationId);
        line = line.replace(moduleActivityName.replace(".java", ""), activityName);
        content += line + "\n";
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return content;
  }
}
