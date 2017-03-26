package com.androidstarterkit.file;

import com.androidstarterkit.util.FileUtils;

import java.io.File;
import java.util.List;

public class BaseFile extends File {
  protected List<String> lineList;

  public BaseFile(String pathName, String fileName) {
    super(pathName + "/" + fileName);

    lineList = FileUtils.readFile(this);
  }

  public String getBaseName() {
    return FileUtils.removeExtension(getName());
  }

  public void print() {
    for (String line : lineList) {
      System.out.println(line);
    }
  }
}
