package com.androidstarterkit;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourcePattern {
  public static Matcher matcherFileInJava(String codeLine) {
    final String reg = "R.("
        + ResourceType.LAYOUT
        + "|" + ResourceType.MENU
        + "|" + ResourceType.DRAWABLE
        + ").([A-Za-z0-1_]*)";

    Pattern pat = Pattern.compile(reg);
    return pat.matcher(codeLine);
  }

  public static Matcher matcherValuesInJava(String codeLine) {
    final String reg = "R.("
        + ValueType.STRING
        + "|" + ValueType.DIMEN
        + ").([A-Za-z0-1_]*)";

    Pattern pat = Pattern.compile(reg);
    return pat.matcher(codeLine);
  }

  public static Matcher matcherFileInXml(String codeLine) {
    final String reg = "@("
        + ResourceType.LAYOUT
        + "|" + ResourceType.MENU
        + "|" + ResourceType.DRAWABLE
        + ")/([A-Za-z0-1_]*)";

    Pattern pat = Pattern.compile(reg);
    return pat.matcher(codeLine);
  }

  public static Matcher matcherValuesInXml(String codeLine) {
    final String reg = "@("
        + ValueType.STYLE
        + "|" + ValueType.DIMEN
        + "|" + ValueType.STRING
        + ")/([A-Za-z0-1_.]*)";

    Pattern pat = Pattern.compile(reg);
    return pat.matcher(codeLine);
  }
}
