package com.androidstarterkit.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MatcherTask {
  private static final String TAG = MatcherTask.class.getSimpleName();

  private String input;
  private Pattern pattern;

  public interface OnMatchListener {
    void onMatched(Matcher matcher);
  }

  public MatcherTask(String regEx, String input) {
    pattern = Pattern.compile(regEx);
    this.input = input;
  }

  public void start(OnMatchListener matchListener) {
    Matcher matcher = pattern.matcher(input);

    while (matcher.find()) {
      matchListener.onMatched(matcher);
    }
  }
}
