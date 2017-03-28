package com.androidstarterkit.file.directory;

import com.androidstarterkit.exception.CommandException;
import com.androidstarterkit.util.FileUtils;

import java.io.File;

public class RemoteDirectory extends Directory {
  public static final String MODULE_NAME = "ask-remote-module";

  public RemoteDirectory() throws CommandException {
    super(FileUtils.linkPathWithSlash(FileUtils.getRootPath(), MODULE_NAME),
        new String[] { "java", "xml", "gradle", "json" },
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

    try {
      return FileUtils.removeFirstSlash(getChildPath(key).substring(index).replace(applicationIdPath, ""));
    } catch (StringIndexOutOfBoundsException exception) {
      return getChildPath(key);
    }
  }

  public File getMainActivity() {
    return getChildFile(getMainActivityExtName());
  }
}
