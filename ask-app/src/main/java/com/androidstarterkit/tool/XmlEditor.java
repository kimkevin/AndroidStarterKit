package com.androidstarterkit.tool;


import com.androidstarterkit.android.api.ElementConstraints;
import com.androidstarterkit.android.api.Extension;
import com.androidstarterkit.android.api.IntentConstraints;
import com.androidstarterkit.android.api.resource.AttributeContraints;
import com.androidstarterkit.SyntaxConstraints;
import com.androidstarterkit.file.AndroidManifest;
import com.androidstarterkit.module.RemoteModule;
import com.androidstarterkit.module.SourceModule;
import com.androidstarterkit.util.FileUtils;
import com.androidstarterkit.util.PrintUtils;
import com.androidstarterkit.util.StringUtils;
import com.androidstarterkit.util.SyntaxUtils;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import javax.xml.transform.TransformerException;

public class XmlEditor {
  private static final String TAG = XmlEditor.class.getSimpleName();

  private SourceModule sourceModule;
  private RemoteModule remoteModule;

  public interface OnEditListener {
    void onFinished();
  }

  public XmlEditor(SourceModule sourceModule, RemoteModule remoteModule) {
    this.sourceModule = sourceModule;
    this.remoteModule = remoteModule;
  }

  public void importAttrsOfRemoteMainActivity(OnEditListener onEditListener) {
    AndroidManifest androidManifestFile = remoteModule.getAndroidManifestFile();
    System.out.println(PrintUtils.prefixDash(0) + androidManifestFile.getName());

    NodeList acitivityNodes = androidManifestFile
        .getRootNode()
        .getElementsByTagName(ElementConstraints.APPLICATION)
        .item(0)
        .getChildNodes();

    NamedNodeMap activityAttributeNodeMap = null;

    for (int i = 0, li = acitivityNodes.getLength(); i < li; i++) {
      Node activityNode = acitivityNodes.item(i);

      if (activityNode.getNodeType() == Node.ELEMENT_NODE) {
        Element activityElement = (Element) activityNode;

        if (activityElement.hasChildNodes()) {
          Node intentFilterNode = activityElement.getElementsByTagName(ElementConstraints.INTENT_FILTER).item(0);

          if (intentFilterNode != null && intentFilterNode.hasChildNodes()) {
            Node actionNode = ((Element) intentFilterNode).getElementsByTagName(ElementConstraints.ACTION).item(0);
            Element actionElement = (Element) actionNode;

            if (actionElement.getAttribute(AttributeContraints.NAME).equals(IntentConstraints.ACTION_MAIN)) {
              activityAttributeNodeMap = activityElement.getAttributes();
            }
          }
        }
      }
    }

    for (int k = 0, size = activityAttributeNodeMap.getLength(); k < size; k++) {
      Node attributeNode = activityAttributeNodeMap.item(k);

      if (!attributeNode.getNodeName().equals(AttributeContraints.NAME)) {
        Element activityElement = sourceModule.getAndroidManifestFile().getMainActivityElement();
        activityElement.setAttribute(attributeNode.getNodeName(), attributeNode.getNodeValue());

        ResourceMatcher matcher = new ResourceMatcher(attributeNode.getNodeValue(), ResourceMatcher.MatchType.XML_VALUE);
        matcher.match((String resourceTypeName, String elementName) -> {
          try {
            transferElement(resourceTypeName, elementName, 1);
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          }
        });
      }
    }

    XmlDomWriter writer = new XmlDomWriter();
    try {
      writer.write(sourceModule.getAndroidManifestFile(), sourceModule.getAndroidManifestFile().getDocument());
      onEditListener.onFinished();
    } catch (TransformerException | IOException e) {
      e.printStackTrace();
    }
  }

  public void importResourcesForJava(String codeLine, int depth) {
    ResourceMatcher matcher = new ResourceMatcher(codeLine,
            ResourceMatcher.MatchType.JAVA_FILE);
    matcher.match((resourceTypeName, layoutName) -> {
      try {
        transferResourceXml(resourceTypeName, layoutName, depth + 1);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    });

    matcher = new ResourceMatcher(codeLine,
            ResourceMatcher.MatchType.JAVA_VALUE);
    matcher.match((resourceTypeName, elementName) -> {
      try {
        transferElement(resourceTypeName, elementName, depth + 1);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    });
  }

  /**
   * @param resourceTypeName
   * @param layoutName
   * @param depth
   * @throws FileNotFoundException
   */
  private void transferResourceXml(String resourceTypeName, String layoutName, int depth) throws FileNotFoundException {
    File moduleXmlFile = remoteModule.getChildFile(layoutName, Extension.XML);

    String codes = "";
    Scanner scanner = new Scanner(moduleXmlFile);

    System.out.println(PrintUtils.prefixDash(depth) + resourceTypeName + "/" + moduleXmlFile.getName());

    while (scanner.hasNext()) {
      String xmlCodeLine = scanner.nextLine()
          .replace(remoteModule.getApplicationId(), sourceModule.getApplicationId())
          .replace(remoteModule.getMainActivityName(),
              sourceModule.getMainActivityName());

      ResourceMatcher matcher = new ResourceMatcher(xmlCodeLine,
              ResourceMatcher.MatchType.XML_FILE);

      matcher.match((resourceTypeName1, layoutName1) -> {
        try {
          transferResourceXml(resourceTypeName1, layoutName1, depth + 1);
        } catch (FileNotFoundException e) {
          //TODO : onMatched
        }
      });

      matcher = new ResourceMatcher(xmlCodeLine,
              ResourceMatcher.MatchType.XML_VALUE);
      matcher.match((resourceTypeName12, elementName) -> {
        try {
          transferElement(resourceTypeName12, elementName, depth + 1);
        } catch (FileNotFoundException e) {
          //TODO : onMatched
        }
      });

      codes += xmlCodeLine + "\n";

      importDependencyToBuildGradle(xmlCodeLine);
    }

    FileUtils.writeFile(sourceModule.getResPath() + "/" + resourceTypeName
        , moduleXmlFile.getName()
        , codes);
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
    List<File> valueFiles = remoteModule.getChildFiles(resourceTypeName + "s", Extension.XML);
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
        String sampleValuePathname = FileUtils.linkPathWithSlash(sourceModule.getResPath()
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

  private void importDependencyToBuildGradle(String codeLine) {
    for (String dependencyKey : sourceModule.getExternalLibrary().getKeys()) {
      if (codeLine.contains(dependencyKey)) {
        sourceModule.getBuildGradleFile().addDependency(sourceModule.getExternalLibrary()
            .getInfo(dependencyKey)
            .getLibrary());
        break;
      }
    }
  }

  private File createNewXmlFile(String pathName) {
    String codeLines = SyntaxUtils.createStartElement(ElementConstraints.RESOURCE) + SyntaxConstraints.NEWLINE;
    codeLines += SyntaxUtils.createEndElement(ElementConstraints.RESOURCE) + SyntaxConstraints.NEWLINE;

    File file = new File(FileUtils.linkPathWithSlash(pathName));
    FileUtils.writeFile(file, codeLines);
    return file;
  }
}
