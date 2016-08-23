package com.androidstarterkit;

public enum Extension {
  JAVA(".java"), XML(".xml"), GRADLE(".gradle");

  private String extensionName;

  Extension(String extensionName) {
    this.extensionName = extensionName;
  }

  public String getName() {
    return extensionName;
  }
}
