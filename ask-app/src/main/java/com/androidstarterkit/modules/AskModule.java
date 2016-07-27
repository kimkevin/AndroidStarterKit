package com.androidstarterkit.modules;

import com.androidstarterkit.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AskModule extends BaseModule {
  public static final String MODULE_NAME = "ask-module";

  private Map<String, String> fileMap;

  public AskModule() {
    super(FileUtils.linkPathWithSlash(FileUtils.getRootPath(), MODULE_NAME), "com.androidstarterkit.module");

    fileMap = new HashMap<>();

    listFilesForFolder(this);
  }

  private void listFilesForFolder(File directory) {
    for (File file : directory.listFiles(new MyFileFilter())) {
      if (file.isDirectory()) {
        listFilesForFolder(file);
      } else {
        fileMap.put(file.getName(), file.getPath().replace("/" + file.getName(), ""));
      }
    }
  }

  public String getPath(String key) {
    return fileMap.get(key);
  }

  public File getFile(String key) {
    return new File(getPath(key) + "/" + key);
  }
}
