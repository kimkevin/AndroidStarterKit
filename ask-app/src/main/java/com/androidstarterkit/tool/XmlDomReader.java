package com.androidstarterkit.tool;


import com.androidstarterkit.exception.IllegalDocumentException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XmlDomReader {
  private static final String TAG = XmlDomReader.class.getSimpleName();

  private Document document;

  public XmlDomReader(File file) {
    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

    DocumentBuilder documentBuilder;
    try {
      documentBuilder = builderFactory.newDocumentBuilder();
      document = documentBuilder.parse(file);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new IllegalDocumentException("failed to create document : " + file.getPath());
    }
    document.getDocumentElement().normalize();
  }

  public Document getDocument() {
    return document;
  }

  public Element getRootNode() {
    return document.getDocumentElement();
  }
}
