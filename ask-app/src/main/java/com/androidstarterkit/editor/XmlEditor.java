package com.androidstarterkit.editor;


import com.androidstarterkit.Extension;
import com.androidstarterkit.ResourcePattern;
import com.androidstarterkit.config.SyntaxConfig;
import com.androidstarterkit.file.BuildGradleFile;
import com.androidstarterkit.model.ExternalLibrary;
import com.androidstarterkit.module.AskModule;
import com.androidstarterkit.module.SampleModule;
import com.androidstarterkit.util.FileUtils;
import com.androidstarterkit.util.StringUtils;
import com.androidstarterkit.util.SyntaxUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;

public class XmlEditor {
  private static final String TAG = XmlEditor.class.getSimpleName();

  private static final String ELEMENT_RESOURCE_NAME = "resources";

  private static final String ELEMENT_START = "<" + SyntaxConfig.REPLACE_STRING + ">";
  private static final String ELEMENT_END = "</" + SyntaxConfig.REPLACE_STRING + ">";

  private String sampleResPath;
  private BuildGradleFile buildGradleFile;
  private ExternalLibrary externalLibrary;
  private String sampleApplicationId;

  public XmlEditor(String sampleResPath
      , String sampleApplicationId
      , BuildGradleFile buildGradleFile
      , ExternalLibrary externalLibrary) {
    this.sampleResPath = sampleResPath;
    this.sampleApplicationId = sampleApplicationId;
    this.buildGradleFile = buildGradleFile;
    this.externalLibrary = externalLibrary;
  }

  public void importLayout(String resourceType
      , String layoutName
      , SampleModule sampleModule
      , AskModule askModule) throws FileNotFoundException {
    File moduleXmlFile = askModule.getChildFile(layoutName, Extension.XML);

    String codes = "";
    Scanner scanner = new Scanner(moduleXmlFile);

    while (scanner.hasNext()) {
      String xmlCodeLine = scanner.nextLine()
          .replace(askModule.getApplicationId(), sampleApplicationId)
          .replace(SampleModule.DEFAULT_SAMPLE_ACTIVITY_NAME,
              FileUtils.removeExtension(sampleModule.getMainActivityName()));

      Matcher matcher = ResourcePattern.matcherFileInXml(xmlCodeLine);
      while (matcher.find()) {
        final String childResourceTypeName = matcher.group(1);
        final String childLayoutName = matcher.group(2);

        importLayout(childResourceTypeName, childLayoutName, sampleModule, askModule);
      }

      matcher = ResourcePattern.matcherValuesInXml(xmlCodeLine);
      while (matcher.find()) {
        final String childResourceTypeName = matcher.group(1);
        final String childElementName = matcher.group(2);

        importElement(childResourceTypeName, childElementName, sampleModule, askModule);
      }

      codes += xmlCodeLine + "\n";

      importDependencyToBuildGradle(xmlCodeLine);
    }

    FileUtils.writeFile(sampleResPath + "/" + resourceType
        , moduleXmlFile.getName()
        , codes);
  }

  public void importElement(String resourceTypeName, String elementName, SampleModule sampleModule, AskModule askModule) throws FileNotFoundException {
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

          if (SyntaxUtils.hasLastElement(xmlCodeLine, resourceTypeName)) {
            break;
          } else {
            isCopying = true;
          }
        }
      }

      if (StringUtils.isValid(elementLines)) {
        String codeLines = "";
        String sampleValuePathname = FileUtils.linkPathWithSlash(sampleResPath
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
            isDuplicatedElement = !SyntaxUtils.hasLastElement(xmlCodeLine, resourceTypeName);
            continue;
          }

          // Check if it is end of element
          if (SyntaxUtils.hasLastElement(xmlCodeLine, ELEMENT_RESOURCE_NAME)
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
    for (String dependencyKey : externalLibrary.getKeys()) {
      if (codeLine.contains(dependencyKey)) {
        buildGradleFile.addDependency(externalLibrary.getInfo(dependencyKey).getLibrary());
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
