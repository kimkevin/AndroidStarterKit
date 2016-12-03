package com.androidstarterkit.file;


import com.androidstarterkit.exception.ModuleFileNotFoundException;
import com.androidstarterkit.tool.MatcherTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SettingsGradle extends File {
  private static final String TAG = SettingsGradle.class.getSimpleName();

  public static final String FILE_NAME = "settings.gradle";

  private String appModuleName;

  public SettingsGradle(File file) {
    super(file.getPath());

    Scanner scanner;
    try {
      scanner = new Scanner(this);
    } catch (FileNotFoundException e) {
      throw new ModuleFileNotFoundException(FILE_NAME);
    }

    while (scanner.hasNextLine()) {
      String codeLine = scanner.nextLine();

      MatcherTask task = new MatcherTask(":([\\w_-]+)", codeLine);
      task.start(matcher -> {
        appModuleName = matcher.group(1);
      });
    }
  }

  public String getAppModuleName() {
    return appModuleName;
  }
}
