package com.androidstarterkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassParser {
  /**
   * Primitive Data Types
   */
  public static List<String> PRIMITIVE_DATA_TYPE = Arrays.asList(
      "byte", "short", "int", "long", "float", "double", "boolean", "char"
  );

  /**
   * Modifier Types : Java Access Modifiers
   */
  public static List<String> KEYWORDS_JAVA_ACCESS_MODIFIER = Arrays.asList(
      "private", "protected", "public", "default"
  );

  /**
   * Modifier Types : Non Access Modifiers
   */
  public static List<String> KEYWORDS_NON_ACCESS_MODIFIER = Arrays.asList(
      "final", "static", "abstract", "synchronized", "volatile"
  );

  /**
   * Reserved Keywords
   */
  public static List<String> KEYWORDS_RESERVED = Arrays.asList(
      "class", "void", "return", "enum"
  );

  public static String ARGUMENTS_SYNTAX = "...";

  /**
   * Get class names from code file
   *
   * @param file code file
   * @return String array for class name
   */
  public static List<String> getClassNames(File file) {
    Scanner scanner;
    List<String> classNames = new ArrayList<>();

    try {
      scanner = new Scanner(file);

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        classNames.addAll(getClassNames(line));
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    return classNames;
  }

  /**
   * Get class names from code line
   *
   * @param line String for code line
   * @return String array for class name
   */
  public static List<String> getClassNames(String line) {
    List<String> classNames = new ArrayList<>();

    String replacedLine = line;
    replacedLine = replaceNoBlank(replacedLine, PRIMITIVE_DATA_TYPE);
    replacedLine = replaceNoBlank(replacedLine, KEYWORDS_JAVA_ACCESS_MODIFIER);
    replacedLine = replaceNoBlank(replacedLine, KEYWORDS_NON_ACCESS_MODIFIER);
    replacedLine = replaceNoBlank(replacedLine, KEYWORDS_RESERVED);

    mergeDistinct(classNames, listInheritClasses(replacedLine));
    mergeDistinct(classNames, listFieldClasses(replacedLine));
    mergeDistinct(classNames, listStaticClasses(replacedLine));
    mergeDistinct(classNames, listParameterClasses(line));

    return classNames;
  }

  /**
   * Get class names for inherit class
   * ex) extends Class, implements Interface
   *
   * @param line String for code line
   * @return String array for class name
   */
  public static List<String> listInheritClasses(String line) {
    List<String> classNames = new ArrayList<>();
    String reg = "(implements|extends)\\s+([A-Za-z0-9]+)\\s";
    Pattern pat = Pattern.compile(reg);
    Matcher matcher = pat.matcher(line);

    while (matcher.find()) {
      String matched = matcher.group(2);
      classNames.add(matched.trim());
    }

    return classNames;
  }

  /**
   * Get class names as field variables which can be instance variable(Non-Static Fields)
   * and class variable(Static Fields)
   *
   * ex)
   * ClassName.InnerClass class;
   * ClassName.InnerClass class =
   * ClassName class;
   * ClassName<GenericClass> class;
   *
   * @param line String for code line
   * @return String array for class name
   */
  public static List<String> listFieldClasses(String line) {
    List<String> classNames = new ArrayList<>();

    String reg = "[A-Za-z0-9.]+([\\[\\s\\]]*|(<[A-Za-z0-9.]+>)*)\\s+[A-Za-z0-9]+\\s*(;|=)";

    Pattern pat = Pattern.compile(reg);
    Matcher matcher = pat.matcher(line);

    while (matcher.find()) {
      String matched = matcher.group()
          .replaceAll(";", "")
          .replaceAll("=", "");

      classNames.add(matched.trim().split(" ")[0]);
    }

    return classNames;
  }

  /**
   * Get class names as parameters
   * ex) (Context context, AttributeSet attrs, int defStyle)
   *
   * @param line String for code line
   * @return String array for class name
   */
  public static List<String> listParameterClasses(String line) {
    List<String> classNames = new ArrayList<>();

    line = replaceNoBlank(line, KEYWORDS_NON_ACCESS_MODIFIER);

    String reg = "[A-Za-z0-9]+\\s*\\([A-Za-z0-9.\\s,<>]+\\)";
    Pattern pat = Pattern.compile(reg);
    Matcher matcher = pat.matcher(line);

    while (matcher.find()) {
      String group = matcher.group();

      if (group.length() > 0) {
        String[] parameterClassNames = getParamsInBraces(group);

        if (parameterClassNames == null) {
          break;
        }

        for (String matched : parameterClassNames) {
          String[] splitParamter = matched.trim().split(" ");

          if (splitParamter.length < 2 || splitParamter[0] == null) {
            continue;
          }

          String className = splitParamter[0];

          if (PRIMITIVE_DATA_TYPE.contains(className.replace(ARGUMENTS_SYNTAX, ""))) {
            // Removed primitive type keywords
          } else {
            if (listClassWithGeneric(className) != null) {
              classNames.addAll(listClassWithGeneric(className));
            } else if (getOuterClass(className) != null) {
              classNames.add(getOuterClass(className));
            } else {
              classNames.add(className);
            }
          }
        }
      }
    }

    return classNames;
  }

  /**
   * Get class names which have static method or variable
   *
   * ex) Class.method()
   * Class.variable
   * Class.StaticInnerClass
   *
   * warning : It can not distinguish between Class.method() and class.method()
   *
   * @param line String for code line
   * @return String array for class name
   */
  public static List<String> listStaticClasses(String line) {
    List<String> classNames = new ArrayList<>();

    String reg = "(\\s+[A-Za-z0-9]+\\s*)\\.[A-Za-z0-9]+(\\s|\\[|;|\\()";
    Pattern pat = Pattern.compile(reg);
    Matcher matcher = pat.matcher(line);

    while (matcher.find()) {
      String matched = matcher.group(1).trim();

      if (!classNames.contains(matched)) {
        classNames.add(matched);
      }
    }

    return classNames;
  }

  /**
   * Get class names which have generic class
   *
   * @param className String for class name
   * @return String array for class name
   */
  public static List<String> listClassWithGeneric(String className) {
    Pattern pattern = Pattern.compile("\\<([^>]+)\\>");
    Matcher matcher = pattern.matcher(className);

    List<String> classList = new ArrayList<>();

    int anglebrackIndex = className.indexOf('<');
    if (anglebrackIndex < 0) {
      return null;
    }

    classList.add(className.substring(0, anglebrackIndex));

    while (matcher.find()) {
      classList.add(matcher.group(1));
      return classList;
    }

    return null;
  }

  /**
   * Get strings of class variable as parameters
   *
   * @param line String for code line
   * @return String array for class name
   */
  public static String[] getParamsInBraces(String line) {
    Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
    Matcher matcher = pattern.matcher(line);

    while (matcher.find()) {
      return matcher.group(1).split(",");
    }

    return null;
  }

  /**
   * Get name of outer class which has nested class from class name
   * ex) OuterClass.NestedClass
   *
   * @param className String of class name
   * @return String of outer class name
   */
  public static String getOuterClass(String className) {
    String[] classsArr = className.split("\\.");
    if (classsArr.length > 0 && classsArr[0] != null) {
      return classsArr[0];
    }

    return null;
  }

  /**
   * Replace list of identifier to no blank
   *
   * @param line           String for code line
   * @param identifierList list of identifiers
   * @return String that was removed to identifiers
   */
  private static String replaceNoBlank(String line, List<String> identifierList) {
    for (String identifier : identifierList) {
      line = line.replaceAll(identifier, "");
    }

    return line;
  }

  public static void mergeDistinct(List<String> originalList, List<String> matchedList) {
    for (String className : matchedList) {
      if (!originalList.contains(className)) {
        originalList.add(className);
      }
    }
  }
}
