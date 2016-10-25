package com.androidstarterkit.cmd;

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
