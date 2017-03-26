package com.androidstarterkit.model;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Config {
  public static final String ROOT_PATH = "root";
  public static final String APP_PATH = "app";

  @SerializedName("path")
  private String path;

  @SerializedName("file")
  private String fileFullName;

  @SerializedName("codeblocks")
  private List<CodeBlock> codeBlocks;

  public String getPath() {
    return path;
  }

  public String getFileFullName() {
    return fileFullName;
  }

  public List<CodeBlock> getCodeBlocks() {
    return codeBlocks;
  }

  public void addCodeBlock(CodeBlock codeBlock) {
    if (codeBlocks == null) {
      codeBlocks = new ArrayList<>();
    }

    codeBlocks.add(codeBlock);
  }

  @Override
  public String toString() {
    return "Config{" +
        "path='" + path + '\'' +
        ", fileFullName='" + fileFullName + '\'' +
        ", codeBlocks=" + codeBlocks.toString() +
        '}';
  }
}
