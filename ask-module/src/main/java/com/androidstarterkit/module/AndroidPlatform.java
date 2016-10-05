package com.androidstarterkit.module;

public class AndroidPlatform {
  private static final String FILE_PATH = "https://raw.githubusercontent.com/kimkevin/AndroidStarterKit/master/assets/";
  private static final String EXTENSION_NAME = ".png";

  private String name;
  private String version;
  private int apiLevel;
  private String logoUrl;

  public AndroidPlatform(String name, String version, int apiLevel) {
    this.name = name;
    this.version = version;
    this.apiLevel = apiLevel;
    this.logoUrl = FILE_PATH + name + EXTENSION_NAME;
  }

  public String getName() {
    return name.toUpperCase();
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersion() {
    return "Android " + version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public int getApiLevel() {
    return apiLevel;
  }

  public void setApiLevel(int apiLevel) {
    this.apiLevel = apiLevel;
  }

  public String getLogoUrl() {
    return logoUrl;
  }

  public void setLogoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
  }
}
