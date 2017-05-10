package com.androidstarterkit.tool;


import com.androidstarterkit.android.api.ElementConstraints;
import com.androidstarterkit.android.api.Extension;
import com.androidstarterkit.constraints.SyntaxConstraints;
import com.androidstarterkit.directory.RemoteDirectory;
import com.androidstarterkit.directory.SourceDirectory;
import com.androidstarterkit.util.FileUtils;
import com.androidstarterkit.util.PrintUtils;
import com.androidstarterkit.util.StringUtils;
import com.androidstarterkit.util.SyntaxUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class XmlLineTransfer {
  private static final String TAG = XmlLineTransfer.class.getSimpleName();

  private SourceDirectory sourceDirectory;

  public XmlLineTransfer(SourceDirectory sourceDirectory) {
    this.sourceDirectory = sourceDirectory;
  }

  public void retrievalResourceFileInJava(String input, ResourceMatcher.Handler handler) {
    ResourceMatcher matcher = new ResourceMatcher(input, ResourceMatcher.MatchType.RES_FILE_IN_JAVA);
    matcher.match(handler);
  }

  public void retrievalValuesInJava(String codeLine, ResourceMatcher.Handler handler) {
    ResourceMatcher matcher = new ResourceMatcher(codeLine, ResourceMatcher.MatchType.RES_VALUE_IN_JAVA);
    matcher.match(handler);
  }

  public void retrievalResourceFileInXml(String input, ResourceMatcher.Handler handler) {
    ResourceMatcher matcher = new ResourceMatcher(input, ResourceMatcher.MatchType.RES_FILE_IN_XML);
    matcher.match(handler);
  }

  public void retrievalValuesInXml(String input, ResourceMatcher.Handler handler) {
    ResourceMatcher matcher = new ResourceMatcher(input, ResourceMatcher.MatchType.RES_VALUE_IN_XML);
    matcher.match(handler);
  }

  public void transferResourceFileFromRemote(RemoteDirectory remoteDirectory, String resourceTypeName, String layoutName
      , int depth) throws FileNotFoundException {
    File moduleXmlFile = remoteDirectory.getChildFile(layoutName, Extension.XML);

    String codelines = "";
    Scanner scanner = new Scanner(moduleXmlFile);

    System.out.println(PrintUtils.prefixDash(depth) + resourceTypeName + "/" + moduleXmlFile.getName());

    while (scanner.hasNext()) {
      String xmlCodeline = scanner.nextLine()
          .replace(remoteDirectory.getApplicationId(), sourceDirectory.getApplicationId())
          .replace(remoteDirectory.getMainActivityName(), sourceDirectory.getMainActivityName());

      retrievalResourceFileInXml(xmlCodeline, (resourceTypeName1, layoutName1) -> {
        try {
          transferResourceFileFromRemote(remoteDirectory, resourceTypeName1, layoutName1, depth + 1);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
      });

      retrievalValuesInXml(xmlCodeline, (resourceTypeName12, elementName) -> {
        try {
          transferValueFromRemote(remoteDirectory, resourceTypeName12, elementName, depth + 1);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
      });

      codelines += xmlCodeline + "\n";

      addDependencyToBuildGradle(xmlCodeline);
    }

    FileUtils.writeFile(sourceDirectory.getResPath() + "/" + resourceTypeName
        , moduleXmlFile.getName()
        , codelines);
  }

  public void transferValueFromRemote(RemoteDirectory remoteDirectory, String resourceTypeName
      , String elementName, int depth) throws FileNotFoundException {
    List<File> valueFiles = remoteDirectory.getChildFiles(resourceTypeName + "s", Extension.XML);
    if (valueFiles == null) {
      throw new FileNotFoundException();
    }

    for (File moduleValueFile : valueFiles) {
      String relativeValueFolderPath = moduleValueFile.getPath().replace(moduleValueFile.getName(), "");
      relativeValueFolderPath = relativeValueFolderPath.substring(relativeValueFolderPath.indexOf("values")
          , relativeValueFolderPath.length() - 1);

      Scanner scanner = new Scanner(moduleValueFile);

      String elementLines = "";
      boolean isCopying = false;

      // Get line from module xml file
      while (scanner.hasNext()) {
        String xmlCodeLine = scanner.nextLine();

        if (xmlCodeLine.contains(elementName) || isCopying) {
          elementLines += xmlCodeLine + SyntaxConstraints.NEWLINE;

          if (SyntaxUtils.hasEndElement(xmlCodeLine, resourceTypeName)) {
            break;
          } else {
            isCopying = true;
          }
        }
      }

      if (StringUtils.isValid(elementLines)) {
        String codeLines = "";
        String sampleValuePathname = FileUtils.linkPathWithSlash(sourceDirectory.getResPath()
            , relativeValueFolderPath
            , moduleValueFile.getName());

        File sampleValueFile = new File(FileUtils.linkPathWithSlash(sampleValuePathname));

        if (!sampleValueFile.exists()) {
          sampleValueFile = createResourceFile(sampleValuePathname);

          System.out.println(PrintUtils.prefixDash(depth) + resourceTypeName + "/" + sampleValueFile.getName());
        }

        scanner = new Scanner(sampleValueFile);
        boolean isDuplicatedElement = false;

        while (scanner.hasNext()) {
          String xmlCodeLine = scanner.nextLine();

          // Check if it has already element
          if (isDuplicatedElement || xmlCodeLine.contains(elementName)) {
            isDuplicatedElement = !SyntaxUtils.hasEndElement(xmlCodeLine, resourceTypeName);
            continue;
          }

          // Check if it is end of element
          if (SyntaxUtils.hasEndElement(xmlCodeLine, ElementConstraints.RESOURCE)
              && !codeLines.contains(elementLines)) {
            codeLines += elementLines;
          }

          codeLines += xmlCodeLine + SyntaxConstraints.NEWLINE;
        }

        FileUtils.writeFile(sampleValueFile, codeLines);
      }
    }
  }

  private void addDependencyToBuildGradle(String codeLine) {
    for (String dependencyKey : sourceDirectory.getExternalLibrary().getKeys()) {
      if (codeLine.contains(dependencyKey)) {
        sourceDirectory.getAppBuildGradleFile()
            .addDependency(sourceDirectory.getExternalLibrary().getInfo(dependencyKey).getLibrary());
        break;
      }
    }
  }

  private File createResourceFile(String pathName) {
    String codelines = SyntaxUtils.createStartElement(ElementConstraints.RESOURCE) + SyntaxConstraints.NEWLINE;
    codelines += SyntaxUtils.createEndElement(ElementConstraints.RESOURCE) + SyntaxConstraints.NEWLINE;

    File file = new File(pathName);
    FileUtils.writeFile(file, codelines);
    return file;
  }
}
