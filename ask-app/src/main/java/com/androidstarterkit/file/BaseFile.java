package com.androidstarterkit.file;

import java.io.File;

public class BaseFile extends File {
  public BaseFile(String pathName, String fileName) {
    super(pathName + "/" + fileName);
  }
}
