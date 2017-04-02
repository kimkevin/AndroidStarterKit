package com.androidstarterkit.model;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Config {
  @SerializedName("path")
  private String path;

  @SerializedName("file")
  private String fileNameEx;

  @SerializedName("codeblocks")
  private List<CodeBlock> codeBlocks;

  public String getPath() {
    return path;
  }

  public String getFullPathname() {
    return path + "/" + fileNameEx;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getFileNameEx() {
    return fileNameEx;
  }

  public void setFileNameEx(String fileNameEx) {
    this.fileNameEx = fileNameEx;
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
        ", fileNameEx='" + fileNameEx + '\'' +
        ", codeBlocks=" + (codeBlocks != null ? codeBlocks.toString() : "") +
        '}';
  }
}
