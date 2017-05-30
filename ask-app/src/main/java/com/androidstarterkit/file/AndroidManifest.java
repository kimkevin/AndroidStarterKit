package com.androidstarterkit.file;

import com.androidstarterkit.android.api.ElementConstraints;
import com.androidstarterkit.android.api.Extension;
import com.androidstarterkit.android.api.IntentConstraints;
import com.androidstarterkit.android.api.manifest.Permission;
import com.androidstarterkit.android.api.resource.AttributeContraints;
import com.androidstarterkit.constraints.SyntaxConstraints;
import com.androidstarterkit.exception.ActivityNotFoundException;
import com.androidstarterkit.exception.PackageNotFoundException;
import com.androidstarterkit.injection.file.android.InjectionXmlFile;
import com.androidstarterkit.injection.model.ManifestConfig;
import com.androidstarterkit.injection.model.ManifestElement;
import com.androidstarterkit.util.FileUtils;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AndroidManifest extends InjectionXmlFile {
  private static final String TAG = AndroidManifest.class.getSimpleName();

  public static final String FILE_NAME = "AndroidManifest.xml";

  private Node applicationNode;
  private Node mainActivityNode;

  private String mainActivityName;
  private String packageName;

  public AndroidManifest(String pathname) {
    super(pathname + "/" + FILE_NAME);

    packageName = rootElement.getAttribute(SyntaxConstraints.IDENTIFIER_PACKAGE);

    if (packageName == null) {
      throw new PackageNotFoundException();
    }

    applicationNode = rootElement.getElementsByTagName(ElementConstraints.APPLICATION).item(0);
    mainActivityNode = getMainActivityNode();

    if (mainActivityNode == null) {
      throw new ActivityNotFoundException();
    }

    mainActivityName = FileUtils.getFilenameFromDotPath(((Element) mainActivityNode).getAttribute(AttributeContraints.NAME));
  }

  public void addPermissions(Permission... permissions) {
    if (permissions == null || permissions.length <= 0) {
      return;
    }

    for (Permission permission : permissions) {
      addUsesPermissionNode(permission.toString());
    }
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

  public void addMainActivityAttribute(String key, String value) {
    Element activityElement = (Element) mainActivityNode;
    activityElement.setAttribute(key, value);
  }

  private void addUsesPermissionNode(String value) {
    if (!getUsesPermissionNames().contains(value)) {
      Element usesPermissionElement = document.createElement(ElementConstraints.USES_PERMISSION);
      usesPermissionElement.setAttribute(AttributeContraints.NAME, value);
      rootElement.appendChild(usesPermissionElement);
    }
  }

  public Map<String, String> getMainActivityAttributes() {
    NamedNodeMap activityAttributeNodeMap = mainActivityNode.getAttributes();

    Map<String, String> attributes = new HashMap<>();
    if (activityAttributeNodeMap != null) {
      for (int i = 0, size = activityAttributeNodeMap.getLength(); i < size; i++) {
        Node attributeNode = activityAttributeNodeMap.item(i);
        attributes.put(attributeNode.getNodeName(), attributeNode.getNodeValue());
      }
    }

    return attributes;
  }

  @Override
  public void apply() {
    for (ManifestConfig config : configs) {
      for (ManifestElement element : config.getManifestElements()) {
        List<String> nodeNames = element.getNodes();

        Element parentElement = rootElement;
        for (int i = 0, li = nodeNames.size(); i < li; i++) {
          if (i < li - 1) {
            if (!nodeNames.get(i).equals(ElementConstraints.MANIFEST)) {
              parentElement = (Element) parentElement.getElementsByTagName(nodeNames.get(i)).item(0);
            }
          } else {
            Element targetElement = document.createElement(nodeNames.get(i));
            for (String attributeName : element.getAttributes().keySet()) {
              targetElement.setAttribute(attributeName, element.getAttributes().get(attributeName));
            }
            parentElement.appendChild(targetElement);
          }
        }
      }
    }

    super.apply();
  }



  private Node getMainActivityNode() {
    NodeList activityNodes = ((Element) applicationNode).getElementsByTagName(ElementConstraints.ACTIVITY);

    for (int i = 0, li = activityNodes.getLength(); i < li; i++) {
      Node activityNode = activityNodes.item(i);
      if (activityNode.getNodeType() == Node.ELEMENT_NODE) {
        Element activityElement = (Element) activityNode;

        if (activityElement.hasChildNodes()) {
          Node intentFilterNode = activityElement.getElementsByTagName(ElementConstraints.INTENT_FILTER).item(0);

          if (intentFilterNode != null && intentFilterNode.hasChildNodes()) {
            Node actionNode = ((Element) intentFilterNode).getElementsByTagName(ElementConstraints.ACTION).item(0);
            Element actionElement = (Element) actionNode;

            if (actionElement.getAttribute(AttributeContraints.NAME).equals(IntentConstraints.ACTION_MAIN)) {
              return activityElement;
            }
          }
        }
      }
    }

    return null;
  }

  private List<String> getUsesPermissionNames() {
    NodeList usesPermissionNodes = rootElement.getElementsByTagName(ElementConstraints.USES_PERMISSION);

    List<String> usesPermissionNames = new ArrayList<>();

    for (int i = 0, li = usesPermissionNodes.getLength(); i < li; i++) {
      NamedNodeMap attributes = usesPermissionNodes.item(i).getAttributes();
      String nameValue = attributes.getNamedItem(AttributeContraints.NAME).getNodeValue();
      usesPermissionNames.add(nameValue);
    }

    return usesPermissionNames;
  }
}
