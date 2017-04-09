package com.androidstarterkit.file.base;


import com.androidstarterkit.tool.XmlDomParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlFile extends BaseFile {

  protected Document document;
  protected Element rootNode;

  public XmlFile(String pathName, String fileName) {
    super(pathName, fileName);

    XmlDomParser xmlDomParser = new XmlDomParser(this);
    document = xmlDomParser.getDocument();
    rootNode = document.getDocumentElement();
  }

  public Element getRootNode() {
    return rootNode;
  }

  public Document getDocument() {
    return document;
  }
}
