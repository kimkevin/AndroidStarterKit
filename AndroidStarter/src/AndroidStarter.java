import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AndroidStarter {
  public static final String BUILD_GRADLE_NAME = "build.gradle";
  public static final String SOURCE_PATH = "src/main/java";

  public static void main(String[] args) {
    final String appDir = "/Users/kevin/Documents/git/AndroidMakeModule/AndroidSample";
    String moduleName = null;
    String packagePath = null;

    File settingsGradleFile = getFile(appDir, "settings.gradle");
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
      System.out.println("Success to find module name : " + moduleName);
    } else {
      System.out.println("Failed to find module name");
      return;
    }

    File buildGradleFile = getFile(appDir + "/" + moduleName, BUILD_GRADLE_NAME);
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
      System.out.println("Success to find package path : " + packagePath);
    } else {
      System.out.println("Failed to find package path");
      return;
    }


  }

  public static File getFile(String dirPath, String fileName) {
    File dir = new File(dirPath);
    File file = new File(dir, fileName);
    return file;
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
}
