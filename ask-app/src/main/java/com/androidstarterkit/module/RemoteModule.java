package com.androidstarterkit.module;

import com.androidstarterkit.exception.CommandException;
import com.androidstarterkit.util.FileUtils;

public class RemoteModule extends Directory {
  public static final String MODULE_NAME = "ask-remote-module";

  public RemoteModule() throws CommandException {
    super(FileUtils.linkPathWithSlash(FileUtils.getRootPath(), MODULE_NAME),
        new String[] { "java", "xml", "gradle" },
        new String[] { "build", "libs" });
  }

  public String getRelativePathFromJavaDir(String key) {
    String applicationIdPath = FileUtils.changeDotToSlash(applicationId);
    int index;
    try {
      index = getChildPath(key).indexOf(applicationIdPath);
    } catch (NullPointerException exception) {
      return null;
    }
    return FileUtils.splitFirstSlash(getChildPath(key).substring(index).replace(applicationIdPath, ""));
  }
}
