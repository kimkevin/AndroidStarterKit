package com.androidstarterkit;

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

  public static final String DEFAULT_INDENT = "    ";
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

  /**
   * Write content you want to file
   *
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

  /**
   *
   * Change applicationId of AndroidModule to applicationId of source project
   * @param filePath is the file path in source project
   * @param sourceApplicationId of source project
   * @param moduleApplicationId of module project
   */
  public static void changeAppplicationId(String filePath, String sourceApplicationId, String moduleApplicationId) {
    File file = new File(filePath);
    try {
      String lines = "";

      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();

        line = line.replace(moduleApplicationId, sourceApplicationId);
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
   * Add line to specific object by a string list of scope
   *
   * @param objectName is keyword to find scope
   * @param line is a string that is needed to add
   * @param stringList is string list of scope
   * @return string list was added to line such as external library
   */
  public static List<String> addLineToObject(String objectName, String line, List<String> stringList) {
    boolean isFoundScope = false;
    String indent = "";

    for (int i = 0, li = stringList.size(); i < li; i++) {
      String str = stringList.get(i);
      if (str.contains(objectName)) {
        isFoundScope = true;

        if (i + 1 < li) {
          indent = getIndentOfLine(stringList.get(i + 1));
        } else {
          indent = DEFAULT_INDENT;
        }
      }

      if (isFoundScope && str.contains("}")) {
        if (stringList.contains(indent + line)) {
          continue;
        }

        stringList.add(i, indent + line);
        return stringList;
      }
    }

    return stringList;
  }

  /**
   * Add intent to line
   *
   * @param line is a string
   * @return new string with indent
   */
  private static String getIndentOfLine(String line) {
    String intent = "";
    for (int i = 0, li = line.length(); i < li; i++) {
      if (line.toCharArray()[i] == ' ') {
        intent += " ";
      } else {
        return intent;
      }
    }
    return DEFAULT_INDENT;
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
}
