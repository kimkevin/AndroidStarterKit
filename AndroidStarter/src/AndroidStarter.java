import utils.FileUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AndroidStarter {
  static final String BUILD_GRADLE_NAME = "build.gradle";
  static final String SETTINGS_GRADLE_NAME = "settings.gradle";
  static final String MAIN_DIR_PATH = "src/main";
  static final String ANDROID_MANIFEST_XML_NAME = "AndroidManifest.xml";
  static final String ANDROID_MODULE_NAME = "AndroidModule";

  static String sampleSourceDirPath;
  static String sampleAndroidManifestPath;
  static String sampleBuildGradlePath;
  static String sampleLayoutDirPath;

  static boolean isDebuggable = true;

  public static void main(String[] args) {
    /**
     * Get sample project path through argument
     */
    String sampleHomePath = "/Users/kevin/Documents/git/AndroidStarterKit/AndroidSample";
    String moduleHomePath;
    try {
      moduleHomePath = FileUtils.makePathWithSlash(new File(".").getCanonicalPath(), ANDROID_MODULE_NAME);
    } catch (IOException e) {
      printLog("Failed to find module");
      e.printStackTrace();
      return;
    }

    File rootPath = new File(".");
    try {
      printLog("Root Path : " + rootPath.getCanonicalPath());
    } catch (IOException e) {
      e.printStackTrace();
    }

    String sampleModuleName = null;
    String samplePackagePath = null;

    /**
     * Get module's name
     */
    File settingsGradleFile = FileUtils.getFileInDirectory(sampleHomePath, SETTINGS_GRADLE_NAME);
    if (settingsGradleFile.exists()) {
      try {
        Scanner scanner = new Scanner(settingsGradleFile);

        while (scanner.hasNextLine()) {
          String line = scanner.nextLine().replace("\'", "").replace(":", "");

          String[] tokens = line.split(" ");
          sampleModuleName = tokens[1];
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }


    if (sampleModuleName != null) {
      printLog("Success to find module name : " + sampleModuleName);
    } else {
      printLog("Failed to find module name");
      return;
    }

    /**
     * Get package path from build.gradle
     */
    File buildGradleFile = FileUtils.getFileInDirectory(FileUtils.makePathWithSlash(sampleHomePath, sampleModuleName), BUILD_GRADLE_NAME);
    if (buildGradleFile.exists()) {
      try {
        Scanner scanner = new Scanner(buildGradleFile);

        while (scanner.hasNextLine()) {
          String line = scanner.nextLine();

          if (line.contains("applicationId")) {
            samplePackagePath = getStringBetweenQuotes(line).replaceAll("\\.", "/");
            break;
          }
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }

    if (samplePackagePath != null) {
      printLog("Success to find package path : " + samplePackagePath);
    } else {
      printLog("Failed to find package path");
      return;
    }

    sampleSourceDirPath = FileUtils.makePathWithSlash(sampleHomePath, sampleModuleName, MAIN_DIR_PATH, "java", samplePackagePath);

    /**
     * Get name of default Activity
     */
    File sourceDir = new File(sampleSourceDirPath);
    if (sourceDir.list().length > 0) {
      printLog("Success to find Default Activity : " + sourceDir.list()[0]);
    } else {
      printLog("Failed to find Default Activity");
      return;
    }

    sampleBuildGradlePath = buildGradleFile.getPath();
    sampleAndroidManifestPath = FileUtils.makePathWithSlash(sampleHomePath, sampleModuleName, MAIN_DIR_PATH, ANDROID_MANIFEST_XML_NAME);
    sampleLayoutDirPath = FileUtils.makePathWithSlash(sampleHomePath, sampleModuleName, MAIN_DIR_PATH, "res/layout");

//    System.out.println("Source Path : " + sampleSourceDirPath);
//    System.out.println("build.gradle Path : " + sampleBuildGradlePath);
//    System.out.println("AndroidManifest Path : " + sampleAndroidManifestPath);
//    System.out.println("layout path : " + sampleLayoutDirPath);

    /**
     * Starting to copy Module to Sample project
     */
    final String moduleLayoutDirPath = FileUtils.makePathWithSlash(moduleHomePath, "/app/src/main/res/layout");

//    try {
//      FileUtils.copyFile(moduleLayoutDirPath, sampleLayoutDirPath, "activity_main.xml");
//      FileUtils.copyFile(moduleLayoutDirPath, sampleLayoutDirPath, "layout_list_item.xml");
//    } catch (IOException e) {
//      e.printStackTrace();
//      return;
//    }

    /**
     * copy RecyclerViewActivity to MainActivity and change Package and Class name
     */

    /**
     * copy dependencies in build.gralde
     */

    /**
     * copy comdule only file or file with package
     */
  }

  public static String getStringBetweenQuotes(String str) {
    Pattern pattern = Pattern.compile("\"([^\"]*)\"");
    Matcher matcher = pattern.matcher(str);
    while (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }

  public static void printLog(String log) {
    if (isDebuggable) {
      System.out.println(log);
    }
  }
}
