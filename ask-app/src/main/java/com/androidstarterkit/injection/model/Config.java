package com.androidstarterkit.injection.model;


import com.androidstarterkit.util.FileUtils;
import com.google.gson.annotations.SerializedName;

public class Config {
  @SerializedName("path")
  protected String path;

  @SerializedName("file")
  protected String fileNameEx;

  public Config(String path, String fileNameEx) {
    this.path = path;
    this.fileNameEx = fileNameEx;
  }

  public String getPath() {
    return path;
  }

  public String getFullPathname() {
    return path + "/" + fileNameEx;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getFileName() {
    return FileUtils.removeExtension(fileNameEx);
  }

  public String getFileNameEx() {
    return fileNameEx;
  }

  public void setFileNameEx(String fileNameEx) {
    this.fileNameEx = fileNameEx;
  }

  @Override
  public String toString() {
    return "Config{" +
        "path='" + path + '\'' +
        ", fileNameEx='" + fileNameEx + '\'' +
        '}';
  }
}
