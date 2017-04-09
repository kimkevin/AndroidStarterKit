package com.androidstarterkit.android.api;

public enum Extension {
  JAVA(".java"), XML(".xml"), GRADLE(".gradle"), PRO(".pro");

  private String extensionName;

  Extension(String extensionName) {
    this.extensionName = extensionName;
  }

  @Override
  public String toString() {
    return extensionName.toLowerCase();
  }
}
