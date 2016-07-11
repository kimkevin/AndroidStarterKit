import utils.FileUtils;

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
      appModuleDirPath = FileUtils.makePathWithSlash(homePath, appModuleName);
      System.out.println("Success to find module name : " + appModuleName);
    } else {
      System.out.println("Failed to find module name");
      return;
    }

    /**
     * Get package path from build.gradle
     */
    String packagePath = null;

    File buildGradleFile = FileUtils.getFileInDirectory(FileUtils.makePathWithSlash(homePath, appModuleName), FileNames.BUILD_GRADLE);
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

    sourceDirPath = FileUtils.makePathWithSlash(homePath, appModuleName, "src/main/java", packagePath);
    layoutDirPath = FileUtils.makePathWithSlash(homePath, appModuleName, "src/main/res/layout");

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

  public static Source load(String homePath) {
    if (source == null) {
      synchronized (Source.class) {
        if (source == null) {
          source = new Source(homePath);
        }
      }
    }

    return source;
  }

  /**
   * copy RecyclerViewActivity to MainActivity and change Package and Class name
   */
  public Source with(ModuleType moduleType) {
    if (source.homePath == null) {
      throw new NullPointerException("Failed : There is no HOME PATH");
    }

    switch (moduleType) {
      case RecyclerViewActivity:
        File activityFile = new File(FileUtils.makePathWithSlash(module.getPath(FileNames.RECYCLERVIEWACTIVITY), FileNames.RECYCLERVIEWACTIVITY));
        try {
          String lines = "";

          Scanner scanner = new Scanner(activityFile);
          while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            line = line.replace("com.kimkevin.module", source.applicationId);
            line = line.replace(FileNames.RECYCLERVIEWACTIVITY.replace(".java", ""), source.activityName);
            lines += line + "\n";
          }

          FileUtils.writeFile(new File(FileUtils.makePathWithSlash(source.sourceDirPath, activityName + ".java")), lines);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
        break;
    }

    return source;
  }

  /**
   * copy dependencies in build.gralde
   */
  /**
   * copy comdule only file or file with package
   */
  public Source put(String fileName) {
    try {
      switch (fileName) {
        case FileNames.ACTIVITY_MAIN_XML:
        case FileNames.LAYOUT_LIST_ITEM_XML:
          FileUtils.copyFile(module.getPath(fileName), source.layoutDirPath, fileName);
          break;
        case FileNames.RECYCLERVIEWADAPTER:
          FileUtils.copyFile(module.getPath(fileName), source.sourceDirPath + "/adapter", fileName);
          FileUtils.changePackageForSampleModule(
                  FileUtils.makePathWithSlash(source.sourceDirPath, "adapter", fileName),
                  source.applicationId);
          break;
        case FileNames.BUILD_GRADLE:
          copyBuildGradle();
          break;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return source;
  }

  private void copyBuildGradle() {
    File moduleBuildGradleFile = new File(
            FileUtils.makePathWithSlash(module.getPath(FileNames.BUILD_GRADLE), FileNames.BUILD_GRADLE));
    try {
      boolean shudCopy = false;
      String moduleLines = "";
      Scanner scanner = new Scanner(moduleBuildGradleFile);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();

        if (line.contains("dependencies")) {
          shudCopy = true;
        }

        if (shudCopy) {
          moduleLines += line + "\n";
        }
      }

      String sourceLines = "";

      File sourceBuildGradleFile = new File(
              FileUtils.makePathWithSlash(source.appModuleDirPath, FileNames.BUILD_GRADLE));
      scanner = new Scanner(sourceBuildGradleFile);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();

        if (line.contains("dependencies")) {
          sourceLines += moduleLines;
          break;
        }

        sourceLines += line + "\n";
      }

      FileUtils.writeFile(sourceBuildGradleFile, sourceLines);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}
