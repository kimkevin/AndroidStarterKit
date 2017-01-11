package com.androidstarterkit.tool;


import java.util.ArrayList;
import java.util.List;

public class CodeSpliter {

  public static List<String> split(String str, char regex) {
    List<String> strList = new ArrayList<>();
    String splitStr = "";
    int stackCnt = 0;
    for (int i = 0, li = str.length(); i < li; i++) {
      char c = str.toCharArray()[i];
      if (c == '<') {
        stackCnt++;
      } else if (c == '>') {
        stackCnt--;
      }

      if (c != regex || stackCnt > 0) {
        splitStr += c;
      }

      if ((c == regex && stackCnt <= 0) || i == str.length() - 1) {
        strList.add(splitStr.trim());
        splitStr = "";
      }
    }

    return strList;
  }
}
