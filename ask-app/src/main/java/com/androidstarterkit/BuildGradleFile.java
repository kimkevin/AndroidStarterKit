package com.androidstarterkit;

import com.androidstarterkit.config.SyntaxConfig;
import com.androidstarterkit.utils.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class BuildGradleFile extends File {
  public static final String DEPENDENCIES_ELEMENT_NAME = "dependencies";
  public static final String COMPILE_CONFIGURATION_FORMAT = "compile '" + SyntaxConfig.REPLACE_STRING + "'";
  public static final String FILE_NAME = "build.gradle";
  public static final String SURPPORT_LIBRARY_VERSION = "23.4.0";

  private Map<String, String> dependencyDict;
  private List<String> lineList;

  public BuildGradleFile(String pathname) {
    super(pathname + "/" + FILE_NAME);

    lineList = FileUtil.readFile(this);

    dependencyDict = new HashMap<>();
    dependencyDict.put("android.support.v7.widget.CardView", "com.android.support:cardview-v7:" + SURPPORT_LIBRARY_VERSION);
    dependencyDict.put("android.support.v7.widget.RecyclerView", "com.android.support:recyclerview-v7:" + SURPPORT_LIBRARY_VERSION);
    dependencyDict.put("com.github.bumptech.glide", "com.github.bumptech.glide:glide:3.7.0");
  }

  public String getApplicationId() {
    String applicationId = null;
    try {
      Scanner scanner = new Scanner(this);

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();

        if (line.contains("applicationId")) {
          applicationId = FileUtil.getStringBetweenQuotes(line);
          break;
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    return applicationId;
  }

  public Set<String> getDependencyKeys() {
    return dependencyDict.keySet();
  }

  public void addDependency(String key) {
    addDependencies(dependencyDict.get(key));
  }

  /**
   * Add dependencies on compile configuration
   *
   * @param externalLibraries is strings
   */
  public void addDependencies(String... externalLibraries) {
    if (externalLibraries.length <= 0) {
      return;
    }

    for (String externalLibrary : externalLibraries) {
      lineList = FileUtil.addLineToObject(
          DEPENDENCIES_ELEMENT_NAME,
          COMPILE_CONFIGURATION_FORMAT.replace(SyntaxConfig.REPLACE_STRING, externalLibrary),
          lineList);
    }

    FileUtil.writeFile(this, FileUtil.getString(lineList));
  }
}
