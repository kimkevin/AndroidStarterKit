package com.androidstarterkit.modules;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

public class BaseModule extends File {
  private String applicationId;

  public BaseModule(String pathname) {
    super(pathname);
  }

  public BaseModule(String pathname, String applicationId) {
    super(pathname);

    this.applicationId = applicationId;
  }

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public class MyFileFilter implements FileFilter {
    private final String[] FILE_EXTENSIONS = new String[] { "java", "xml", "gradle" };
    private final String[] IGNORE_DIR_NAMES = new String[] { "build", "libs" };

    public boolean accept(File file) {
      for (String extension : FILE_EXTENSIONS) {
        if (file.getName().toLowerCase().endsWith(extension)) {
          return true;
        } else if (file.isDirectory()
            && !Arrays.asList(IGNORE_DIR_NAMES).contains(file.getName().toLowerCase())) {
          return true;
        }
      }
      return false;
    }
  }
}
