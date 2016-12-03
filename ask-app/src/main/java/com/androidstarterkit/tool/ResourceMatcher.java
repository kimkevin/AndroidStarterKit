package com.androidstarterkit.tool;


import com.androidstarterkit.api.resource.ResourceType;
import com.androidstarterkit.api.resource.ValueType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceMatcher {
  private final Matcher matcher;

  public interface Handler {
    void handle(String type, String name);
  }

  public ResourceMatcher(String codeLine, MatchType matchType) {
    this.matcher = matchType.getMatchPattern().matcher(codeLine);
  }

  public void match(Handler handler) {
    matcher.reset();
    while (matcher.find()) {
      handler.handle(matcher.group(1), matcher.group(2));
    }
  }

  public enum MatchType {
    JAVA_FILE("R.("
            + ResourceType.LAYOUT
            + "|" + ResourceType.MENU
            + "|" + ResourceType.DRAWABLE
            + ").([A-Za-z0-1_]*)"),

    JAVA_VALUE("R.("
            + ValueType.STRING
            + "|" + ValueType.DIMEN
            + ").([A-Za-z0-1_]*)"),

    XML_FILE("@("
            + ResourceType.LAYOUT
            + "|" + ResourceType.MENU
            + "|" + ResourceType.DRAWABLE
            + ")/([A-Za-z0-1_]*)"),

    XML_VALUE("@("
            + ValueType.STYLE
            + "|" + ValueType.DIMEN
            + "|" + ValueType.STRING
            + ")/([A-Za-z0-1_.]*)");

    private Pattern matchPattern;
    MatchType(String regex) {
      this.matchPattern = Pattern.compile(regex);
    }

    public Pattern getMatchPattern() {
      return matchPattern;
    }
  }
}
