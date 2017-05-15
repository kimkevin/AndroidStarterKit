package com.androidstarterkit.file;

import com.androidstarterkit.android.api.ElementConstraints;
import com.androidstarterkit.android.api.Extension;
import com.androidstarterkit.android.api.IntentConstraints;
import com.androidstarterkit.android.api.manifest.Permission;
import com.androidstarterkit.android.api.resource.AttributeContraints;
import com.androidstarterkit.constraints.SyntaxConstraints;
import com.androidstarterkit.exception.ActivityNotFoundException;
import com.androidstarterkit.exception.PackageNotFoundException;
import com.androidstarterkit.file.base.XmlFile;
import com.androidstarterkit.tool.MatcherTask;
import com.androidstarterkit.tool.XmlDomWriter;
import com.androidstarterkit.util.FileUtils;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerException;

public class AndroidManifest extends XmlFile {
  private static final String TAG = AndroidManifest.class.getSimpleName();

  private static final String USES_PERMISSION = "<uses-permission android:name=\"android.permission."
      + SyntaxConstraints.REPLACE_STRING + "\"/>";
  public static final String FILE_NAME = "AndroidManifest.xml";

  private String mainActivityName;
  private String packageName;

  private Node applicationNode;
  private Node activityNode;

  public AndroidManifest(String pathname) {
    super(pathname + "/" + FILE_NAME);

    packageName = rootNode.getAttribute(SyntaxConstraints.IDENTIFIER_PACKAGE);

    if (packageName == null) {
      throw new PackageNotFoundException();
    }

    applicationNode = rootNode.getElementsByTagName(ElementConstraints.APPLICATION).item(0);
    NodeList acitivityNodes = applicationNode.getChildNodes();

    for (int i = 0, li = acitivityNodes.getLength(); i < li; i++) {
      Node activityNode = acitivityNodes.item(i);
      if (activityNode.getNodeType() == Node.ELEMENT_NODE) {
        Element activityElement = (Element) activityNode;

        if (activityElement.hasChildNodes()) {
          Node intentFilterNode = activityElement.getElementsByTagName(ElementConstraints.INTENT_FILTER).item(0);

          if (intentFilterNode != null && intentFilterNode.hasChildNodes()) {
            Node actionNode = ((Element) intentFilterNode).getElementsByTagName(ElementConstraints.ACTION).item(0);
            Element actionElement = (Element) actionNode;

            if (actionElement.getAttribute(AttributeContraints.NAME)
                .equals(IntentConstraints.ACTION_MAIN)) {
              this.activityNode = activityElement;

              String regEx = ".(\\w+)$";
              MatcherTask task = new MatcherTask(regEx, activityElement.getAttribute(AttributeContraints.NAME));
              task.match(1, matched -> mainActivityName = matched);
            }
          }
        }
      }
    }

    if (mainActivityName == null) {
      throw new ActivityNotFoundException();
    }
  }

  public void addPermissions(Permission... permissions) {
    if (permissions == null || permissions.length <= 0) {
      return;
    }

    List<String> lineList = FileUtils.readFileAsString(this);

    for (Permission permission : permissions) {
      lineList = addLineToElement("manifest",
          permission,
          lineList);
    }

    FileUtils.writeFile(this, lineList);
  }

  public String getMainActivityName() {
    return mainActivityName;
  }

  public String getMainActivityNameEx() {
    return mainActivityName + Extension.JAVA;
  }

  public String getPackageName() {
    return packageName;
  }

  public void writeDocument() {
    XmlDomWriter xmlDomWriter = new XmlDomWriter();
    try {
      xmlDomWriter.writeDocument(this, getDocument());
    } catch (TransformerException | IOException e) {
      e.printStackTrace();
    }
  }

  public String getApplicationRelativePathname() {
    NamedNodeMap applicationAttributeNodeMap = applicationNode.getAttributes();

    for (int i = 0, size = applicationAttributeNodeMap.getLength(); i < size; i++) {
      Node attributeNode = applicationAttributeNodeMap.item(i);
      if (attributeNode.getNodeName().equals(AttributeContraints.NAME)) {
        return attributeNode.getNodeValue();
      }
    }
    return null;
  }

  public void addApplicationAttribute(String key, String value) {
    Element applicationElement = (Element) applicationNode;
    applicationElement.setAttribute(key, value);
  }

  public void addActivityAttribute(String key, String value) {
    Element activityElement = (Element) activityNode;
    activityElement.setAttribute(key, value);
  }

  public Map<String, String> getActivityAttributes() {
    Map<String, String> attributes = new HashMap<>();
    NodeList acitivityNodes = applicationNode.getChildNodes();

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

    if (activityAttributeNodeMap != null) {
      for (int i = 0, size = activityAttributeNodeMap.getLength(); i < size; i++) {
        Node attributeNode = activityAttributeNodeMap.item(i);
        attributes.put(attributeNode.getNodeName(), attributeNode.getNodeValue());
      }
    }

    return attributes;
  }

  private List<String> addLineToElement(String elementName, Permission permission, List<String> lineList) {
    String indent = SyntaxConstraints.DEFAULT_INDENT;

    String reg = "\\<\\/\\s*" + elementName + "\\s*\\>";
    Pattern pattern = Pattern.compile(reg);

    for (int i = 0, li = lineList.size(); i < li; i++) {
      final String codeLine = lineList.get(i);

      if (codeLine.contains(permission.name())) {
        return lineList;
      }

      Matcher matcher = pattern.matcher(codeLine);

      if (matcher.find()) {
        lineList.add(i, indent + getPermissionToString(permission));
      }
    }

    return lineList;
  }

  private String getPermissionToString(Permission permission) {
    return USES_PERMISSION.replace(SyntaxConstraints.REPLACE_STRING, permission.name());
  }
}
