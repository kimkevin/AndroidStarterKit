package com.androidstarterkit;

import com.androidstarterkit.cmd.CommandParser;
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
    WidgetType type;

    try {
      projectPath = commandParser.getPath();
      type = commandParser.getWidgetType();
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    SampleModule sampleModule = SampleModule.load(projectPath).with(type);

    System.out.println("Run sample project with " + type + " , path = " + sampleModule.getPath());
  }

  public static void printHelp() {
    System.out.println();
    System.out.println("Usage: AndroidStater <options> <dir>");
    System.out.println();

    System.out.println("Options:");
    System.out.println();
    System.out.println("    -h, --help                  output usage information");
    System.out.println("    -w, --widget <view>         add <view> support (RecyclerView, ListView) (defaults to RecyclerView)");
    System.out.println();

    System.out.println("Dir:");
    System.out.println();
    System.out.println("    -p, --path                  source project path (defaults to sample project path)");
    System.out.println();
  }
}
