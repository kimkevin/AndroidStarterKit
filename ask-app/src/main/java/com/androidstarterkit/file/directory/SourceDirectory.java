package com.androidstarterkit.file.directory;

import com.androidstarterkit.SyntaxConstraints;
import com.androidstarterkit.android.api.Extension;
import com.androidstarterkit.android.api.resource.ResourceType;
import com.androidstarterkit.command.TabType;
import com.androidstarterkit.config.RemoteModuleConfig;
import com.androidstarterkit.exception.CommandException;
import com.androidstarterkit.file.BuildGradle;
import com.androidstarterkit.file.MainActivity;
import com.androidstarterkit.file.ProguardRules;
import com.androidstarterkit.model.LayoutGroup;
import com.androidstarterkit.tool.ClassInfo;
import com.androidstarterkit.tool.ClassParser;
import com.androidstarterkit.tool.XmlEditor;
import com.androidstarterkit.util.FileUtils;
import com.androidstarterkit.util.PrintUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SourceDirectory extends Directory {
  private RemoteDirectory remoteDirectory;

  private BuildGradle projectBuildGradle;
  private ProguardRules proguardRules;
  private MainActivity remoteMainActivity;

  private String javaPath;
  private String resPath;
  private String layoutPath;

  private XmlEditor xmlEditor;

  private TabType tabType;
  private List<LayoutGroup> layoutGroups;

  private List<ClassInfo> transformedClassInfos;

  public SourceDirectory(String projectPathname, String sourceModuleName, RemoteDirectory remoteDirectory) {
    super(projectPathname + "/" + sourceModuleName
        , new String[]{"java", "gradle", "xml"}
        , new String[]{"build", "libs", "test", "androidTest", "res"});

    this.remoteDirectory = remoteDirectory;

    transformedClassInfos = new ArrayList<>();

    // Project level files
    remoteMainActivity = new MainActivity(remoteDirectory.getMainActivity());
    projectBuildGradle = new BuildGradle(projectPathname);
    proguardRules = new ProguardRules(getPath());
    xmlEditor = new XmlEditor(this, remoteDirectory);

    // Source directory
    javaPath = FileUtils.linkPathWithSlash(getPath(), "src/main/java", applicationId.replaceAll("\\.", "/"));
    resPath = FileUtils.linkPathWithSlash(getPath(), "src/main/res");
    layoutPath = FileUtils.linkPathWithSlash(resPath, ResourceType.LAYOUT.toString());
  }

  public BuildGradle getProjectBuildGradle() {
    return projectBuildGradle;
  }

  public ProguardRules getProguardRules() {
    return proguardRules;
  }

  public MainActivity getMainActivity() {
    return new MainActivity(getChildFile(getMainActivityExtName()));
  }

  public String getJavaPath() {
    return javaPath;
  }

  public String getResPath() {
    return resPath;
  }

  public String getLayoutPath() {
    return layoutPath;
  }

  public void transform(TabType tabType, List<LayoutGroup> layoutGroups) {
    this.tabType = tabType;
    this.layoutGroups = layoutGroups;

    xmlEditor.importAttrsOfRemoteMainActivity(() -> transformFileFromRemote(0, remoteMainActivity));
  }

  /**
   * If depth is 0, file is main Activity of askModule
   *
   * @param depth
   * @param remoteFile
   */
  public void transformFileFromRemote(int depth, File remoteFile) throws CommandException {
    String remoteFileNameEx = remoteFile.getName();
    String fileFullPathname;

    if (depth == 0) {
      fileFullPathname = FileUtils.linkPathWithSlash(javaPath
          , androidManifestFile.getMainActivityNameEx());
    } else {
      fileFullPathname = FileUtils.linkPathWithSlash(javaPath
          , remoteDirectory.getRelativePathFromJavaDir(remoteFileNameEx)
          , remoteFileNameEx);
    }

    System.out.println(PrintUtils.prefixDash(depth) + remoteFileNameEx);

    Scanner scanner;
    try {
      scanner = new Scanner(remoteFile);
    } catch (FileNotFoundException e) {
      throw new CommandException(CommandException.FILE_NOT_FOUND, remoteFile.getName());
    }
    List<String> codeLines = new ArrayList<>();
    List<ClassInfo> addedPackageClasses = new ArrayList<>();

    while (scanner.hasNext()) {
      String codeLine = scanner.nextLine();

      codeLine = changePackage(codeLine);

      if (depth == 0) {
        codeLine = changeMainActivityName(codeLine, remoteFileNameEx);
        codeLine = changeMainFragmentByOptions(codeLine, tabType);
      } else {
        codeLine = importFragments(codeLine);
      }

      codeLine = importDeclaredClasses(codeLine, depth, addedPackageClasses);

      xmlEditor.importResourcesForJava(codeLine, depth);

      codeLines.add(codeLine);
    }

    codeLines = defineImportClasses(codeLines, addedPackageClasses);

    FileUtils.writeFile(new File(fileFullPathname), codeLines);

    if (depth == 0) {
      System.out.println();
    }
  }

  private List<String> defineImportClasses(List<String> codeLines, List<ClassInfo> addedPackageClassInfos) {
    List<String> importedClassStrings = new ArrayList<>();
    for (int i = 0, li = addedPackageClassInfos.size(); i < li; i++) {
      String className = addedPackageClassInfos.get(i).getName();
      if (remoteDirectory.getRelativePathFromJavaDir(className + Extension.JAVA.toString()) != null) {
        String importedClassString = SyntaxConstraints.IDENTIFIER_IMPORT + " "
            + applicationId + "."
            + remoteDirectory.getRelativePathFromJavaDir(className + Extension.JAVA.toString()).replaceAll("/", ".") + "."
            + className + ";";

        if (!importedClassStrings.contains(importedClassString)) {
          importedClassStrings.add(importedClassString);
        }
      }
    }

    for (int i = 0, li = codeLines.size(); i < li; i++) {
      String line = codeLines.get(i);

      boolean isLastIndex = i + 1 < li;
      if (line.contains(SyntaxConstraints.IDENTIFIER_PACKAGE) && isLastIndex) {
        codeLines.addAll(i + 1, importedClassStrings);
        break;
      }
    }
    return codeLines;
  }

  private String changeMainActivityName(String codeLine, String moduleActivityName) {
    return codeLine.replace(FileUtils.removeExtension(moduleActivityName)
        , getMainActivityName());
  }

  private String changeMainFragmentByOptions(String codeLine, TabType tabType) throws CommandException {
    if (tabType != null) {
      return codeLine.replace(RemoteModuleConfig.DEFAULT_MODULE_FRAGMENT_NAME, tabType.getFragmentName());
    } else {
      if (codeLine.contains(RemoteModuleConfig.DEFAULT_MODULE_FRAGMENT_NAME)) {
        return codeLine.replace(RemoteModuleConfig.DEFAULT_MODULE_FRAGMENT_NAME, layoutGroups.get(0).getName());
      } else {
        return codeLine;
      }
    }
  }

  private String importFragments(String codeLine) {
    if (codeLine.contains("fragmentInfos.add")) {
      return "";
    }

    if (codeLine.contains("List<FragmentInfo> fragmentInfos = new ArrayList<>();") && layoutGroups.size() > 0) {
      final String ADD_FRAGMENT_STRING = "fragmentInfos.add(new FragmentInfo(" + SyntaxConstraints.REPLACE_STRING + ".class));";

      final String intent = FileUtils.getIndentOfLine(codeLine);

      for (LayoutGroup layoutGroup : layoutGroups) {
        codeLine += "\n";
        codeLine += intent + ADD_FRAGMENT_STRING.replace(SyntaxConstraints.REPLACE_STRING, layoutGroup.getName());
      }
      return codeLine;
    }
    return codeLine;
  }

  private String changePackage(String codeLine) {
    if (codeLine.contains("package")) {
      return codeLine.replace(remoteDirectory.getApplicationId(), getApplicationId());
    }

    return codeLine;
  }

  private String importDeclaredClasses(String codeLine, int depth
      , List<ClassInfo> addedPackageClassInfos) throws CommandException {
    List<ClassInfo> classInfos = ClassParser.extractClasses(codeLine);

    addedPackageClassInfos.addAll(classInfos);

    classInfos = uniquifyClasses(classInfos);

    if (classInfos.size() > 0) {
      for (ClassInfo classInfo : classInfos) {
        if (remoteDirectory.getRelativePathFromJavaDir(classInfo.getName() + Extension.JAVA.toString()) != null) {
          transformedClassInfos.add(classInfo);

          transformFileFromRemote(depth + 1, remoteDirectory.getChildFile(classInfo.getName(), Extension.JAVA));
        } else {
          for (String dependencyKey : externalLibrary.getKeys()) {
            if (classInfo.getName().equals(dependencyKey)) {
              appBuildGradleFile.addDependency(externalLibrary.getInfo(dependencyKey).getLibrary());
              androidManifestFile.addPermissions(externalLibrary.getInfo(dependencyKey).getPermissions());
              break;
            }
          }
        }
      }
    }

    return codeLine.replace(remoteDirectory.getApplicationId(), getApplicationId());
  }

  private List<ClassInfo> uniquifyClasses(List<ClassInfo> extractedClassInfos) {
    return uniquifyClasses(null, extractedClassInfos);
  }

  private List<ClassInfo> uniquifyClasses(List<ClassInfo> uniquifiedClassInfos, List<ClassInfo> extractedClassInfos) {
    if (uniquifiedClassInfos == null) {
      uniquifiedClassInfos = new ArrayList<>();
    }

    if (extractedClassInfos.size() <= 0) {
      return uniquifiedClassInfos;
    }

    ClassInfo classInfo = extractedClassInfos.get(0);
    boolean isExtractedClass = false;

    for (ClassInfo transformedClassInfo : transformedClassInfos) {
      if (transformedClassInfo.equals(classInfo)) {
        isExtractedClass = true;
      }
    }

    extractedClassInfos.remove(classInfo);

    if (!isExtractedClass) {
      boolean isUniquifiedClass = false;
      for (ClassInfo uniquifiedClassInfo : uniquifiedClassInfos) {
        if (uniquifiedClassInfo.equals(classInfo)) {
          isUniquifiedClass = true;
        }
      }

      if (!isUniquifiedClass) {
        uniquifiedClassInfos.add(classInfo);
      }
    }

    return uniquifyClasses(uniquifiedClassInfos, extractedClassInfos);
  }
}
