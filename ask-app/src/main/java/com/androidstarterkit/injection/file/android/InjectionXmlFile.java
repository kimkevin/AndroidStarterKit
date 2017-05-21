package com.androidstarterkit.injection.file.android;


import com.androidstarterkit.injection.file.base.InjectionBaseFile;
import com.androidstarterkit.injection.model.ManifestConfig;
import com.androidstarterkit.tool.XmlDomReader;
import com.androidstarterkit.tool.XmlDomWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;

import javax.xml.transform.TransformerException;

public class InjectionXmlFile extends InjectionBaseFile<ManifestConfig> {
  protected Document document;
  protected Element rootElement;

  public InjectionXmlFile(String fullPathname) {
    super(fullPathname);

    XmlDomReader xmlDomParser = new XmlDomReader(this);
    document = xmlDomParser.getDocument();
    rootElement = document.getDocumentElement();
  }

  @Override
  public void apply() {
    XmlDomWriter xmlDomWriter = new XmlDomWriter();
    try {
      xmlDomWriter.writeDocument(this, document);
    } catch (TransformerException | IOException e) {
      e.printStackTrace();
    }
  }
}
