package com.androidstarterkit.cmd;

import com.androidstarterkit.CommandParseException;
import com.androidstarterkit.UnsupportedWidgetTypeException;

import java.util.*;

public class CommandParser {

  private List<String> argList;

  public CommandParser(String[] args) {
    argList = new ArrayList<>(Arrays.asList(args));
  }

  public String getPath() throws CommandParseException {
    final String option = findOption(com.androidstarterkit.cmd.CommandOption.PATH_KEY, com.androidstarterkit.cmd.CommandOption.PATH_LONG_KEY);

    if (option == null) {
      throw new CommandParseException("Missing a project path : please check -p <path>");
    }
    return option;
  }

  public com.androidstarterkit.cmd.WidgetType getWidgetType() throws CommandParseException, UnsupportedWidgetTypeException {
    final String typeStr = findOption(com.androidstarterkit.cmd.CommandOption.WIDGET_KEY, com.androidstarterkit.cmd.CommandOption.WIDGET_LONG_KEY);

    if (typeStr == null) {
      throw new CommandParseException("Missing a widget type : please check -w <widget>");
    }

    if (typeStr.equals(com.androidstarterkit.cmd.WidgetType.RecyclerView.getName())) {
      return com.androidstarterkit.cmd.WidgetType.RecyclerView;
    } else if (typeStr.equals(com.androidstarterkit.cmd.WidgetType.ListView.getName())) {
      return com.androidstarterkit.cmd.WidgetType.ListView;
    } else if (typeStr.equals(com.androidstarterkit.cmd.WidgetType.SlidingTabLayout.getName())) {
      return com.androidstarterkit.cmd.WidgetType.SlidingTabLayout;
    } else if (typeStr.equals(com.androidstarterkit.cmd.WidgetType.SlidingIconTabLayout.getName())) {
      return com.androidstarterkit.cmd.WidgetType.SlidingIconTabLayout;
    } else {
      throw new CommandParseException("Unsupported a widget type : please check -h , --help");
    }
  }

  public boolean hasHelpCommand() {
    return argList.contains(com.androidstarterkit.cmd.CommandOption.HELP_KEY)
            || argList.contains(com.androidstarterkit.cmd.CommandOption.HELP_LONG_KEY);
  }

  private String findOption(String... key) {
    for (int i = 0, li = argList.size(); i < li; i++) {
      if (contain(argList.get(i), key)) {
        if (i < argList.size() - 1) {
          return argList.get(i + 1);
        }
      }
    }

    return null;
  }

  private boolean contain(String arg, String... keys) {
    for (String key : keys) {
      if (arg.equals(key)) {
        return true;
      }
    }

    return false;
  }
}
