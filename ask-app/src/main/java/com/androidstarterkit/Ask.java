package com.androidstarterkit;

import com.androidstarterkit.cmd.WidgetType;
import com.androidstarterkit.modules.SampleModule;
import com.androidstarterkit.utils.FileUtil;

public class Ask {
  private static boolean isUsedProgramArg = false;

  public static void main(String[] args) {
    String projectPath;

    com.androidstarterkit.cmd.CommandParser commandParser = new com.androidstarterkit.cmd.CommandParser(args);

    if (commandParser.hasHelpCommand()) {
      printHelp();
      return;
    }

    projectPath = FileUtil.linkPathWithSlash(FileUtil.getRootPath(), "AndroidSample");

    WidgetType type = WidgetType.RecyclerView;

    if (!isUsedProgramArg) {
      try {
        projectPath = commandParser.getPath();
        type = commandParser.getWidgetType();
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }
    }

    SampleModule
        .load(projectPath)
        .with(type);

    System.out.println("Run sample project with " + type);
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
    System.out.println("    -p, --path                  source project path (defaults to new project)");
    System.out.println();
  }
}
