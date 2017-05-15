package com.androidstarterkit.directory;

import com.androidstarterkit.android.api.Extension;
import com.androidstarterkit.exception.CommandException;
import com.androidstarterkit.file.AndroidManifest;
import com.androidstarterkit.file.Application;
import com.androidstarterkit.file.BuildGradle;
import com.androidstarterkit.tool.ExternalLibrary;
import com.androidstarterkit.util.FileUtils;

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

  // Files in moculde
  protected Map<String, Object> fileMap;
  protected String applicationId;

  protected String[] fileExtensions;
  protected String[] ignoreDirNames;

  protected BuildGradle appBuildGradleFile;
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

    String buildGradlePath = getChildDirPath(BUILD_GRADLE_FILE);
    appBuildGradleFile = new BuildGradle(buildGradlePath);
    this.applicationId = appBuildGradleFile.getApplicationId();

    androidManifestFile = new AndroidManifest(getChildDirPath(ANDROID_MANIFEST_FILE));

    externalLibrary = new ExternalLibrary(appBuildGradleFile.getSupportLibraryVersion());
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

  public Application getApplication() {
    try {
      String relativePathname = androidManifestFile.getApplicationRelativePathname();
      String filenameEx = FileUtils.getFilenameFromDotPath(relativePathname) + Extension.JAVA;
      return new Application(getChildDirPath(filenameEx) + "/" + filenameEx);
    } catch (NullPointerException exception) {
      return null;
    }
  }

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public String getChildDirPath(String key) {
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
    String path = getChildDirPath(fileName);
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

  public BuildGradle getAppBuildGradleFile() {
    return appBuildGradleFile;
  }

  public String getMainActivityName() {
    return androidManifestFile.getMainActivityName();
  }

  public String getMainActivityExtName() {
    return androidManifestFile.getMainActivityName() + Extension.JAVA;
  }

  public void printFileMap() {
    System.out.println("[DEBUG START]");
    for (String key : fileMap.keySet()) {
      System.out.println(key + " , " + fileMap.get(key));
    }
    System.out.println("[END]");
  }
}
