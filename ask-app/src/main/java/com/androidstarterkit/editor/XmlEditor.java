package com.androidstarterkit.editor;


import com.androidstarterkit.Extension;
import com.androidstarterkit.ResourceMatcher;
import com.androidstarterkit.config.SyntaxConfig;
import com.androidstarterkit.module.AskModule;
import com.androidstarterkit.module.SampleModule;
import com.androidstarterkit.util.FileUtils;
import com.androidstarterkit.util.PrintUtils;
import com.androidstarterkit.util.StringUtils;
import com.androidstarterkit.util.SyntaxUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class XmlEditor {
  private static final String TAG = XmlEditor.class.getSimpleName();

  private static final String ELEMENT_RESOURCE_NAME = "resources";

  private SampleModule sampleModule;
  private AskModule askModule;

  public XmlEditor(SampleModule sampleModule, AskModule askModule) {
    this.sampleModule = sampleModule;
    this.askModule = askModule;
  }

  public void importAttrsOfAndroidManifest(int depth) {
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

        ResourceMatcher matcher = new ResourceMatcher(codeLine, new ResourceMatcher.XmlValueMatcher() {
          @Override
          public void matched(String resourceTypeName, String elementName) throws FileNotFoundException {
            transferElement(resourceTypeName, elementName, depth);
          }
        });
        matcher.match();
      }

      FileUtils.writeFile(sampleModule.getAndroidManifestFile(), codeLines);

      System.out.println(PrintUtils.prefixDash(depth) + askModule.getAndroidManifestFile().getName());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void importResourcesForJava(String codeLine, int depth) {
    ResourceMatcher matcher = new ResourceMatcher(codeLine, new ResourceMatcher.JavaFileMatcher() {
      @Override
      public void matched(String resourceTypeName, String layoutName) throws FileNotFoundException {
        transferResourceXml(resourceTypeName, layoutName, depth);
      }
    });
    matcher.match();

    matcher = new ResourceMatcher(codeLine, new ResourceMatcher.JavaValueMatcher() {
      @Override
      public void matched(String resourceTypeName, String elementName) throws FileNotFoundException {
        transferElement(resourceTypeName, elementName, depth);
      }
    });
    matcher.match();
  }

  /**
   * @param resourceTypeName
   * @param layoutName
   * @param depth
   * @throws FileNotFoundException
   */
  private void transferResourceXml(String resourceTypeName, String layoutName, int depth) throws FileNotFoundException {
    File moduleXmlFile = askModule.getChildFile(layoutName, Extension.XML);

    String codes = "";
    Scanner scanner = new Scanner(moduleXmlFile);

    while (scanner.hasNext()) {
      String xmlCodeLine = scanner.nextLine()
          .replace(askModule.getApplicationId(), sampleModule.getApplicationId())
          .replace(AskModule.DEFAULT_MODULE_ACTIVITY_NAME,
              FileUtils.removeExtension(sampleModule.getMainActivityName()));

      ResourceMatcher matcher = new ResourceMatcher(xmlCodeLine, new ResourceMatcher.XmlFileMatcher() {
        @Override
        public void matched(String resourceTypeName, String layoutName) throws FileNotFoundException {
          transferResourceXml(resourceTypeName, layoutName, depth);
        }
      });

      matcher.match();

      matcher = new ResourceMatcher(xmlCodeLine, new ResourceMatcher.XmlValueMatcher() {
        @Override
        public void matched(String resourceTypeName, String elementName) throws FileNotFoundException {
          transferElement(resourceTypeName, elementName, depth);
        }
      });
      matcher.match();

      codes += xmlCodeLine + "\n";

      importDependencyToBuildGradle(xmlCodeLine);
    }

    FileUtils.writeFile(sampleModule.getResPath() + "/" + resourceTypeName
        , moduleXmlFile.getName()
        , codes);

    System.out.println(PrintUtils.prefixDash(depth) + resourceTypeName + "/" + moduleXmlFile.getName());
  }

  /**
   * Imported element of xml for supported platform folders
   *
   * @param resourceTypeName
   * @param elementName
   * @param depth
   * @throws FileNotFoundException
   */
  private void transferElement(String resourceTypeName, String elementName, int depth) throws FileNotFoundException {
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
