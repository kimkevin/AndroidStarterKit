package com.androidstarterkit.tool;


import com.androidstarterkit.android.api.resource.ResourceType;
import com.androidstarterkit.android.api.resource.ValueType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ResourceMatcher {
  private final Matcher matcher;

  public interface Handler {
    void handle(String type, String name);
  }

  public ResourceMatcher(String input, MatchType matchType) {
    this.matcher = matchType.getMatchPattern().matcher(input);
  }

  public void match(Handler handler) {
    matcher.reset();
    while (matcher.find()) {
      handler.handle(matcher.group(1), matcher.group(2));
    }
  }

  public enum MatchType {
    RES_FILE_IN_JAVA("R.("
        + ResourceType.LAYOUT
        + "|" + ResourceType.MENU
        + "|" + ResourceType.DRAWABLE
        + ").([\\w_]*)"),

    RES_VALUE_IN_JAVA("R.("
        + ValueType.STRING
        + "|" + ValueType.DIMEN
        + ").([\\w_.]*)"),

    RES_FILE_IN_XML("@("
        + ResourceType.LAYOUT
        + "|" + ResourceType.MENU
        + "|" + ResourceType.DRAWABLE
        + ")/([\\w_]*)"),

    RES_VALUE_IN_XML("@("
        + ValueType.STYLE
        + "|" + ValueType.DIMEN
        + "|" + ValueType.STRING
        + ")/([\\w_.]*)");

    private Pattern matchPattern;

    MatchType(String regex) {
      this.matchPattern = Pattern.compile(regex);
    }

    public Pattern getMatchPattern() {
      return matchPattern;
    }
  }
}
