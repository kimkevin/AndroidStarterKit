package com.androidstarterkit.cmd;

import com.androidstarterkit.UnsupportedWidgetTypeException;

public enum TabType {
  SlidingTabLayout("SlidingTabLayout"),
  SlidingIconTabLayout("SlidingIconTabLayout");

  private String fileName;

  TabType(String fileName) {
    this.fileName = fileName;
  }

  public String getName() throws UnsupportedWidgetTypeException {
    if (fileName == null) {
      throw new UnsupportedWidgetTypeException("Could not find widget name");
    }

    return fileName;
  }

  public String getActivityName() throws UnsupportedWidgetTypeException {
    if (fileName == null) {
      throw new UnsupportedWidgetTypeException("Could not find widget activity name");
    }

    return fileName + "Activity";
  }
}
