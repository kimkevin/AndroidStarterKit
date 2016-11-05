package com.androidstarterkit;

public class CommandException extends Exception {
  public static final String ERROR_MESSAGE_PREFIX = "ask: ";

  public static final int INVALID_NO_OPTIONS = 1;
  public static final int FILE_NOT_FOUND = 2;
  public static final int APP_MODULE_NOT_FOUND = 3;
  public static final int WIDGET_NOT_FOUND = 4;
  public static final int INVAILD_NO_WIDGET = 5;
  public static final int OPTION_NOT_FOUND = 6;

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
      return ERROR_MESSAGE_PREFIX + "there is no options";
    } else if (errCode == FILE_NOT_FOUND) {
      return ERROR_MESSAGE_PREFIX + "failed to find project: " + super.getMessage();
    } else if (errCode == APP_MODULE_NOT_FOUND) {
      return ERROR_MESSAGE_PREFIX + "failed to find module: " + super.getMessage();
    } else if (errCode == WIDGET_NOT_FOUND) {
      return ERROR_MESSAGE_PREFIX + "failed to find widget: " + super.getMessage();
    } else if (errCode == INVAILD_NO_WIDGET) {
      return ERROR_MESSAGE_PREFIX + "there is no widgets";
    } else if (errCode == OPTION_NOT_FOUND) {
      return ERROR_MESSAGE_PREFIX + "failed to find option: " + super.getMessage();
    }

    return super.getMessage();
  }

  public boolean shudShowHelp() {
    return errCode == INVALID_NO_OPTIONS
        || errCode == INVAILD_NO_WIDGET;
  }
}
