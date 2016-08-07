package com.androidstarterkit;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassParser {

  /**
   * Variable Types : Primitive Data Type
   * and Arrays
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

  public static String ARGUMENT_SYNTAX = "...";

  public static List<String> getAll(File file) {
    return null;
  }

  public static List<String> getVariables(File file) {
    return null;
  }

  public static List<String> getClasses(File file) {
    return null;
  }

  public static List<String> getClassNames(String line) {
    List<String> names = new ArrayList<>();

    String newLine = line;
    newLine = replaceNoBlank(newLine, KEYWORDS_PRIMITIVE_TYPE);
    newLine = replaceNoBlank(newLine, KEYWORDS_JAVA_ACCESS_MODIFIER);
    newLine = replaceNoBlank(newLine, KEYWORDS_NON_ACCESS_MODIFIER);
    newLine = replaceNoBlank(newLine, KEYWORDS_RESERVED);

    /**
     *  Class.InnerClass class;
     *  Class.InnerClass class =
     *  Class class;
     *  Class class =
     *  Class<TypeClass> class;
     *  Class<TypeClass> class =
     */
    String reg = "[A-Za-z0-9.]+(<[A-Za-z0-9.]+>)*\\s+[A-Za-z0-9]+\\s*(;|=)";

    Pattern pat = Pattern.compile(reg);
    Matcher matcher = pat.matcher(newLine);

    while (matcher.find()) {
      String matched = matcher.group().replaceAll(";", "")
          .replaceAll("=", "");
//      System.out.println(matched.trim().split(" ")[0]);
      names.add(matched.trim().split(" ")[0]);
    }

    /**
     * extends Class, implements Interface
     */
    reg = "((implements|extends)\\s+)+[A-Za-z0-9]+\\s";
    pat = Pattern.compile(reg);
    matcher = pat.matcher(newLine);

    while (matcher.find()) {
      String matched = matcher.group();
//      System.out.println(matched.replaceAll("extends", "").replaceAll("interface", "").replaceAll(" ", ""));
      names.add(matched);
    }

    /**
     * Parameters
     * (Context context, AttributeSet attrs, int defStyle)
     */
    for (String returnType : KEYWORDS_RETURN_TYPE) {
      reg = returnType + "\\s[A-Za-z0-9]+\\s*\\([A-Za-z0-9.\\s,]+\\)+";
      pat = Pattern.compile(reg);
      matcher = pat.matcher(line);

      while (matcher.find()) {
        String matchedGroup = matcher.group();
        if (matchedGroup != null && matchedGroup.length() > 0) {
          String[] matchedArr = getStringBetweenBrackets(matchedGroup);
          for (String matched : matchedArr) {
            if (KEYWORDS_PRIMITIVE_TYPE.contains(matched.trim().split(" ")[0].replace(ARGUMENT_SYNTAX, ""))) {
//              System.out.println("removed : " + matched.trim());
            } else {
//              System.out.println(matched.trim().split(" ")[0]);
              names.add(matched.trim().split(" ")[0]);
            }
          }
        }
      }
    }

    return names;
  }

  private static String replaceNoBlank(String line, List<String> indentifierArr) {
    for (String identifier : indentifierArr) {
      line = line.replaceAll(identifier, "");
    }

    return line;
  }

  private static String[] getStringBetweenBrackets(String str) {
    Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
    Matcher matcher = pattern.matcher(str);

    while (matcher.find()) {
      return matcher.group(1).split(",");
    }

    return null;
  }
}
