package com.androidstarterkit.utils;

import com.androidstarterkit.CommandException;
import com.androidstarterkit.config.AskConfig;

public class Console {

  public static void log(Exception e) {
    if (e instanceof CommandException) {
      CommandException exception = (CommandException) e;

      System.out.println(e.getMessage());

      if (AskConfig.env == AskConfig.DEVELOPMENT) {
        e.printStackTrace();
      }

      if (exception.shudShowHelp()) {
        System.out.println();
        printHelp();
      }
    }
  }

  public static void printHelp() {
    System.out.println("Usage: ask [options] [dir]");
    System.out.println();
    System.out.println("Options:");
    System.out.println("First option must be a layout specifier");
    System.out.println("    -l -layout <widget>...   add <widget> support: sv(ScrollView), rv(RecyclerView), lv(ListView), gv(GridView)");
    System.out.println("                             defaults to ScrollView");
    System.out.println();
    System.out.println("    -h, --help               output usage information");
    System.out.println("    -i, --icon               tab icon instead of text more than 2 widgets");
    System.out.println();
  }
}
