package com.androidstarterkit.android.api.manifest;

public enum Permission {
  INTERNET;

  @Override
  public String toString() {
    return "android.permission." + name();
  }
}
