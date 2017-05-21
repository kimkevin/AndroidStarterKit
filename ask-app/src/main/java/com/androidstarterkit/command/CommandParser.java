package com.androidstarterkit.command;

import com.androidstarterkit.exception.CommandException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {
  private String path;
  private boolean hasIcon;
  private boolean hasHelpCommand;
  private List<String> layouts;
  private List<String> modules;

  public CommandParser(String[] args) throws CommandException {
    layouts = new ArrayList<>();
    modules = new ArrayList<>();

    if (args.length <= 0) {
      throw new CommandException(CommandException.INVALID_NO_OPTIONS);
    }

    for (int i = 0, li = args.length; i < li; i++) {
      final String key = args[i];

      if (key.contains(CommandOption.HELP_KEY) || key.contains(CommandOption.HELP_LONG_KEY)) {
        hasHelpCommand = true;
      } else if (key.contains(CommandOption.ICON_KEY) || key.contains(CommandOption.ICON_LONG_KEY)) {
        hasIcon = true;
      } else if (key.contains(CommandOption.LAYOUT_KEY) || key.contains(CommandOption.LAYOUT_LONG_KEY)) {
        if (i + 1 < li && !isCommand(args[++i])) {
          layouts = Arrays.asList(args[i].split(","));
        } else {
          throw new CommandException(CommandException.INVALID_WIDGET);
        }
      } else if (key.contains(CommandOption.TOOL_KEY) || key.contains(CommandOption.TOOL_LONG_KEY)) {
        if (i + 1 < li && !isCommand(args[++i])) {
          modules = Arrays.asList(args[i].split(","));
        } else {
          throw new CommandException(CommandException.INVALID_WIDGET);
        }
      } else if (isCommand(key)) {
        throw new CommandException(CommandException.OPTION_NOT_FOUND, key);
      } else {
        path = key;
      }
    }
  }

  public String getPath() {
    return path;
  }

  public TabType getTabType() {
    if (layouts.size() > 1) {
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

  public List<String> getLayoutCommands() {
    return layouts;
  }

  public List<String> getModuleCommands() {
    return modules;
  }

  private boolean isCommand(String command) {
    String reg = "^(-|--)[a-z]+$";

    Pattern pat = Pattern.compile(reg);
    Matcher matcher = pat.matcher(command);

    return matcher.find();
  }
}
