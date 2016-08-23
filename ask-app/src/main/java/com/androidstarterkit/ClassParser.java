package com.androidstarterkit;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassParser {

  /**
   * Variable Types : Primitive Data Type and Arrays
   */
  public static List<String> KEYWORDS_PRIMITIVE_TYPE = Arrays.asList(
      "byte", "short", "int", "long", "float", "double", "boolean", "String", "char"
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

  public static List<String> KEYWORDS_RETURN_TYPE = Arrays.asList(
      "void", "byte", "short", "int", "long", "float", "double", "boolean", "String", "char"
  );

  public static List<String> KEYWORDS_RESERVED = Arrays.asList(
      "class", "void", "return", "enum"
  );

  public static String ARGUMENTS_SYNTAX = "...";

  public static List<String> getVariables(File file) {
    return null;
  }

  public static List<String> getClasses(File file) {
    return null;
  }

  public static List<String> getClassNames(String line) {
    List<String> classNames = new ArrayList<>();

    String newLine = line;
    newLine = replaceNoBlank(newLine, KEYWORDS_PRIMITIVE_TYPE);
    newLine = replaceNoBlank(newLine, KEYWORDS_JAVA_ACCESS_MODIFIER);
    newLine = replaceNoBlank(newLine, KEYWORDS_NON_ACCESS_MODIFIER);
    newLine = replaceNoBlank(newLine, KEYWORDS_RESERVED);

    listInheritClasses(classNames, newLine);
    listFieldClasses(classNames, newLine);
    listStaticClasses(classNames, newLine);
    listParameterClasses(classNames, line);

    return classNames;
  }

  /**
   * extends Class, implements Interface
   */
  private static void listInheritClasses(List<String> classNames, String line) {
    String reg = "((implements|extends)\\s+)+[A-Za-z0-9]+\\s";
    Pattern pat = Pattern.compile(reg);
    Matcher matcher = pat.matcher(line);

    while (matcher.find()) {
      String matched = matcher.group();
      classNames.add(matched);
    }
  }

  /**
   *  Class.InnerClass class;
   *  Class.InnerClass class =
   *  Class class;
   *  Class class =
   *  Class<TypeClass> class;
   *  Class<TypeClass> class =
   */
  private static void listFieldClasses(List<String> classNames, String line) {
    String reg = "[A-Za-z0-9.]+([\\[\\s\\]]*|(<[A-Za-z0-9.]+>)*)\\s+[A-Za-z0-9]+\\s*(;|=)";

    Pattern pat = Pattern.compile(reg);
    Matcher matcher = pat.matcher(line);

    while (matcher.find()) {
      String matched = matcher.group().replaceAll(";", "")
          .replaceAll("=", "");
//      System.out.println("1th class : " + matched.trim().split(" ")[0]);
      classNames.add(matched.trim().split(" ")[0]);
    }
  }

  /**
   * Parameters
   * (Context context, AttributeSet attrs, int defStyle)
   */
  public static void listParameterClasses(List<String> names, String line) {
    line = replaceNoBlank(line, KEYWORDS_NON_ACCESS_MODIFIER);

    String reg = "[A-Za-z0-9]+\\s*\\([A-Za-z0-9.\\s,<>]+\\)";
    Pattern pat = Pattern.compile(reg);
    Matcher matcher = pat.matcher(line);

    while (matcher.find()) {
      String group = matcher.group();

      if (group.length() > 0) {
        String[] matchedArr = getParamsInBraces(group);

        for (String matched : matchedArr) {
          matched = matched.trim();

          if (matched.split(" ").length < 2) {
            continue;
          }

          if (KEYWORDS_PRIMITIVE_TYPE.contains(matched.split(" ")[0].replace(ARGUMENTS_SYNTAX, ""))) {
            // Removed primitive type keywords
          } else {
            String className = matched.trim().split(" ")[0];

            if (listClassWithGeneric(className) != null) {
              names.addAll(listClassWithGeneric(className));
            } else if (getInnerClass(className) != null) {
              names.add(getInnerClass(className));
            } else {
              names.add(className);
            }
          }
        }
      }
    }
//    System.out.println("classes : " + names.toString());
  }

  /**
   * Class.method()
   * Class.variable
   * Class.StaticInnerClass
   * but can not distinguish between Class.method() and class.method()
   */
  public static void listStaticClasses(List<String> classNames, String line) {
    String reg = "(\\s+[A-Za-z0-9]+\\s*\\.[A-Za-z0-9]+)(\\s|\\[|;|\\()";
    Pattern pat = Pattern.compile(reg);
    Matcher matcher = pat.matcher(line);

    while (matcher.find()) {
      String matched = matcher.group(1);

      String[] result = matched.trim().split("\\.");
      for (String className : result) {
        classNames.add(className);
      }
    }
  }

  private static String replaceNoBlank(String line, List<String> indentifierArr) {
    for (String identifier : indentifierArr) {
      line = line.replaceAll(identifier, "");
    }

    return line;
  }

  public static String[] getParamsInBraces(String str) {
    Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
    Matcher matcher = pattern.matcher(str);

    while (matcher.find()) {
      return matcher.group(1).split(",");
    }

    return null;
  }

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

  public static String getInnerClass(String className) {
    String[] classsArr = className.split("\\.");
    if (classsArr.length > 0 && classsArr[0] != null) {
      return classsArr[0];
    }

    return null;
  }
}
