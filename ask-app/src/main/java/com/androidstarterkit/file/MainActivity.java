package com.androidstarterkit.file;


import com.androidstarterkit.injection.file.android.InjectionJavaFile;

import java.io.File;

public class MainActivity extends InjectionJavaFile {
  public MainActivity(File file) {
    super(file);
  }
}
