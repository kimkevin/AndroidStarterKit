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
      "class", "void", "return", "enum", "super", "package", "import"
  );

  public static String ARGUMENTS_SYNTAX = "...";

  private static final String CLASS_VARIABLE_DECLARATION_REGEX = "\\s*([\\w<>.,\\s]+)(\\[\\s*\\])?\\s+\\w+\\s*(\\[\\s*\\])?\\s*";
  private static final String DECLARED_CLASS_VARIABLE_REGEX = CLASS_VARIABLE_DECLARATION_REGEX + "(;|=)";

  /**
   * Get class names from code file
   *
   * @param file code file
   * @return String array for class name
   */
  public static List<ClassInfo> getClasses(File file) {
    Scanner scanner;
    List<ClassInfo> classNames = new ArrayList<>();

    try {
      scanner = new Scanner(file);

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        classNames.addAll(getClasses(line));
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
  public static List<ClassInfo> getClasses(String line) {
    List<ClassInfo> classInfos = new ArrayList<>();

    classInfos.addAll(listConstructedClass(line));
    classInfos.addAll(listInheritClass(line));
    classInfos.addAll(listVariableClass(line));

    String replacedLine = line;
    replacedLine = replaceIdentifiers(replacedLine, PRIMITIVE_DATA_TYPE);
    replacedLine = replaceIdentifiers(replacedLine, KEYWORDS_JAVA_ACCESS_MODIFIER);
    replacedLine = replaceIdentifiers(replacedLine, KEYWORDS_NON_ACCESS_MODIFIER);
    replacedLine = replaceIdentifiers(replacedLine, KEYWORDS_RESERVED);

    classInfos.addAll(listClassWithStatic(replacedLine));
    classInfos.addAll(listParameterClass(replacedLine));

    return classInfos;
  }

  /**
   * Get class names when make a instance
   * ex) new Class
   *
   * @param codeline String for code codeline
   * @return String array for class name
   */
  public static List<ClassInfo> listConstructedClass(String codeline) {
    List<ClassInfo> classNames = new ArrayList<>();
    String reg = "new\\s+([\\w]+)";
    Pattern pat = Pattern.compile(reg);
    Matcher matcher = pat.matcher(codeline);

    while (matcher.find()) {
      String matched = matcher.group(1);
      classNames.add(new ClassInfo(matched));
    }

    return classNames;
  }

  /**
   * Get class names for inherit class
   * ex) extends Class, implements Interface
   *
   * @param codeline String for code codeline
   * @return String array for class name
   */
  public static List<ClassInfo> listInheritClass(String codeline) {
    List<ClassInfo> classNames = new ArrayList<>();

    Pattern pat = Pattern.compile("extends\\s+((\\w+\\s*(<[^>]+>+)?)\\s*(\\.\\s*\\w+\\s*(<[^>]+>+)?)*)");
    Matcher matcher = pat.matcher(codeline);

    while (matcher.find()) {
      final String extendCodeline = matcher.group(1).trim();
      classNames.add(new ClassInfo(extendCodeline));
    }

    pat = Pattern.compile("implements\\s+(.+)\\{?");
    matcher = pat.matcher(codeline);

    while (matcher.find()) {
      final String implementCodeline = matcher.group(1).trim();
      for (String className : CodeSpliter.split(implementCodeline, ',')) {
        classNames.add(splitClasses(className));
      }
    }

    return classNames;
  }

  /**
   * Get class names as field variables which can be instance variable(Non-Static Fields)
   * and class variable(Static Fields)
   * We need `;` or `=` on the end of code
   *
   * @param codeline String for code codeline
   * @return List of {@link ClassInfo}
   */
  public static List<ClassInfo> listVariableClass(String codeline) {
    return listVariableClass(codeline, false);
  }

  public static List<ClassInfo> listVariableClass(String codeline, boolean hasSemicolonOrAssignment) {
    Pattern pat = Pattern.compile(hasSemicolonOrAssignment ?
        CLASS_VARIABLE_DECLARATION_REGEX : DECLARED_CLASS_VARIABLE_REGEX);
    Matcher matcher = pat.matcher(codeline);

    List<ClassInfo> classInfos = new ArrayList<>();

    while (matcher.find()) {
      final String classVariableDeclarationStr = matcher.group(1);

      ClassInfo classInfo = splitClasses(classVariableDeclarationStr);
      if (classInfo != null) {
        classInfos.add(classInfo);
      }
    }

    return classInfos;
  }

  public static List<ClassInfo> listGenericTypeClass(String codeline) {
    List<ClassInfo> variableClassInfos = listVariableClass(codeline);

    List<ClassInfo> genericTypeClassInfos = new ArrayList<>();
    for (ClassInfo classInfo : variableClassInfos) {
      genericTypeClassInfos.addAll(classInfo.getGenericTypeClassInfos());
    }

    return genericTypeClassInfos;
  }

  /**
   * Get class names as parameters
   * ex) (Context context, AttributeSet attrs, int defStyle)
   *
   * @param codeline String for code codeline
   * @return String array for class name
   */
  public static List<ClassInfo> listParameterClass(String codeline) {
    List<ClassInfo> classInfos = new ArrayList<>();

    codeline = replaceIdentifiers(codeline, KEYWORDS_NON_ACCESS_MODIFIER);

    Pattern pat = Pattern.compile("[\\w]+\\s*\\((.+)\\)");
    Matcher matcher = pat.matcher(codeline);

    while (matcher.find()) {
      String group = matcher.group(1);

      List<String> paramClassNames = CodeSpliter.split(group, ',');
      for (String paramStr : paramClassNames) {
        // removed ... parameter syntax
        paramStr = paramStr.replace(ARGUMENTS_SYNTAX, "");

        classInfos.addAll(listVariableClass(paramStr, true));
      }
    }

    return classInfos;
  }

  /**
   * Get class names which have static method or variable
   * <p>
   * ex)
   * Class.method();
   * Class.method(Parameters);
   * Class.class
   * <p>
   * warning : It can not distinguish between Class.method() and class.method()
   *
   * @param codeline String for code codeline
   * @return String array for class name
   */
  public static List<ClassInfo> listClassWithStatic(String codeline) {
    List<ClassInfo> classInfos = new ArrayList<>();

    String reg = "\\s*([\\w]+)\\s*.\\s*(([\\w]+\\s*(\\([\\w]*\\)))|class)";
    Pattern pat = Pattern.compile(reg);
    Matcher matcher = pat.matcher(codeline);

    while (matcher.find()) {
      String matched = matcher.group(1).trim();

      classInfos.add(new ClassInfo(matched));
    }

    return classInfos;
  }

  public static ClassInfo splitClasses(String declaredClassName) {
    List<String> classInfoStrList = CodeSpliter.split(declaredClassName, '.');
    ClassInfo classInfo = null;

    for (String classInfoStr : classInfoStrList) {
      if (PRIMITIVE_DATA_TYPE.contains(classInfoStr) || KEYWORDS_RESERVED.contains(classInfoStr)) {
        continue;
      }

      ClassInfo nestedClassInfo = splitClass(classInfoStr, 0);

      if (classInfo == null) {
        classInfo = nestedClassInfo;
      } else {
        ClassInfo parentClassInfo = classInfo;
        while (parentClassInfo.getNestedClassInfo() != null) {
          parentClassInfo = parentClassInfo.getNestedClassInfo();
        }

        parentClassInfo.setNestedClassInfo(nestedClassInfo);
      }
    }

    return classInfo;
  }

  /**
   *
   * @param declaredClassName
   * @param depth
   * @return
   */
  private static ClassInfo splitClass(String declaredClassName, int depth) {
    Pattern pat = Pattern.compile("\\s*(\\w+)\\s*(<(.+)>)?");
    Matcher matcher = pat.matcher(declaredClassName);

    ClassInfo classInfo = null;

    while (matcher.find()) {
      final String className = matcher.group(1);
      classInfo = new ClassInfo(className);

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

  private static boolean hasGenericTypeClass(String classCodeline) {
    final String GENERIC_TYPE_REGEX = "\\<(.+)\\>";

    Matcher matcher = Pattern.compile(GENERIC_TYPE_REGEX).matcher(classCodeline);
    return matcher.find();
  }

  /**
   * Replace list of identifier to no blank
   *
   * @param line           String for code line
   * @param identifierList list of identifiers
   * @return String that was removed to identifiers
   */
  private static String replaceIdentifiers(String line, List<String> identifierList) {
    for (String identifier : identifierList) {
      line = line.replaceAll(identifier, "");
    }

    return line;
  }
}
