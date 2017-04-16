package com.androidstarterkit.tool;


import com.androidstarterkit.android.api.ElementConstraints;
import com.androidstarterkit.android.api.Extension;
import com.androidstarterkit.android.api.IntentConstraints;
import com.androidstarterkit.android.api.resource.AttributeContraints;
import com.androidstarterkit.SyntaxConstraints;
import com.androidstarterkit.file.AndroidManifest;
import com.androidstarterkit.file.directory.RemoteDirectory;
import com.androidstarterkit.file.directory.SourceDirectory;
import com.androidstarterkit.util.FileUtils;
import com.androidstarterkit.util.PrintUtils;
import com.androidstarterkit.util.StringUtils;
import com.androidstarterkit.util.SyntaxUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XmlEditor {
  private static final String TAG = XmlEditor.class.getSimpleName();

  private SourceDirectory sourceDirectory;
  private RemoteDirectory remoteDirectory;

  public interface OnEditListener {
    void onFinished();
  }

  public XmlEditor(SourceDirectory sourceDirectory, RemoteDirectory remoteDirectory) {
    this.sourceDirectory = sourceDirectory;
    this.remoteDirectory = remoteDirectory;
  }

  public void importAttrsOfRemoteMainActivity(OnEditListener onEditListener) {
    AndroidManifest androidManifestFile = remoteDirectory.getAndroidManifestFile();
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
        Element activityElement = sourceDirectory.getAndroidManifestFile().getMainActivityElement();
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
      writer.write(sourceDirectory.getAndroidManifestFile(), sourceDirectory.getAndroidManifestFile().getDocument());
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
    File moduleXmlFile = remoteDirectory.getChildFile(layoutName, Extension.XML);

    String codes = "";
    Scanner scanner = new Scanner(moduleXmlFile);

    System.out.println(PrintUtils.prefixDash(depth) + resourceTypeName + "/" + moduleXmlFile.getName());

    while (scanner.hasNext()) {
      String xmlCodeline = scanner.nextLine()
          .replace(remoteDirectory.getApplicationId(), sourceDirectory.getApplicationId())
          .replace(remoteDirectory.getMainActivityName(),
              sourceDirectory.getMainActivityName());

      ResourceMatcher matcher = new ResourceMatcher(xmlCodeline,
              ResourceMatcher.MatchType.XML_FILE);

      matcher.match((resourceTypeName1, layoutName1) -> {
        try {
          transferResourceXml(resourceTypeName1, layoutName1, depth + 1);
        } catch (FileNotFoundException e) {
          //TODO : handle
        }
      });

      matcher = new ResourceMatcher(xmlCodeline,
              ResourceMatcher.MatchType.XML_VALUE);
      matcher.match((resourceTypeName12, elementName) -> {
        try {
          transferElement(resourceTypeName12, elementName, depth + 1);
        } catch (FileNotFoundException e) {
          //TODO : handle
        }
      });

      codes += xmlCodeline + "\n";

      importDependencyToBuildGradle(xmlCodeline);
    }

    FileUtils.writeFile(sourceDirectory.getResPath() + "/" + resourceTypeName
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
    for (String dependencyKey : sourceDirectory.getExternalLibrary().getKeys()) {
      if (codeLine.contains(dependencyKey)) {
        sourceDirectory.getAppBuildGradleFile().addDependency(sourceDirectory.getExternalLibrary()
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

  private class XmlDomWriter {
    private void write(File file, Document document) throws TransformerException, IOException {
      document.setXmlStandalone(true);

      TransformerFactory tf = TransformerFactory.newInstance();
      tf.setAttribute("indent-number", new Integer(4));

      Transformer transformer = tf.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
      DOMSource source = new DOMSource(document);

      FileWriter fileWriter = new FileWriter(file);
      StreamResult writeStream = new StreamResult(fileWriter);

      transformer.transform(source, writeStream);
    }
  }
}
