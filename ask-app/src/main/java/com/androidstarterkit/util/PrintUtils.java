package com.androidstarterkit.util;

public class PrintUtils {
  public static String prefixDash(int depth) {
    String dash;

    if (depth == 0) {
      dash = "├─ ";
    } else {
      dash = "│";
    }

    for (int i = 0; i < depth; i++) {
      dash += "  ";
    }

    if (depth != 0) {
      dash += "└─ ";
    }

    return dash + (depth > 0 ? " " : "");
  }

  public static String getIntent(int depth) {
    String intent = "";
    for (int i = 0; i < depth; i++) {
      intent += "  ";
    }
    return intent;
  }
}
