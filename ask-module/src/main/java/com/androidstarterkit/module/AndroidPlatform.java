package com.androidstarterkit.module;

public class AndroidPlatform {
  private static final String FILE_PATH = "https://raw.githubusercontent.com/kimkevin/AndroidStarterKit/master/assets/";
  private static final String EXTENSION_NAME = ".png";

  private String name;
  private String verCode;
  private int apiLevel;
  private String logoUrl;

  public AndroidPlatform(String name, String verCode, int apiLevel) {
    this.name = name;
    this.verCode = verCode;
    this.apiLevel = apiLevel;
    this.logoUrl = FILE_PATH + name + EXTENSION_NAME;
  }

  public String getName() {
    return name.toUpperCase() + " " + apiLevel;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVerCode() {
    return "Android " + verCode;
  }

  public void setVerCode(String version) {
    this.verCode = version;
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
