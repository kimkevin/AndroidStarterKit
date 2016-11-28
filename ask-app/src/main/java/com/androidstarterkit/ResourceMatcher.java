package com.androidstarterkit;


import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceMatcher {
  private Matcherable matcherable;
  private Matcher matcher;

  public interface Matcherable {

  }

  public interface JavaFileMatcher extends Matcherable {
    void matched(String resourceTypeName, String layoutName) throws FileNotFoundException;
  }

  public interface JavaValueMatcher extends Matcherable {
    void matched(String resourceTypeName, String elementName) throws FileNotFoundException;
  }

  public interface XmlFileMatcher extends Matcherable {
    void matched(String resourceTypeName, String layoutName) throws FileNotFoundException;
  }

  public interface XmlValueMatcher extends Matcherable {
    void matched(String resourceTypeName, String elementName) throws FileNotFoundException;
  }

  public ResourceMatcher(String codeLine, JavaFileMatcher matcherable) {
    final String reg = "R.("
        + ResourceType.LAYOUT
        + "|" + ResourceType.MENU
        + "|" + ResourceType.DRAWABLE
        + ").([A-Za-z0-1_]*)";

    init(reg, codeLine, matcherable);
  }

  public ResourceMatcher(String codeLine, JavaValueMatcher matcherable) {
    final String reg = "R.("
        + ValueType.STRING
        + "|" + ValueType.DIMEN
        + ").([A-Za-z0-1_]*)";

    init(reg, codeLine, matcherable);
  }

  public ResourceMatcher(String codeLine, XmlFileMatcher matcherable) {
    final String reg = "@("
        + ResourceType.LAYOUT
        + "|" + ResourceType.MENU
        + "|" + ResourceType.DRAWABLE
        + ")/([A-Za-z0-1_]*)";

    init(reg, codeLine, matcherable);
  }

  public ResourceMatcher(String codeLine, XmlValueMatcher matcherable) {
    final String reg = "@("
        + ValueType.STYLE
        + "|" + ValueType.DIMEN
        + "|" + ValueType.STRING
        + ")/([A-Za-z0-1_.]*)";

    init(reg, codeLine, matcherable);
  }

  private void init(String reg, String codeLine, Matcherable matcherable) {
    Pattern pattern = Pattern.compile(reg);
    this.matcher = pattern.matcher(codeLine);
    this.matcherable = matcherable;
  }

  public void match() {
    while (matcher.find()) {
      group();
    }
  }

  private void group() {
    try {
      if (matcherable instanceof JavaFileMatcher) {
        ((JavaFileMatcher) matcherable).matched(matcher.group(1), matcher.group(2));
      } else if (matcherable instanceof JavaValueMatcher) {
        ((JavaValueMatcher) matcherable).matched(matcher.group(1), matcher.group(2));
      } else if (matcherable instanceof XmlFileMatcher) {
        ((XmlFileMatcher) matcherable).matched(matcher.group(1), matcher.group(2));
      } else if (matcherable instanceof XmlValueMatcher) {
        ((XmlValueMatcher) matcherable).matched(matcher.group(1), matcher.group(2));
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

//  public static Matcher createFileMatcherForJava(String codeLine) {
//    final String reg = "R.("
//        + ResourceType.LAYOUT
//        + "|" + ResourceType.MENU
//        + "|" + ResourceType.DRAWABLE
//        + ").([A-Za-z0-1_]*)";
//
//    Pattern pat = Pattern.compile(reg);
//    return pat.matcher(codeLine);
//  }
//
//  public static Matcher createValueMatcherForJava(String codeLine) {
//    final String reg = "R.("
//        + ValueType.STRING
//        + "|" + ValueType.DIMEN
//        + ").([A-Za-z0-1_]*)";
//
//    Pattern pat = Pattern.compile(reg);
//    return pat.matcher(codeLine);
//  }
//
//  public static Matcher createFileMatcherForXml(String codeLine) {
//    final String reg = "@("
//        + ResourceType.LAYOUT
//        + "|" + ResourceType.MENU
//        + "|" + ResourceType.DRAWABLE
//        + ")/([A-Za-z0-1_]*)";
//
//    Pattern pat = Pattern.compile(reg);
//    return pat.matcher(codeLine);
//  }
//
//  public static Matcher createValueMatcherForXml(String codeLine) {
//    final String reg = "@("
//        + ValueType.STYLE
//        + "|" + ValueType.DIMEN
//        + "|" + ValueType.STRING
//        + ")/([A-Za-z0-1_.]*)";
//
//    Pattern pat = Pattern.compile(reg);
//    return pat.matcher(codeLine);
//  }
}
