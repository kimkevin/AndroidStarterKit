package com.androidstarterkit.command;

public enum TabType {
  SlidingTab("SlidingTab"),
  SlidingIconTab("SlidingIconTab");

  private String fileName;

  TabType(String fileName) {
    this.fileName = fileName;
  }

  public String getFragmentName() {
    return fileName + "Fragment";
  }
}
