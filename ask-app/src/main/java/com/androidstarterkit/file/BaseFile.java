package com.androidstarterkit.file;

import com.androidstarterkit.util.FileUtils;

import java.io.File;

public class BaseFile extends File {

  public BaseFile(String pathName, String fileName) {
    super(pathName + "/" + fileName);
  }

  public String getBaseName() {
    return FileUtils.removeExtension(getName());
  }
}
