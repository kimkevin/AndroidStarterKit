package com.androidstarterkit.util;


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
}
