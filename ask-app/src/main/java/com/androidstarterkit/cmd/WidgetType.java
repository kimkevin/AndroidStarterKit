package com.androidstarterkit.cmd;

public enum WidgetType {
  RecyclerView("RecyclerView"),
  ListView("ListView"),
  Default("Default");

  private String widgetName;

  WidgetType(String widgetName) {
    this.widgetName = widgetName;
  }

  public String getFragmentName() {
    return widgetName + "Fragment";
  }
}
