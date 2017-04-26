package com.androidstarterkit.file;


import com.androidstarterkit.injection.file.android.InjectionJavaFile;

public class MainActivity extends InjectionJavaFile {

  public MainActivity(String fullPathname) {
    super(fullPathname);
  }

  public MainActivity(String pathname, String filename) {
    super(pathname + "/" + filename);
  }
}
