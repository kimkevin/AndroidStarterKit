package com.androidstarterkit.util;

import com.androidstarterkit.config.AskConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {

  public static String getRootPath() {
    File projectDir = new File(".");

    try {
      int index = projectDir.getCanonicalPath().indexOf(AskConfig.DEFAULT_ASK_APP_NAME);

      if (index > 0) {
        return projectDir.getCanonicalPath().substring(0, index - 1);
      }
      return projectDir.getCanonicalPath();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

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

  public static void writeFile(File file, List<String> lineList) {
    writeFile(file, toString(lineList));
  }

  public static void writeFile(File file, String content) {
    if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
      return;
    }

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

  public static void writeFile(String pathName, String fileName, String content) {
    File destDir = new File(pathName);
    if (!destDir.exists() && !destDir.mkdirs()) {
      return;
    }

    writeFile(FileUtils.linkPathWithSlash(pathName, fileName), content);
  }

  public static void writeFile(String filePath, String content) {
    File file = new File(filePath);

    try {
      if (!file.exists()) {
        file.createNewFile();
      }

      writeFile(file, content);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static List<String> readFileAsString(File file) {
    Scanner scanner;
    List<String> stringList = new ArrayList<>();

    try {
      scanner = new Scanner(file);

      while (scanner.hasNextLine()) {
        String e = scanner.nextLine();
        stringList.add(e);
      }
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Failed to find : " + file.getPath());
    }

    return stringList;
  }

  public static void copyFile(File moduleFile,
                              String sourceFilePath) throws IOException {
    File destDir = new File(sourceFilePath);
    if (!destDir.exists() && !destDir.mkdirs()) {
      return;
    }

    File sourceFile = moduleFile;
    File destFile = new File(linkPathWithSlash(sourceFilePath, moduleFile.getName()));

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

  public static void copyDirectory(File src, File dest) throws IOException{
    if (src.isDirectory()) {
      if (!dest.exists()) {
        dest.mkdirs();
      }

      String files[] = src.list();

      for (String file : files) {
        File srcFile = new File(src, file);
        File destFile = new File(dest, file);
        copyDirectory(srcFile,destFile);
      }

    } else {
      InputStream in = new FileInputStream(src);
      OutputStream out = new FileOutputStream(dest);

      byte[] buffer = new byte[1024];

      int length;
      while ((length = in.read(buffer)) > 0) {
        out.write(buffer, 0, length);
      }

      in.close();
      out.close();
    }
  }

  public static String changeDotToSlash(String str) {
    return str.replaceAll("\\.", "/");
  }

  public static String getStringBetweenQuotes(String str) {
    Pattern pattern = Pattern.compile("\"([^\"]*)\"");
    Matcher matcher = pattern.matcher(str);
    while (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }

  public static String getFilenameFromDotPath(String pathname) {
    int index = pathname.lastIndexOf(".");
    if (index < 0) {
      index = 0;
    } else {
      index += 1;
    }
    return pathname.substring(index, pathname.length());
  }

  public static String getFilenameFromSlashPath(String pathname) {
    int index = pathname.lastIndexOf("/");
    if (index < 0) {
      index = 0;
    } else {
      index += 1;
    }
    return pathname.substring(index, pathname.length());
  }

  public static String removeExtension(String filename) {
    return filename.substring(0, filename.lastIndexOf('.'));
  }

  public static String removeFirstSlash(String path) {
    if (path.length() > 0 && path.charAt(0) == '/') {
      path = path.substring(1);
    }

    return path;
  }

  public static String extractIndentInLine(String line) {
    String intent = "";
    for (int i = 0, li = line.length(); i < li; i++) {
      if (line.toCharArray()[i] == ' ') {
        intent += " ";
      } else {
        return intent;
      }
    }
    return intent;
  }

  public static String toString(List<String> strList) {
    final String EoL = System.getProperty("line.separator");

    StringBuilder sb = new StringBuilder();
    for (String line : strList) {
      sb.append(line).append(EoL);
    }

    return sb.toString();
  }

  public static String getStringWithRemovedComment(String line) {
    return line.replaceAll("//.*", "");
  }

  public static int getOpenCurlyBracketCount(String line) {
    int count = 0;
    for ( int i = 0; i < line.length(); i++ ) {
      if (line.charAt(i) == '{') {
        count++;
      }
    }
    return count;
  }

  public static int getCloseCurlyBracketCount(String line) {
    int count = 0;
    for ( int i = 0; i < line.length(); i++ ) {
      if (line.charAt(i) == '}') {
        count++;
      }
    }
    return count;
  }
}
