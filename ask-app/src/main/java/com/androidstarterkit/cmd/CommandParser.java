package com.androidstarterkit.cmd;

import com.androidstarterkit.CommandParseException;
import com.androidstarterkit.UnsupportedWidgetTypeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandParser {

  private List<String> argList;

  public CommandParser(String[] args) {
    argList = new ArrayList<>(Arrays.asList(args));
  }

  public String getPath() throws CommandParseException {
    return findOption(CommandOption.PATH_KEY, CommandOption.PATH_LONG_KEY);
  }

  public TabType getTabType() throws CommandParseException, UnsupportedWidgetTypeException {
    final String typeStr = findOption(CommandOption.TAB_KEY, CommandOption.TAB_LONG_KEY);

    if (typeStr == null) {
      return null;
    }

    if (typeStr.equals(TabType.SlidingTabLayout.getName())) {
      return TabType.SlidingTabLayout;
    } else if (typeStr.equals(TabType.SlidingIconTabLayout.getName())) {
      return TabType.SlidingIconTabLayout;
    } else {
      throw new CommandParseException("Unsupported " + typeStr + " for a tab type : please check -h , --help");
    }
  }

  public WidgetType getWidgetType() throws CommandParseException, UnsupportedWidgetTypeException {
    final String typeStr = findOption(CommandOption.WIDGET_KEY, CommandOption.WIDGET_LONG_KEY);

    if (typeStr == null) {
      return null;
    }

    if (typeStr.equals(WidgetType.RecyclerView.getName())) {
      return WidgetType.RecyclerView;
    } else if (typeStr.equals(WidgetType.ListView.getName())) {
      return WidgetType.ListView;
    } else {
      throw new CommandParseException("Unsupported " + typeStr + " for a widget type : please check -h , --help");
    }
  }

  public List<String> getArguments() {
    for (int i = 0, li = argList.size(); i < li; i++) {
      if (findOption(argList.get(i)) != null) {
        i++;
      } else {
        return Arrays.asList(argList.get(i).split(","));
      }
    }
    return new ArrayList<>();
  }

  public boolean hasHelpCommand() {
    return argList.contains(CommandOption.HELP_KEY)
            || argList.contains(CommandOption.HELP_LONG_KEY);
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
