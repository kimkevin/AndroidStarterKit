package com.androidstarterkit;

import com.androidstarterkit.config.AskConfig;
import com.androidstarterkit.exception.CommandException;

public class Console {

  public static void log(Exception e) {
    if (e instanceof CommandException) {
      CommandException exception = (CommandException) e;

      System.out.println(e.getMessage());

      if (Ask.env == AskConfig.DEVELOPMENT) {
        e.printStackTrace();
      }

      if (exception.shudShowHelp()) {
        System.out.println();
        printHelp();
      }
    }
  }

  static void printHelp() {
    System.out.println("Usage: ask [options] [dir]");
    System.out.println();
    System.out.println("Options:");
    System.out.println("    -l -layout <view>...     replace <widget> support: sv(ScrollView), rv(RecyclerView), lv(ListView), gv(GridView)");
    System.out.println("                             defaults to ScrollView");
    System.out.println("    -i, --icon               tab icon instead of text more than 2 widgets");
    System.out.println();
    System.out.println("    -t, —-tool <lib>...    add <lib> support: fa(FirebaseAnalytics), fc(FirebaseCrashReporting), st(Stetho),");
    System.out.println("                             an(Answers), cr(Crashlytics), lc(LeakCanary)");
    System.out.println();
    System.out.println("    -h, --help               output usage information");
    System.out.println();
  }
}
