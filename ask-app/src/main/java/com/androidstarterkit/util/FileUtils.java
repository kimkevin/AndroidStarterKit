package com.androidstarterkit.util;

import com.androidstarterkit.constraint.SyntaxConstraints;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {
  public static final String CLASSES_PATH = "/ask-app/build/classes/main";

  /**
   * Get root path in this project
   *
   * @return the string for project root path
   */
  public static String getRootPath() {
    File rootPath = new File(".");
    try {
      return rootPath.getCanonicalPath().replace(CLASSES_PATH, "");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Get file in directory
   *
   * @param dirPath  to find the file by name in directory
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
   *
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

  public static void writeFile(File file, List<String> lineList) {
    writeFile(file, getString(lineList));
  }

  /**
   * Write content you want to file
   *
   * @param file    is target file to importLayout
   * @param content is the string you want to importLayout
   */
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

  public static void writeFile(String pathName
      , String fileName
      , String content) {
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

  /**
   * Read content from file
   *
   * @param file is target file to read
   * @return content is the strings in file
   */
  public static List<String> readFile(File file) {
    Scanner scanner;
    List<String> stringList = new ArrayList<>();

    try {
      scanner = new Scanner(file);

      while (scanner.hasNextLine()) {
        String e = scanner.nextLine();
        stringList.add(e);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    return stringList;
  }

  /**
   * Copy file of AndroidModule to file of source project.
   *
   * @param sourceFilePath is path of source
   * @throws IOException
   */
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

  /**
   * Get String from String List
   *
   * @param strList is a content of file
   * @return String of a content
   */
  public static String getString(List<String> strList) {
    StringBuffer stringBuffer = new StringBuffer();
    for (String str : strList) {
      stringBuffer.append(FileUtils.addNewLine(str));
    }

    return stringBuffer.toString();
  }

  public static String changeDotToSlash(String str) {
    return str.replaceAll("\\.", "/");
  }

  /**
   * Get a string between double quotes (")
   *
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

  /**
   * @param path is the string which is the path with dot(.) delimeter such as com.ask.MainActivity
   * @return is the string for file name
   */
  public static String getFileNameForDotPath(String path) {
    final String[] token = path.split("\\.");
    final int lastIndex = token.length - 1;
    return token[lastIndex];
  }

  /**
   * Add intent to line
   *
   * @param line is a string
   * @return new string with indent
   */
  public static String getIndentOfLine(String line) {
    String intent = "";
    for (int i = 0, li = line.length(); i < li; i++) {
      if (line.toCharArray()[i] == ' ') {
        intent += " ";
      } else {
        return intent;
      }
    }
    return SyntaxConstraints.DEFAULT_INDENT;
  }

  /**
   * Add '\n' to line
   *
   * @param line is a string without new-line character
   * @return new string with new-line character
   */
  private static String addNewLine(String line) {
    return line + "\n";
  }

  /**
   * Remove extension
   *
   * @param filename String for file name with extension
   * @return String for file name
   */
  public static String removeExtension(String filename) {
    return filename.substring(0, filename.lastIndexOf('.'));
  }

  public static String splitFirstSlash(String path) {
    if (path.length() > 0 && path.charAt(0) == '/') {
      path = path.substring(1);
    }

    return path;
  }
}
