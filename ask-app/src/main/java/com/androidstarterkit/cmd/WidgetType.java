package com.androidstarterkit.cmd;

public enum WidgetType {
  RecyclerView("RecyclerView"),
  ListView("ListView"),
  ScrollView("ScrollView"),
  GridView("GridView");

  private String widgetName;

  WidgetType(String widgetName) {
    this.widgetName = widgetName;
  }

  public String getFragmentName() {
    return widgetName + "Fragment";
  }
}
