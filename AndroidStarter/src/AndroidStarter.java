import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AndroidStarter {
  static final String fileName = "settings.gradle";
  static final String BUILD_GRADLE_NAME = "build.gradle";
  static final String MAIN_DIR_PATH = "src/main";
  static final String ANDROID_MANIFEST_XML_NAME = "AndroidManifest.xml";

  static String sourceDirPath;
  static String androidManifestPath;
  static String buildGradlePath;
  static String layoutDirPath;

  static boolean isDebuggable = true;

  public static void main(String[] args) {
    final String appDir = "/Users/kevin/Documents/git/AndroidStarter/AndroidSample";

    String moduleName = null;
    String packagePath = null;

    /**
     * Get module's name
     */
    File settingsGradleFile = getFile(appDir, fileName);
    if (settingsGradleFile.exists()) {
      try {
        Scanner scanner = new Scanner(settingsGradleFile);

        while (scanner.hasNextLine()) {
          String line = scanner.nextLine().replace("\'", "").replace(":", "");

          String[] tokens = line.split(" ");
          moduleName = tokens[1];
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }


    if (moduleName != null) {
      printLog("Success to find module name : " + moduleName);
    } else {
      printLog("Failed to find module name");
      return;
    }

    /**
     * Get package path from build.gradle
     */
    File buildGradleFile = getFile(makePath(appDir, moduleName), BUILD_GRADLE_NAME);
    if (buildGradleFile.exists()) {
      try {
        Scanner scanner = new Scanner(buildGradleFile);

        while (scanner.hasNextLine()) {
          String line = scanner.nextLine();

          if (line.contains("applicationId")) {
            String applicationId = getStringBetweenQuotes(line);
            packagePath = changeApplicationIdToPath(applicationId);
            break;
          }
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }

    if (packagePath != null) {
      printLog("Success to find package path : " + packagePath);
    } else {
      printLog("Failed to find package path");
      return;
    }

    sourceDirPath = makePath(appDir, moduleName, MAIN_DIR_PATH, "java", packagePath);

    /**
     * Get name of default Activity
     */
    File sourceDir = new File(sourceDirPath);
    if (sourceDir.list().length > 0) {
      printLog("Success to find Default Activity : " + sourceDir.list()[0]);
    } else {
      printLog("Failed to find Default Activity");
    }

    buildGradlePath = buildGradleFile.getPath();
    androidManifestPath = makePath(appDir, moduleName, MAIN_DIR_PATH, ANDROID_MANIFEST_XML_NAME);
    layoutDirPath = makePath(appDir, moduleName, MAIN_DIR_PATH, "res/layout");

    System.out.println("Source Path : " + sourceDirPath);
    System.out.println("build.gradle Path : " + buildGradlePath);
    System.out.println("AndroidManifest Path : " + androidManifestPath);
    System.out.println("layout path : " + layoutDirPath);
  }

  public static File getFile(String dirPath, String fileName) {
    File dir = new File(dirPath);
    File file = new File(dir, fileName);
    return file;
  }

  public static String makePath(String... args) {
    String path = "";
    for (int i = 0, li = args.length; i < li; i++) {
      path += args[i];

      if (i != li - 1) {
        path += "/";
      }
    }
    return path;
  }

  public static String getStringBetweenQuotes(String str) {
    Pattern pattern = Pattern.compile("\"([^\"]*)\"");
    Matcher matcher = pattern.matcher(str);
    while (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }

  public static String changeApplicationIdToPath(String str) {
    String path = null;
    String[] tokens = str.split("\\.");

    if (tokens.length == 0) {
      return path;
    } else {
      path = "";
    }

    for (int i = 0, li = tokens.length; i < li; i++) {
      path += tokens[i];
      if (i != li - 1) {
        path += "/";
      }
    }
    return path;
  }

  public static void printLog(String log) {
    if (isDebuggable) {
      System.out.println(log);
    }
  }
}
