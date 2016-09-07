package com.androidstarterkit.modules;

import com.androidstarterkit.BuildGradleFile;
import com.androidstarterkit.Extension;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Directory extends File {
  public static final String ANDROID_MANIFEST_FILE = "AndroidManifest.xml";
  public static final String SETTINGS_GRADLE_FILE = "settings.gradle";

  protected Map<String, String> fileMap;
  protected String applicationId;

  protected String[] fileExtensions;
  protected String[] ignoreDirNames;
  protected BuildGradleFile buildGradleFile;

  public Directory(String pathname) {
    this(pathname, null);
  }

  public Directory(String pathname, String applicationId) {
    super(pathname);

    fileMap = new HashMap<>();

    this.applicationId = applicationId;

    listFilesForFolder(this);
  }

  public Directory(String pathname, String[] fileExtensions, String[] ignoreDirNames) {
    super(pathname);

    this.fileExtensions = fileExtensions;
    this.ignoreDirNames = ignoreDirNames;

    listFilesForFolder(this);

    String buildGradlePath = fileMap.get("build.gradle");
    buildGradleFile = new BuildGradleFile(buildGradlePath);
    this.applicationId = buildGradleFile.getApplicationId();
  }

  private void listFilesForFolder(File directory) {
    if (fileMap == null) {
      fileMap = new HashMap<>();
    }

    for (File file : directory.listFiles(fileFilter)) {
      if (file.isDirectory()) {
        listFilesForFolder(file);
      } else {
        fileMap.put(file.getName(), file.getPath().replace("/" + file.getName(), ""));
      }
    }
  }

  private FileFilter fileFilter = new FileFilter() {
    @Override
    public boolean accept(File file) {
      for (String extension : fileExtensions) {
        if (file.getName().toLowerCase().endsWith(extension)) {
          return true;
        } else if (file.isDirectory()
            && !Arrays.asList(ignoreDirNames).contains(file.getName().toLowerCase())) {
          return true;
        }
      }
      return false;
    }
  };

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public String getChildPath(String key) {
    return fileMap.get(key);
  }

  public File getChildFile(String fileName, Extension extension) {
    return getChildFile(fileName + extension.getName());
  }

  public File getChildFile(String fileName) {
    String path = getChildPath(fileName);
    if (path == null) {
      return null;
    }
    return new File(path + "/" + fileName);
  }

  public void printFileMap() {
    System.out.println("[DEBUG START]");
    for (String key : fileMap.keySet()) {
      System.out.println(key + " , " + fileMap.get(key));
    }
    System.out.println("[END]");
  }
}
