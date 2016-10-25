package com.androidstarterkit.cmd;

import com.androidstarterkit.CommandParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {

  private String path;
  private boolean hasIcon;
  private boolean hasHelpCommand;
  private List<WidgetType> widgets;

  public CommandParser(String[] args) throws CommandParseException {
    widgets = new ArrayList<>();

    for (int i = 0, li = args.length; i < li; i++) {
      String key = args[i];
      if (key.contains(CommandOption.HELP_KEY) || key.contains(CommandOption.HELP_LONG_KEY)) {
        hasHelpCommand = true;
      } else if (key.contains(CommandOption.ICON_KEY) || key.contains(CommandOption.ICON_LONG_KEY)) {
        hasIcon = true;
      } else if (key.contains(CommandOption.LAYOUT_KEY) || key.contains(CommandOption.LAYOUT_LONG_KEY)) {
        if (i + 1 < li && !isCommand(args[++i])) {
          List<String> splitedWidgets = Arrays.asList(args[i].split(","));

          for (int j = 0; j < splitedWidgets.size(); j++) {
            String widget = splitedWidgets.get(j).toLowerCase();
            switch (widget) {
              case "rv":
              case "recyclerview":
                widgets.add(WidgetType.RecyclerView);
                break;
              case "lv":
              case "listview":
                widgets.add(WidgetType.ListView);
                break;
              default:
                widgets.add(WidgetType.Default);
                break;
            }
          }
        }
      } else if (!isCommand(key) && i + 1 < li) {
        throw new CommandParseException("Unsupported option : " + key);
      } else {
        path = key;
      }
    }
  }

  public String getPath() {
    return path;
  }

  public TabType getTabType() {
    if (widgets.size() > 0) {
      if (hasIcon) {
        return TabType.SlidingIconTab;
      } else {
        return TabType.SlidingTab;
      }
    } else {
      return null;
    }
  }

  public boolean hasIcon() {
    return hasIcon;
  }

  public boolean hasHelpCommand() {
    return hasHelpCommand;
  }

  public List<WidgetType> getWidgets() {
    return widgets;
  }

  public boolean isCommand(String command) {
    String reg = "^(-|--)[a-z]+$";

    Pattern pat = Pattern.compile(reg);
    Matcher matcher = pat.matcher(command);

    return matcher.find();
  }
}
