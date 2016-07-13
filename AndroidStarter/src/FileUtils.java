import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {
  /**
   * Get file in directory
   * @param dirPath to find the file by name in directory
   * @param fileName to get the file
   * @return the file was found in directory
   */
  public static File getFileInDirectory(String dirPath, String fileName) {
    File dir = new File(dirPath);
    File file = new File(dir, fileName);
    return file;
  }

  /**
   * Adding a slash between arguments for new path
   * @param args are paths which are needed to one path
   * @return path was made by args
   */
  public static String linkPathWithSlash(String... args) {
    String path = "";
    for (int i = 0, li = args.length; i < li; i++) {
      path += args[i];

      if (i != li - 1) {
        path += "/";
      }
    }
    return path;
  }

  /**
   * Write content you want to file
   * @param file is target file to write
   * @param content is the string you want to write
   */
  public static void writeFile(File file, String content) {
    FileWriter fileWriter;
    try {
      fileWriter = new FileWriter(file);
      BufferedWriter bw = new BufferedWriter(fileWriter);
      bw.write(content);
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Copy file of AndroidModule to file of source project.
   * @param moduleFilePath is path of source file
   * @param sourceFilePath is path of module
   * @param fileName
   * @throws IOException
   */
  public static void copyFile(String moduleFilePath,
                              String sourceFilePath, String fileName) throws IOException {
    File destDir = new File(sourceFilePath);
    if (!destDir.exists()) {
      destDir.mkdir();
    }

    File sourceFile = new File(linkPathWithSlash(moduleFilePath, fileName));
    File destFile = new File(linkPathWithSlash(sourceFilePath, fileName));

    if (!sourceFile.exists()) {
      return;
    }

    if (!destFile.exists()) {
      destFile.createNewFile();
    }

    FileChannel sourceChannel;
    FileChannel destinationChannel;

    sourceChannel = new FileInputStream(sourceFile).getChannel();
    destinationChannel = new FileOutputStream(destFile).getChannel();

    if (destinationChannel != null && sourceChannel != null) {
      destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
    }

    if (sourceChannel != null) {
      sourceChannel.close();
    }

    if (destinationChannel != null) {
      destinationChannel.close();
    }
  }

  /**
   * Change applicationId of AndroidModule to applicationId of source project
   * @param filePath is the file path in source project
   * @param applicationId of source project
   */
  public static void changeAppplicationId(String filePath, String applicationId) {
    File file = new File(filePath);
    try {
      String lines = "";

      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();

        line = line.replace(AndroidModule.APPLICATION_ID, applicationId);
        lines += line + "\n";
      }

      FileWriter fw = new FileWriter(new File(filePath));
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(lines);
      bw.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get a string between double quotes (")
   * @param str has with double quotes
   * @return string was removed double quotes
   */
  public static String getStringBetweenQuotes(String str) {
    Pattern pattern = Pattern.compile("\"([^\"]*)\"");
    Matcher matcher = pattern.matcher(str);
    while (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }
}
