package com.androidstarterkit;

import com.androidstarterkit.cmd.CommandParser;
import com.androidstarterkit.cmd.TabType;
import com.androidstarterkit.cmd.WidgetType;
import com.androidstarterkit.modules.SampleModule;
import com.androidstarterkit.utils.Console;

import java.util.List;

public class Ask {

  public static void main(String[] args) {
    CommandParser commandParser;
    try {
      commandParser = new CommandParser(args);
    } catch (CommandException e) {
      Console.log(e);
      return;
    }

    if (commandParser.hasHelpCommand()) {
      Console.printHelp();
      return;
    }

    final String projectPath = commandParser.getPath();
    final TabType tabType = commandParser.getTabType();
    final List<WidgetType> widgets = commandParser.getWidgets();

    SampleModule sampleModule = null;
    try {
      sampleModule = SampleModule
          .load(projectPath)
          .with(tabType, widgets);
    } catch (CommandException e) {
      Console.log(e);
    }

    System.out.println("Project path : " + sampleModule.getPath());
  }
}
