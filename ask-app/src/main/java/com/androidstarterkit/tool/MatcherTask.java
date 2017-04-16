package com.androidstarterkit.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MatcherTask {
  private static final String TAG = MatcherTask.class.getSimpleName();

  private String input;
  private Pattern pattern;

  public interface MatchHandler {
    void handle(String group);
  }

  public MatcherTask(String regEx, String input) {
    pattern = Pattern.compile(regEx);
    this.input = input;
  }

  public void match(int groupIdx, MatchHandler matchListener) {
    Matcher matcher = pattern.matcher(input);

    while (matcher.find()) {
      matchListener.handle(matcher.group(groupIdx));
    }
  }
}
