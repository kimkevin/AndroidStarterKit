package com.androidstarterkit.tool;


import com.androidstarterkit.android.api.Extension;
import com.androidstarterkit.util.FileUtils;

public class ClassInfo {
  private String name;
  private String packageFullPathname;

  public ClassInfo(String packageFullPathname) {
    this.packageFullPathname = packageFullPathname;
    this.name = FileUtils.getFilenameFromSlashPath(packageFullPathname);
  }

  public String getName() {
    return name;
  }

  public String getNameEx() {
    return name + Extension.JAVA;
  }

  public String getPackageFullPathname() {
    return packageFullPathname;
  }

  @Override
  public String toString() {
    return "ClassInfo{" +
        "name='" + name + '\'' +
        ", packageFullPathname='" + packageFullPathname + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ClassInfo) {
      ClassInfo classInfo = (ClassInfo) o;
      if (packageFullPathname.equals(classInfo.getPackageFullPathname())) {
        return true;
      }
    }
    return false;
  }
}
