package com.androidstarterkit.tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassParser {
  private static final String TAG = ClassParser.class.getSimpleName();

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
      "class", "void", "return", "enum", "super", "package", "import", "Class"
  );

  public static String ARGUMENTS_SYNTAX = "...";

  private static final String CLASS_DECLARATION_REGEX = "((\\w+\\s*(<[^>]+>+)?)\\s*(\\.\\s*\\w+\\s*(<[^>]+>+)?)*)";
  private static final String CLASS_VARIABLE_DECLARATION_REGEX = "\\s*([\\w<>.,\\s]+)(\\[\\s*\\])?\\s+\\w+\\s*(\\[\\s*\\])?\\s*";

  private static final String DECLARED_CLASS_VARIABLE_REGEX = CLASS_VARIABLE_DECLARATION_REGEX + "(;|=)";

  /**
   * Get {@link ClassInfo} from Java file.
   *
   * @param file Java file
   * @return Array of ClassInfo
   */
  public static List<ClassInfo> extractClasses(File file) {
    Scanner scanner;
    List<ClassInfo> classNames = new ArrayList<>();

    try {
      scanner = new Scanner(file);

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        classNames.addAll(extractClasses(line));
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    return classNames;
  }

  /**
   * Get ClassInfo from code line.
   *
   * @param codeline String for code line
   * @return Array of ClassInfo
   */
  public static List<ClassInfo> extractClasses(String codeline) {
    List<ClassInfo> classInfos = new ArrayList<>();

    codeline = removeComment(codeline);

    classInfos.addAll(listConstructedClass(codeline));
    classInfos.addAll(listInheritanceClass(codeline));
    classInfos.addAll(listInterfaceClass(codeline));
    classInfos.addAll(listVariableClass(codeline));
    classInfos.addAll(listParameterClass(codeline));
    classInfos.addAll(listDotClass(codeline));
    classInfos.addAll(listStaticMethodOfClass(codeline));
    return classInfos;
  }

  /**
   * Get an instanced classes.
   */
  public static List<ClassInfo> listConstructedClass(String codeline) {
    List<ClassInfo> classNames = new ArrayList<>();
    String reg = "new\\s+([\\w]+)";
    Pattern pattern = Pattern.compile(reg);
    Matcher matcher = pattern.matcher(codeline);

    while (matcher.find()) {
      String classStr = matcher.group(1);
      classNames.add(new ClassInfo(classStr));
    }

    return classNames;
  }

  /**
   * Get inheritance classes.
   */
  public static List<ClassInfo> listInheritanceClass(String codeline) {
    List<ClassInfo> classInfos = new ArrayList<>();

    Pattern pat = Pattern.compile("extends\\s+((\\w+\\s*(<[^>]+>+)?)\\s*(\\.\\s*\\w+\\s*(<[^>]+>+)?)*)");
    Matcher matcher = pat.matcher(codeline);

    while (matcher.find()) {
      final String classStr = matcher.group(1).trim();
      classInfos.add(new ClassInfo(classStr));
    }

    return classInfos;
  }

  /**
   * Get interfaces from code line.
   */
  public static List<ClassInfo> listInterfaceClass(String codeline) {
    List<ClassInfo> classInfos = new ArrayList<>();

    Pattern pattern = Pattern.compile("implements\\s+(.+)\\s*\\{?");
    Matcher matcher = pattern.matcher(codeline);

    while (matcher.find()) {
      final String interfaceCodeline = matcher.group(1).trim();
      for (String classStr : CodeSpliter.split(interfaceCodeline, ',')) {
        classInfos.add(splitClasses(classStr));
      }
    }

    return classInfos;
  }

  /**
   * Get field variables in class and local variables in method.
   * But it should have `;` or `=` for the end of code code.
   */
  public static List<ClassInfo> listVariableClass(String codeline) {
    return listVariableClass(codeline, true);
  }

  public static List<ClassInfo> listVariableClass(String codeline, boolean hasSemicolonOrAssignment) {
    Pattern pat = Pattern.compile(hasSemicolonOrAssignment ?
        DECLARED_CLASS_VARIABLE_REGEX : CLASS_VARIABLE_DECLARATION_REGEX);
    Matcher matcher = pat.matcher(codeline);

    List<ClassInfo> classInfos = new ArrayList<>();

    while (matcher.find()) {
      final String classStr = matcher.group(1);

      ClassInfo classInfo = splitClasses(classStr);
      if (classInfo != null) {
        classInfos.add(classInfo);
      }
    }

    return classInfos;
  }

  /**
   * Get generic type classes.
   */
  public static List<ClassInfo> listGenericTypeClass(String codeline) {
    List<ClassInfo> variableClassInfos = listVariableClass(codeline);

    List<ClassInfo> genericTypeClassInfos = new ArrayList<>();
    for (ClassInfo classInfo : variableClassInfos) {
      genericTypeClassInfos.addAll(classInfo.getGenericTypeClassInfos());
    }

    return genericTypeClassInfos;
  }

  /**
   * Get declared classes as parameters.
   */
  public static List<ClassInfo> listParameterClass(String codeline) {
    List<ClassInfo> classInfos = new ArrayList<>();

    if (!isMethodDeclaration(codeline)) {
      return classInfos;
    }

    codeline = replaceIdentifiers(codeline, KEYWORDS_NON_ACCESS_MODIFIER);

    Pattern pat = Pattern.compile("[\\w]+\\s*\\((.+)\\)");
    Matcher matcher = pat.matcher(codeline);

    while (matcher.find()) {
      final String paramGroupStr = matcher.group(1);

      List<String> paramClassNames = CodeSpliter.split(paramGroupStr, ',');
      for (String paramClassStr : paramClassNames) {
        // removed ... parameter syntax
        paramClassStr = paramClassStr.replace(ARGUMENTS_SYNTAX, "")
            .replace("new", "");

        if (!isStringDeclaration(paramClassStr)) {
          classInfos.addAll(listVariableClass(paramClassStr, false));
        }
      }
    }

    return classInfos;
  }

  /**
   * Get classes which have static method or variable.
   */
  public static List<ClassInfo> listStaticMethodOfClass(String codeline) {
    codeline = replaceIdentifiers(codeline, KEYWORDS_RESERVED);

    List<ClassInfo> classInfos = new ArrayList<>();

    String reg = CLASS_DECLARATION_REGEX + "(\\.\\w+\\s*\\(.*\\))+\\s*;";
    Pattern pat = Pattern.compile(reg);
    Matcher matcher = pat.matcher(codeline);

    while (matcher.find()) {
      final String classStr = matcher.group(1).trim();

      classInfos.add(splitClasses(classStr));
    }

    return classInfos;
  }

  /**
   * Get classes which are defined as Class.class.
   */
  public static List<ClassInfo> listDotClass(String codeline) {
    List<ClassInfo> classInfos = new ArrayList<>();
    Matcher matcher = Pattern.compile("(\\w+\\s*(\\.\\w+)*)\\.\\s*class").matcher(codeline);

    while (matcher.find()) {
      final String classStr = matcher.group(1).trim();

      classInfos.add(splitClasses(classStr));
    }

    return classInfos;
  }

  /**
   * ClassInfo computed by splitting the delimiting comma(.) for nested class.
   *
   * @param declaredClassName String of class declaration
   * @return ClassInfo
   */
  public static ClassInfo splitClasses(String declaredClassName) {
    List<String> classStrList = CodeSpliter.split(declaredClassName, '.');
    ClassInfo classInfo = null;

    for (String classStr : classStrList) {
      if (PRIMITIVE_DATA_TYPE.contains(classStr) || KEYWORDS_RESERVED.contains(classStr)) {
        continue;
      }

      ClassInfo nestedClassStr = splitClass(classStr, 0);

      if (classInfo == null) {
        classInfo = nestedClassStr;
      } else {
        ClassInfo parentClassInfo = classInfo;
        while (parentClassInfo.getNestedClassInfo() != null) {
          parentClassInfo = parentClassInfo.getNestedClassInfo();
        }

        parentClassInfo.setNestedClassInfo(nestedClassStr);
      }
    }

    return classInfo;
  }

  /**
   * ClassInfo computed by the delimiting regular expression for nested class and generic type classes.
   *
   * @param declaredClassName String of nested class declaration
   * @param depth Depth for debugging
   * @return ClassInfo
   */
  private static ClassInfo splitClass(String declaredClassName, int depth) {
    Pattern pat = Pattern.compile("\\s*(\\w+)\\s*(<(.+)>)?");
    Matcher matcher = pat.matcher(declaredClassName);

    ClassInfo classInfo = null;

    while (matcher.find()) {
      final String classStr = matcher.group(1);
      classInfo = new ClassInfo(classStr);

      final String genericTypeClassCodeLine = matcher.group(3);
      if (genericTypeClassCodeLine != null) {
        List<String> genericTypeClassStrList = CodeSpliter.split(genericTypeClassCodeLine, ',');

        for (String genericTypeClassStr : genericTypeClassStrList) {
          if (hasGenericTypeClass(genericTypeClassStr)) {
            classInfo.addGenericTypeClassInfo(splitClass(genericTypeClassStr, depth + 1));
          } else {
            if (CodeSpliter.split(genericTypeClassStr, '.').size() > 1) {
              classInfo.addGenericTypeClassInfo(splitClasses(genericTypeClassStr));
            } else {
              classInfo.addGenericTypeClassInfo(new ClassInfo(genericTypeClassStr));
            }
          }
        }
      }
    }

    return classInfo;
  }

  private static boolean hasGenericTypeClass(String codeline) {
    final String GENERIC_TYPE_REGEX = "<(.+)>";

    Matcher matcher = Pattern.compile(GENERIC_TYPE_REGEX).matcher(codeline);
    return matcher.find();
  }

  private static boolean isStringDeclaration(String codeline) {
    final String GENERIC_TYPE_REGEX = "\"(.+)\"";

    Matcher matcher = Pattern.compile(GENERIC_TYPE_REGEX).matcher(codeline.trim());
    return matcher.find();
  }

  /**
   * Check method declaration
   *
   * @param codeline String of code line
   * @return boolean if it has brackets
   */
  private static boolean isMethodDeclaration(String codeline) {
    int stackCnt = 0;
    for (int i = 0, li = codeline.length(); i < li; i++) {
      char c = codeline.toCharArray()[i];
      if (c == '(') {
        stackCnt++;
      } else if (c == ')') {
        stackCnt--;
      }
    }

    return stackCnt == 0;
  }

  /**
   * Remove comment
   *
   * @param codeline String of code line
   * @return String of code line of which comment is removed
   */
  private static String removeComment(String codeline) {
    int index = codeline.indexOf("//");

    return index > 0 ? codeline.substring(0, index) : codeline;
  }

  /**
   * Replace list of identifier to no blank
   *
   * @param codeline String for code codeline
   * @param identifierList List of identifier
   * @return String that was removed to identifier
   */
  private static String replaceIdentifiers(String codeline, List<String> identifierList) {
    for (String identifier : identifierList) {
      codeline = codeline.replaceAll(identifier, "");
    }

    return codeline;
  }
}
