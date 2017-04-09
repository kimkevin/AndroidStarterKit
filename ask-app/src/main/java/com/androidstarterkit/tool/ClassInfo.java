package com.androidstarterkit.tool;


import com.androidstarterkit.android.api.Extension;

import java.util.ArrayList;
import java.util.List;

public class ClassInfo {
  private String name;
  private ClassInfo nestedClassInfo;
  private List<ClassInfo> genericTypeClassInfos;

  public ClassInfo(String name) {
    this(name, null, null);
  }

  public ClassInfo(String name, List<ClassInfo> genericTypeClassNames) {
    this(name, genericTypeClassNames, null);
  }

  public ClassInfo(String name, ClassInfo nestedClassInfo) {
    this(name, null, nestedClassInfo);
  }

  public ClassInfo(String name, List<ClassInfo> genericTypeClassNames, ClassInfo nestedClassInfo) {
    this.name = name;
    this.nestedClassInfo = nestedClassInfo;

    if (genericTypeClassNames != null) {
      this.genericTypeClassInfos = genericTypeClassNames;
    } else {
      this.genericTypeClassInfos = new ArrayList<>();
    }
  }

  public String getName() {
    return name;
  }

  public String getNameEx() {
    return name + Extension.JAVA;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ClassInfo getNestedClassInfo() {
    return nestedClassInfo;
  }

  public void setNestedClassInfo(ClassInfo nestedClassInfo) {
    this.nestedClassInfo = nestedClassInfo;
  }

  public List<ClassInfo> getGenericTypeClassInfos() {
    return genericTypeClassInfos;
  }

  public void addGenericTypeClassInfo(ClassInfo genericTypeClassInfo) {
    genericTypeClassInfos.add(genericTypeClassInfo);
  }

  public boolean equals(ClassInfo obj) {
    if (nestedClassInfo != null && obj.getNestedClassInfo() != null) {
      if (!nestedClassInfo.equals(obj.getNestedClassInfo())) {
        return false;
      }
    } else if (nestedClassInfo == null && obj.getNestedClassInfo() != null) {
      return false;
    } else if (nestedClassInfo != null && obj.getNestedClassInfo() == null) {
      return false;
    }

    if (genericTypeClassInfos.size() > 0 && obj.getGenericTypeClassInfos().size() > 0) {
      if (!compare(genericTypeClassInfos, obj.getGenericTypeClassInfos())) {
        return false;
      }
    } else if (genericTypeClassInfos.size() != obj.getGenericTypeClassInfos().size()) {
      return false;
    }

    return name.equals(obj.getName());
  }

  private boolean compare(List<ClassInfo> genericTypeClassInfos, List<ClassInfo> targetInfos) {
    for (int i = 0, li = genericTypeClassInfos.size(); i < li; i++) {
      ClassInfo originalInfo = genericTypeClassInfos.get(i);
      ClassInfo targetInfo = targetInfos.get(i);

      if (!originalInfo.equals(targetInfo)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    return toString(0);
  }

  private String toString(int depth) {
    String msg = getIntent(depth) + "class : " + name + "\n ";

    for (ClassInfo classInfo : genericTypeClassInfos) {
      msg += getIntent(depth) + "Generic type class : \n";
      msg += getIntent(depth) + classInfo.toString(depth + 1);
    }

    if (nestedClassInfo != null) {
      msg += getIntent(depth) + "Nested class : \n";
      msg += getIntent(depth) + nestedClassInfo.toString(depth + 1);
    }

    return msg;
  }

  private String getIntent(int depth) {
    String intent = "";
    for (int i = 0; i < depth; i++) {
      intent += "  ";
    }
    return intent;
  }
}
