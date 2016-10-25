package com.androidstarterkit;

import com.androidstarterkit.cmd.CommandParser;
import com.androidstarterkit.cmd.TabType;
import com.androidstarterkit.modules.SampleModule;

public class Ask {

  public static void main(String[] args) {
    CommandParser commandParser = null;
    try {
      commandParser = new CommandParser(args);
    } catch (CommandParseException e) {
      e.printStackTrace();
    }

    if (commandParser.hasHelpCommand()) {
      printHelp();
      return;
    }

    String projectPath;
    TabType tabType;

    try {
      projectPath = commandParser.getPath();
      tabType = commandParser.getTabType();
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    SampleModule sampleModule = SampleModule.load(projectPath)
        .with(tabType, commandParser.getWidgets());

    System.out.println("Project path : " + sampleModule.getPath());
  }

  private static void printHelp() {
    System.out.println();
    System.out.println("Usage: ./ask [options] [dir]");
    System.out.println("       ./ask [-w <widget>] [dir]");
    System.out.println("       ./ask [-c <container>] [dir] [args...]");
    System.out.println();

    System.out.println("Options:");
    System.out.println("    -h, --help           output usage information");
    System.out.println("    -w, --widget <view>  add <view> support (RecyclerView | ListView) (defaults to RecyclerView)");
    System.out.println("    -t, --tab <tab>      add <tab> support (SlidingTab | SlidingIconTab)");
    System.out.println("                         (defaults to <tab> which has two fragment)");
    System.out.println();
    System.out.println("    args...              arguments should be <view> for adding to <tab>");
    System.out.println("                         use - for default <view>");
    System.out.println("Dir");
    System.out.println("    -p, --path           sample project path (defaults to ask-sample module)");
    System.out.println();
  }
}
