package com.androidstarterkit.module;

import com.androidstarterkit.android.api.Extension;
import com.androidstarterkit.exception.CommandException;
import com.androidstarterkit.file.AndroidManifest;
import com.androidstarterkit.file.BuildGradle;
import com.androidstarterkit.model.ExternalLibrary;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Directory extends File {
  public static final String ANDROID_MANIFEST_FILE = "AndroidManifest.xml";
  public static final String BUILD_GRADLE_FILE = "build.gradle";

  protected Map<String, Object> fileMap;
  protected String applicationId;

  protected String[] fileExtensions;
  protected String[] ignoreDirNames;

  protected BuildGradle buildGradleFile;
  protected AndroidManifest androidManifestFile;
  protected ExternalLibrary externalLibrary;

  public Directory(String pathname, String[] fileExtensions, String[] ignoredDirNames) {
    super(pathname);

    this.fileExtensions = fileExtensions;
    this.ignoreDirNames = ignoredDirNames;

    try {
      listFilesForFolder(this);
    } catch (NullPointerException e) {
      throw new CommandException(CommandException.FILE_NOT_FOUND, getName());
    }

    String buildGradlePath = getChildPath(BUILD_GRADLE_FILE);
    buildGradleFile = new BuildGradle(buildGradlePath);
    this.applicationId = buildGradleFile.getApplicationId();

    androidManifestFile = new AndroidManifest(getChildPath(ANDROID_MANIFEST_FILE));

    externalLibrary = new ExternalLibrary(buildGradleFile.getSupportLibraryVersion());
  }

  private void listFilesForFolder(File directory) {
    for (File file : directory.listFiles(fileFilter)) {
      if (file.isDirectory()) {
        listFilesForFolder(file);
      } else {
        if (fileMap == null) {
          fileMap = new HashMap<>();
        }

        Object valueObject = fileMap.get(file.getName());
        String pathname = file.getPath().replace("/" + file.getName(), "");

        if (valueObject == null) {
          fileMap.put(file.getName(), pathname);
        } else {
          String savedValue = (String) valueObject;
          List<String> values = new ArrayList<>();
          values.add(savedValue);
          values.add(pathname);

          fileMap.put(file.getName(), values);
        }
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
    Object value = fileMap.get(key);
    if (value instanceof String) {
      return (String) value;
    }

    return null;
  }

  public List<File> getChildFiles(String fileName, Extension extension) {
    Object value = fileMap.get(fileName + extension.toString());

    List<String> filePaths;
    if (value instanceof ArrayList<?>) {
      filePaths = (ArrayList<String>) value;
    } else if (value instanceof String) {
      filePaths = Arrays.asList((String) value);
    } else {
      return null;
    }

    List<File> files = new ArrayList<>();

    for (String filePath : filePaths) {
      files.add(new File(filePath, fileName + extension.toString()));
    }

    return files;
  }

  public File getChildFile(String fileName, Extension extension) {
    return getChildFile(fileName + extension.toString());
  }

  public File getChildFile(String fileName) {
    String path = getChildPath(fileName);
    if (path == null) {
      return null;
    }

    return new File(path, fileName);
  }

  public ExternalLibrary getExternalLibrary() {
    return externalLibrary;
  }

  public AndroidManifest getAndroidManifestFile() {
    return androidManifestFile;
  }

  public BuildGradle getBuildGradleFile() {
    return buildGradleFile;
  }

  public void printFileMap() {
    System.out.println("[DEBUG START]");
    for (String key : fileMap.keySet()) {
      System.out.println(key + " , " + fileMap.get(key));
    }
    System.out.println("[END]");
  }

  public String getMainActivityName() {
    return androidManifestFile.getMainActivityName();
  }
}
