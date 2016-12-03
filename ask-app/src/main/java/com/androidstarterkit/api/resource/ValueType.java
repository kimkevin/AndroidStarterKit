package com.androidstarterkit.api.resource;

public enum ValueType {
  DIMEN("dimen")
  , STYLE("style")
  , STRING("string");

  private String name;

  ValueType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name.toLowerCase();
  }

  public String fileName() {
    return name + "s";
  }
}
