package com.androidstarterkit.editor;


import com.androidstarterkit.Extension;
import com.androidstarterkit.ResourcePattern;
import com.androidstarterkit.config.SyntaxConfig;
import com.androidstarterkit.module.AskModule;
import com.androidstarterkit.module.SampleModule;
import com.androidstarterkit.util.FileUtils;
import com.androidstarterkit.util.PrintUtils;
import com.androidstarterkit.util.StringUtils;
import com.androidstarterkit.util.SyntaxUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;

public class XmlEditor {
  private static final String TAG = XmlEditor.class.getSimpleName();

  private static final String ELEMENT_RESOURCE_NAME = "resources";

  private SampleModule sampleModule;
  private AskModule askModule;

  public XmlEditor(SampleModule sampleModule, AskModule askModule) {
    this.sampleModule = sampleModule;
    this.askModule = askModule;
  }

  public void importAttrsInAndroidManifest() {
    try {
      Scanner scanner = new Scanner(askModule.getAndroidManifestFile());
      boolean isFound = false;
      String activityBlock = "";

      while (scanner.hasNext()) {
        String codeLine = scanner.nextLine();

        if (isFound || SyntaxUtils.hasStartElement(codeLine, "activity")) {
          isFound = true;

          codeLine = codeLine.replace(AskModule.DEFAULT_MODULE_ACTIVITY_NAME
              , FileUtils.removeExtension(sampleModule.getMainActivityName()));

          activityBlock += codeLine;

          if (codeLine.contains("</activity>")) {
            break;
          } else {
            activityBlock += "\n";
          }
        }
      }

      scanner = new Scanner(sampleModule.getAndroidManifestFile());
      isFound = false;
      String codeLines = "";

      while (scanner.hasNext()) {
        String codeLine = scanner.nextLine();

        if (isFound || SyntaxUtils.hasStartElement(codeLine, "activity")) {
          isFound = true;

          codeLine = codeLine.replace(AskModule.DEFAULT_MODULE_ACTIVITY_NAME
              , FileUtils.removeExtension(sampleModule.getMainActivityName()));

          if (codeLine.contains("</activity>")) {
            codeLines += activityBlock + "\n";
            isFound = false;
          }
        } else {
          codeLines += codeLine + "\n";
        }

        importResourceByLine(codeLine, 0);
      }

      FileUtils.writeFile(sampleModule.getAndroidManifestFile(), codeLines);

      Matcher matcher = ResourcePattern.matcherValuesInXml(codeLines);
      while (matcher.find()) {
        final String childResourceTypeName = matcher.group(1);
        final String childElementName = matcher.group(2);

        importElement(childResourceTypeName, childElementName);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void importResourceByLine(String codeLine, int depth) {
    importResourcesXmls(codeLine, depth);
    importValueElements(codeLine, depth);
  }

  public void importResourceByFile(File file, int depth) {
    try {
      Scanner scanner = new Scanner(file);

      while (scanner.hasNext()) {
        String codeLine = scanner.nextLine();

        importResourceByLine(codeLine, depth);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void importResourcesXmls(String codeLine, int depth) {
    Matcher matcher = ResourcePattern.matcherFileInJava(codeLine);

    while (matcher.find()) {
      final String resourceTypeName = matcher.group(1);
      final String layoutName = matcher.group(2);

      try {
        File moduleLayoutFile = askModule.getChildFile(layoutName, Extension.XML);
        importLayout(resourceTypeName, layoutName);

        System.out.println(PrintUtils.prefixDash(depth) + "Transfering : " + moduleLayoutFile.getName());
      } catch (IOException | NullPointerException e) {
        e.printStackTrace();
      }
    }
  }

  private void importValueElements(String codeLine, int depth) {
    Matcher matcher = ResourcePattern.matcherValuesInJava(codeLine);

    while (matcher.find()) {
      final String resourceTypeName = matcher.group(1);
      final String layoutName = matcher.group(2);

      try {
        importElement(resourceTypeName, layoutName);
      } catch (IOException | NullPointerException e) {
        e.printStackTrace();
      }
    }
  }

  private void importLayout(String resourceType, String layoutName) throws FileNotFoundException {
    File moduleXmlFile = askModule.getChildFile(layoutName, Extension.XML);

    String codes = "";
    Scanner scanner = new Scanner(moduleXmlFile);

    while (scanner.hasNext()) {
      String xmlCodeLine = scanner.nextLine()
          .replace(askModule.getApplicationId(), sampleModule.getApplicationId())
          .replace(AskModule.DEFAULT_MODULE_ACTIVITY_NAME,
              FileUtils.removeExtension(sampleModule.getMainActivityName()));

      Matcher matcher = ResourcePattern.matcherFileInXml(xmlCodeLine);
      while (matcher.find()) {
        final String childResourceTypeName = matcher.group(1);
        final String childLayoutName = matcher.group(2);

        importLayout(childResourceTypeName, childLayoutName);
      }

      matcher = ResourcePattern.matcherValuesInXml(xmlCodeLine);
      while (matcher.find()) {
        final String childResourceTypeName = matcher.group(1);
        final String childElementName = matcher.group(2);

        importElement(childResourceTypeName, childElementName);
      }

      codes += xmlCodeLine + "\n";

      importDependencyToBuildGradle(xmlCodeLine);
    }

    FileUtils.writeFile(sampleModule.getResPath() + "/" + resourceType
        , moduleXmlFile.getName()
        , codes);
  }

  private void importElement(String resourceTypeName, String elementName) throws FileNotFoundException {
    List<File> valueFiles = askModule.getChildFiles(resourceTypeName + "s", Extension.XML);
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
          elementLines += xmlCodeLine + SyntaxConfig.NEWLINE;

          if (SyntaxUtils.hasEndElement(xmlCodeLine, resourceTypeName)) {
            break;
          } else {
            isCopying = true;
          }
        }
      }

      if (StringUtils.isValid(elementLines)) {
        String codeLines = "";
        String sampleValuePathname = FileUtils.linkPathWithSlash(sampleModule.getResPath()
            , relativeValueFolderPath
            , moduleValueFile.getName());

        File sampleValueFile = new File(FileUtils.linkPathWithSlash(sampleValuePathname));

        if (!sampleValueFile.exists()) {
          sampleValueFile = createNewXmlFile(sampleValuePathname);
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
          if (SyntaxUtils.hasEndElement(xmlCodeLine, ELEMENT_RESOURCE_NAME)
              && !codeLines.contains(elementLines)) {
            codeLines += elementLines;
          }

          codeLines += xmlCodeLine + SyntaxConfig.NEWLINE;
        }

        FileUtils.writeFile(sampleValueFile, codeLines);
      }
    }
  }

  private void importDependencyToBuildGradle(String codeLine) {
    for (String dependencyKey : sampleModule.getExternalLibrary().getKeys()) {
      if (codeLine.contains(dependencyKey)) {
        sampleModule.getBuildGradleFile().addDependency(sampleModule.getExternalLibrary()
            .getInfo(dependencyKey)
            .getLibrary());
        break;
      }
    }
  }

  private File createNewXmlFile(String pathName) {
    String codeLines = SyntaxUtils.createStartElement(ELEMENT_RESOURCE_NAME) + SyntaxConfig.NEWLINE;
    codeLines += SyntaxUtils.createEndElement(ELEMENT_RESOURCE_NAME) + SyntaxConfig.NEWLINE;

    File file = new File(FileUtils.linkPathWithSlash(pathName));
    FileUtils.writeFile(file, codeLines);
    return file;
  }
}
