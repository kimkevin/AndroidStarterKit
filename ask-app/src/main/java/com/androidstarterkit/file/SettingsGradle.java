package com.androidstarterkit.file;


import com.androidstarterkit.exception.ModuleFileNotFoundException;
import com.androidstarterkit.injection.file.android.InjectionGradleFile;
import com.androidstarterkit.tool.MatcherTask;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class SettingsGradle extends InjectionGradleFile {
  private static final String TAG = SettingsGradle.class.getSimpleName();

  public static final String FILE_NAME = "settings.gradle";

  private String appModuleName;

  public SettingsGradle(String pathname) {
    super(pathname + "/" + FILE_NAME);

    Scanner scanner;
    try {
      scanner = new Scanner(this);
    } catch (FileNotFoundException e) {
      throw new ModuleFileNotFoundException(FILE_NAME);
    }

    while (scanner.hasNextLine()) {
      String codeLine = scanner.nextLine();

      MatcherTask task = new MatcherTask(":([\\w_-]+)", codeLine);
      task.match(1, matched -> appModuleName = matched);
    }
  }

  @Override
  public void apply() {
    super.apply();
  }

  public String getAppModuleName() {
    return appModuleName;
  }
}
