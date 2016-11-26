package com.androidstarterkit.util;


public class SyntaxUtils {
  public static String createStartElement(String elemetName) {
    return "<" + elemetName + ">";
  }

  public static String createEndElement(String elementName) {
    return "</" + elementName + ">";
  }

  public static boolean hasLastElement(String codeLine, String resourceTypeName) {
    return codeLine.contains("</" + resourceTypeName) || codeLine.contains("/>");
  }
}
