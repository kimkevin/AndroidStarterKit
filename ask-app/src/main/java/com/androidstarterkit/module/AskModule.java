package com.androidstarterkit.module;

import com.androidstarterkit.CommandException;
import com.androidstarterkit.util.FileUtil;

public class AskModule extends Directory {
  public static final String MODULE_NAME = "ask-module";

  public AskModule() throws CommandException {
    super(FileUtil.linkPathWithSlash(FileUtil.getRootPath(), MODULE_NAME),
        new String[] { "java", "xml", "gradle" },
        new String[] { "build", "libs" });
  }

  public String getRelativePathFromJavaDir(String key) {
    String applicationIdPath = FileUtil.changeDotToSlash(applicationId);
    int index;
    try {
      index = getChildPath(key).indexOf(applicationIdPath);
    } catch (NullPointerException exception) {
      return null;
    }
    return FileUtil.splitFirstSlash(getChildPath(key).substring(index).replace(applicationIdPath, ""));
  }
}
