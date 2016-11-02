package com.androidstarterkit;

import com.androidstarterkit.cmd.CommandParser;
import com.androidstarterkit.cmd.TabType;
import com.androidstarterkit.cmd.WidgetType;
import com.androidstarterkit.modules.SampleModule;

import java.util.List;

public class Ask {

  public static void main(String[] args) throws CommandParseException {
    CommandParser commandParser= new CommandParser(args);

    if (commandParser.hasHelpCommand()) {
      printHelp();
      return;
    }

    String projectPath = commandParser.getPath();
    TabType tabType = commandParser.getTabType();
    List<WidgetType> widgets = commandParser.getWidgets();

    SampleModule sampleModule = SampleModule
        .load(projectPath)
        .with(tabType, widgets);

    System.out.println("Project path : " + sampleModule.getPath());
  }

  private static void printHelp() {
    System.out.println("Usage: ask [options] [dir]");
    System.out.println();
    System.out.println("Options:");
    System.out.println("First option must be a layout specifier");
    System.out.println("    -l -layout <widget>...   add <widget> support : rv(RecyclerView), lv(ListView), sv(ScrollView), -(ScrollView)");
    System.out.println();
    System.out.println("    -h, --help               output usage information");
    System.out.println("    -i, --icon               tab icon instead of text more than 2 widgets");
    System.out.println();
  }
}
