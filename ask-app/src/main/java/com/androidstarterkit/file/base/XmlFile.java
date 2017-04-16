package com.androidstarterkit.file.base;


import com.androidstarterkit.tool.XmlDomParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

public class XmlFile extends File {

  protected Document document;
  protected Element rootNode;

  public XmlFile(String pathName) {
    super(pathName);

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
