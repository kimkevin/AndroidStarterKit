package com.androidstarterkit.tool;


public interface ResourceExtractable {
  void extractResourceFileInJava(String input, ResourceMatcher.Handler handler);
  void extractValuesInJava(String codeLine, ResourceMatcher.Handler handler);
  void extractResourceFileInXml(String input, ResourceMatcher.Handler handler);
  void extractValuesInXml(String input, ResourceMatcher.Handler handler);
}
