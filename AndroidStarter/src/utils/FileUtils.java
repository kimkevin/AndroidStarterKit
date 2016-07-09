package utils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {
  public static File getFileInDirectory(String dirPath, String fileName) {
    File dir = new File(dirPath);
    File file = new File(dir, fileName);
    return file;
  }

  public static String makePathWithSlash(String... args) {
    String path = "";
    for (int i = 0, li = args.length; i < li; i++) {
      path += args[i];

      if (i != li - 1) {
        path += "/";
      }
    }
    return path;
  }

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

  public static void copyFile(String sourcePath, String destPath, String fileName) throws IOException {
    File destDir = new File(destPath);
    if (!destDir.exists()) {
      destDir.mkdir();
    }

    File sourceFile = new File(makePathWithSlash(sourcePath, fileName));
    File destFile = new File(makePathWithSlash(destPath, fileName));

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

  public static void changePackageForSampleModule(String filePath, String applicationId) {
    File file = new File(filePath);
    try {
      String lines = "";

      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();

        line = line.replace("com.kimkevin.module", applicationId);
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

  public static String getStringBetweenQuotes(String line) {
    Pattern pattern = Pattern.compile("\"([^\"]*)\"");
    Matcher matcher = pattern.matcher(line);
    while (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }
}
