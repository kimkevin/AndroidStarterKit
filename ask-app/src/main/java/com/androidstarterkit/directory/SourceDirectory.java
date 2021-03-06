package com.androidstarterkit.directory;

import com.androidstarterkit.android.api.Extension;
import com.androidstarterkit.android.api.resource.AttributeContraints;
import com.androidstarterkit.android.api.resource.ResourceType;
import com.androidstarterkit.exception.CommandException;
import com.androidstarterkit.file.AndroidManifest;
import com.androidstarterkit.file.BuildGradle;
import com.androidstarterkit.file.MainActivity;
import com.androidstarterkit.file.ProguardRules;
import com.androidstarterkit.tool.ClassDisassembler;
import com.androidstarterkit.tool.ClassInfo;
import com.androidstarterkit.tool.ResourceTransfer;
import com.androidstarterkit.util.FileUtils;
import com.androidstarterkit.util.PrintUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceDirectory extends Directory {
  private RemoteDirectory remoteDirectory;

  private String projectPathname;
  private String mainPath;
  private String javaPath;
  private String resPath;
  private String layoutPath;

  private ResourceTransfer resourceTransfer;

  private List<ClassInfo> transformedClassInfos;

  public SourceDirectory(String projectPathname, String sourceModuleName, RemoteDirectory remoteDirectory) {
    super(projectPathname + "/" + sourceModuleName
        , new String[]{"java", "gradle", "xml"}
        , new String[]{"build", "libs", "test", "androidTest", "res"});
    this.projectPathname = projectPathname;
    this.remoteDirectory = remoteDirectory;

    transformedClassInfos = new ArrayList<>();

    resourceTransfer = new ResourceTransfer(this);

    mainPath = FileUtils.linkPathWithSlash(getPath(), "src/main");
    javaPath = FileUtils.linkPathWithSlash(mainPath, "java", applicationId.replaceAll("\\.", "/"));
    resPath = FileUtils.linkPathWithSlash(mainPath, "res");
    layoutPath = FileUtils.linkPathWithSlash(resPath, ResourceType.LAYOUT.toString());
  }

  public BuildGradle getProjectBuildGradle() {
    return new BuildGradle(projectPathname);
  }

  public ProguardRules getProguardRules() {
    return new ProguardRules(getPath());
  }

  public MainActivity getMainActivity() {
    return new MainActivity(getChildDirPath(getMainActivityExtName()), getMainActivityExtName());
  }

  public String getMainPath() {
    return mainPath;
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

  public void transformAndroidManifest() {
    AndroidManifest remoteAndroidManifestFile = remoteDirectory.getAndroidManifestFile();

    System.out.println(PrintUtils.prefixDash(0) + remoteAndroidManifestFile.getName());

    Map<String, String> remoteActivityAttributes = remoteAndroidManifestFile.getMainActivityAttributes();
    remoteActivityAttributes.keySet().stream()
        .filter(key -> !key.equals(AttributeContraints.NAME))
        .forEach(key -> {
          androidManifestFile.addMainActivityAttribute(key, remoteActivityAttributes.get(key));

          resourceTransfer.extractValuesInXml(remoteActivityAttributes.get(key)
              , (String resourceTypeName, String elementName) -> {
                try {
                  resourceTransfer.transferValueFromRemote(remoteDirectory, resourceTypeName, elementName, 1);
                } catch (FileNotFoundException e) {
                  e.printStackTrace();
                }
              });
        });

    androidManifestFile.apply();
  }

  public void takeFileFromRemote(File remoteFile, int depth) throws CommandException {
    final String remoteFileNameEx = remoteFile.getName();

    String sourceFullPathname;

    if (remoteFile instanceof MainActivity) {
      sourceFullPathname = FileUtils.linkPathWithSlash(javaPath
          , androidManifestFile.getMainActivityNameEx());
    } else {
      sourceFullPathname = FileUtils.linkPathWithSlash(javaPath
          , FileUtils.removeFirstSlash(remoteDirectory.getFilePathFromJavaDir(remoteFileNameEx))
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

    while (scanner.hasNext()) {
      String codeLine = scanner.nextLine();

      codeLine = changePackage(codeLine);

      if (remoteFile instanceof MainActivity) {
        codeLine = changeMainActivityName(codeLine);
      }

      resourceTransfer.extractResourceFileInJava(codeLine, (resourceTypeName, layoutName) -> {
        try {
          resourceTransfer.transferResourceFileFromRemote(remoteDirectory, resourceTypeName, layoutName, depth + 1);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
      });

      resourceTransfer.extractValuesInJava(codeLine, (resourceTypeName, elementName) -> {
        try {
          resourceTransfer.transferValueFromRemote(remoteDirectory, resourceTypeName, elementName, depth + 1);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
      });

      codeLines.add(codeLine);
    }

    File sourceFile = new File(sourceFullPathname);
    FileUtils.writeFile(sourceFile, codeLines);
    fileMap.put(sourceFile.getName(), sourceFile.getPath().replaceAll("/" + sourceFile.getName(), ""));

    findDeclaredClasses(depth, remoteFile.getPath());
  }

  private String changePackage(String codeLine) {
    if (codeLine.contains("package") || codeLine.contains("import")) {
      return codeLine.replace(remoteDirectory.getApplicationId(), getApplicationId());
    }

    return codeLine;
  }

  private String changeMainActivityName(String codeLine) {
    return codeLine.replace(remoteDirectory.getMainActivityName(), getMainActivityName());
  }

  private void findDeclaredClasses(int depth, String pathname) throws CommandException {
    Pattern pattern = Pattern.compile(".+(com/androidstarterkit/module/.+)");
    Matcher matcher = pattern.matcher(pathname);

    while (matcher.find()) {
      pathname = FileUtils.removeExtension(matcher.group(1));
    }

    ClassDisassembler javap = new ClassDisassembler(FileUtils.getRootPath() + "/ask-remote-module/build/intermediates/classes/debug"
        , RemoteDirectory.PACKAGE_NAME);
    javap.extractClasses(pathname);

    javap.getInternalClassInfos().stream()
        .filter(classInfo -> remoteDirectory.getFilePathFromJavaDir(classInfo.getName() + Extension.JAVA.toString()) != null)
        .filter(classInfo -> !isTransformed(classInfo))
        .forEach(classInfo -> {
          transformedClassInfos.add(classInfo);

          takeFileFromRemote(remoteDirectory.getChildFile(classInfo.getName(), Extension.JAVA), depth + 1);
        });

    javap.getExternalClassInfos().forEach(classInfo -> {
      for (String dependencyKey : externalLibrary.getKeys()) {
        if (classInfo.getName().equals(dependencyKey)) {
          appBuildGradleFile.addDependency(externalLibrary.getInfo(dependencyKey).getLibrary());
          androidManifestFile.addPermissions(externalLibrary.getInfo(dependencyKey).getPermissions());
          break;
        }
      }
    });

    androidManifestFile.apply();
  }

  private boolean isTransformed(ClassInfo classInfo) {
    for (ClassInfo transformedClassInfo : transformedClassInfos) {
      if (classInfo.equals(transformedClassInfo)) {
        return true;
      }
    }
    return false;
  }
}
