package com.androidstarterkit;

import com.androidstarterkit.cmd.CommandParser;
import com.androidstarterkit.cmd.TabType;
import com.androidstarterkit.cmd.WidgetType;
import com.androidstarterkit.modules.SampleModule;

public class Ask {

  public static void main(String[] args) {
    CommandParser commandParser = new CommandParser(args);

    if (commandParser.hasHelpCommand()) {
      printHelp();
      return;
    }

    String projectPath;
    WidgetType widgetType;
    TabType tabType;

    try {
      projectPath = commandParser.getPath();
      tabType = commandParser.getTabType();
      widgetType = commandParser.getWidgetType();
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    SampleModule sampleModule = SampleModule.load(projectPath)
        .with(tabType,
            widgetType,
            commandParser.getArguments());

    System.out.println("Run sample project with " + widgetType + " , path = " + sampleModule.getPath());
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
    System.out.println("    -t, --tab <tab>      add <tab> support (SlidingTabLayout | SlidingIconTabLayout)");
    System.out.println("                         (defaults to <tab> which has two fragment)");
    System.out.println();
    System.out.println("    args...              arguments should be <view> for adding to <tab>");
    System.out.println("                         use - for default <view>");
    System.out.println("Dir");
    System.out.println("    -p, --path           sample project path (defaults to ask-sample module)");
    System.out.println();
  }
}
