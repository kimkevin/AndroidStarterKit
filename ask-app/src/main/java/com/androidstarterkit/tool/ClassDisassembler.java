package com.androidstarterkit.tool;


import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ClassDisassembler {
  private String packagePathname;
  private String debugClassesPathname;

  private List<String> classfilterPatterns = Arrays.asList("R", "^R\\$[\\w]+");
  private List<String> externalPackageFilters = Arrays.asList("org/json", "java", "android");

  private List<String> extractedPackagePathnames;

  public ClassDisassembler(String debugClassesPathname, String packagePathname) {
    this.debugClassesPathname = debugClassesPathname;
    this.packagePathname = packagePathname;
  }
  
  public void extractClasses(String classPackagePathname) {
    String shellCommand = "javap -v " + debugClassesPathname + "/" + classPackagePathname + "*.class " +
        "| grep '= Class' " +
        "| grep -v '" + packageFilterToString() + "\\(/[A-Za-z0-9]\\+\\)\\+' " +
        "| awk '{ print $6 }' " +
        "| sort -u";

    extractedPackagePathnames = Arrays.stream(ExecuteShellComand.executeCommand(shellCommand, false).split("\n"))
        .filter(classPathname -> !isExcluded(classPathname) && !classPathname.contains(classPackagePathname))
        .collect(Collectors.toList());
  }

  public List<ClassInfo> getInternalClassInfos() {
    return extractedPackagePathnames.stream()
        .filter(packageFullPathname -> packageFullPathname.contains(packagePathname))
        .map(packageFullPathname -> packageFullPathname.replace(packagePathname + "/", ""))
        .map(ClassInfo::new)
        .collect(Collectors.toList());
  }

  public List<ClassInfo> getExternalClassInfos() {
    return extractedPackagePathnames.stream()
        .filter(packageFullPathname -> !packageFullPathname.contains(packagePathname))
        .map(packageFullPathname -> packageFullPathname.replace(packagePathname + "/", ""))
        .map(ClassInfo::new)
        .collect(Collectors.toList());
  }

  private String packageFilterToString() {
    String filterString = "";
    for (int i = 0, li = externalPackageFilters.size(); i < li; i++) {
      filterString += externalPackageFilters.get(i);

      if (i < li - 1) {
        filterString += "\\|";
      }
    }

    return filterString;
  }

  private boolean isExcluded(String classname) {
    for (String patternString : classfilterPatterns) {
      Pattern pattern = Pattern.compile(packagePathname + "/" + patternString);
      Matcher matcher = pattern.matcher(classname);

      if (matcher.find()) {
        return true;
      }
    }
    return false;
  }
}
