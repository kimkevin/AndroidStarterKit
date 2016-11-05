package com.androidstarterkit;

public class CommandException extends Exception {
  public static final String ERROR_MESSAGE_PREFIX = "ask: ";

  public static final int INVALID_NO_OPTIONS = 1;
  public static final int FILE_NOT_FOUND = 2;
  public static final int MODULE_NOT_FOUND = 3;
  public static final int WIDGET_NOT_FOUND = 4;
  public static final int INVAILD_NO_WIDGET = 5;

  private int errCode;

  public CommandException(String message) {
    this(0, message);
  }

  public CommandException(int errCode) {
    this(errCode, null);
  }

  public CommandException(int errCode, String message) {
    super(message);

    this.errCode = errCode;
  }

  @Override
  public String getMessage() {
    if (errCode == INVALID_NO_OPTIONS) {
      return ERROR_MESSAGE_PREFIX + "no options: please use -h or --help option";
    } else if (errCode == FILE_NOT_FOUND) {
      return ERROR_MESSAGE_PREFIX + "failed to find project: " + super.getMessage();
    } else if (errCode == MODULE_NOT_FOUND) {
      return ERROR_MESSAGE_PREFIX + "failed to find module: " + super.getMessage();
    } else if (errCode == WIDGET_NOT_FOUND) {
      return ERROR_MESSAGE_PREFIX + "failed to find widget: " + super.getMessage();
    } else if (errCode == INVAILD_NO_WIDGET) {
      return ERROR_MESSAGE_PREFIX + "no widgets: please use -h or --help option";
    }

    return super.getMessage();
  }

  public boolean shudShowHelp() {
    return errCode == INVALID_NO_OPTIONS
        || errCode == INVAILD_NO_WIDGET;
  }
}
