package com.androidstarterkit.file;


import com.androidstarterkit.injection.file.android.InjectionProgardFile;

public class ProguardRules extends InjectionProgardFile {

  public static final String FILE_NAME = "proguard-rules.pro";

  public ProguardRules(String pathName) {
    super(pathName + "/" + FILE_NAME);
  }
}
