package com.androidstarterkit.util;


import com.androidstarterkit.constraints.SyntaxConstraints;

import java.util.ArrayList;
import java.util.List;

public class SyntaxUtils {
  public static String createStartElement(String elemetName) {
    return "<" + elemetName + ">";
  }

  public static String createEndElement(String elementName) {
    return "</" + elementName + ">";
  }

  public static boolean hasStartElement(String codeLine, String resourceTypeName) {
    return codeLine.contains("<" + resourceTypeName);
  }

  public static boolean hasEndElement(String codeLine, String resourceTypeName) {
    return codeLine.contains("</" + resourceTypeName) || codeLine.contains("/>");
  }

  public static List<String> appendIndentForPrefix(List<String> codelines, int indentCount) {
    List<String> indentCodelines = new ArrayList<>();

    for (String codeline : codelines) {
      indentCodelines.add(appendIndentForPrefix(codeline, indentCount));
    }

    return indentCodelines;
  }

  public static String appendIndentForPrefix(String codeline, int indentCount) {
    String indent = "";
    for (int i = 0; i < indentCount; i++) {
      indent += SyntaxConstraints.DEFAULT_INDENT;
    }

    return indent + codeline;
  }

  public static String createIndentAsString(int depth) {
    String intent = "";
    for (int i = 0; i < depth; i++) {
      intent += "  ";
    }
    return intent;
  }
}
