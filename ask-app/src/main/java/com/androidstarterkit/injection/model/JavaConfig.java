package com.androidstarterkit.injection.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JavaConfig extends Config{
  @SerializedName("import")
  private List<String> imports;

  @SerializedName("field")
  private List<String> fields;

  @SerializedName("method")
  private List<Method> methods;

  public JavaConfig(String path, String fileNameEx) {
    super(path, fileNameEx);
  }

  public List<String> getImports() {
    return imports;
  }

  public List<String> getFields() {
    return fields;
  }

  public List<Method> getMethods() {
    return methods;
  }

  public void replaceImportStrings(String reg, String applicationId) {
    for (int i = 0, li = imports.size(); i < li; i++) {
      imports.set(i, imports.get(i).replace(reg, applicationId));
    }
  }

  @Override
  public String toString() {
    return super.toString() + ", JavaConfig{" +
        "imports=" + imports +
        ", fields=" + fields +
        ", methods=" + methods +
        '}';
  }
}
