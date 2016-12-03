package com.androidstarterkit.tool;


import org.w3c.dom.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XmlDomWriter {
  private static final String TAG = XmlDomWriter.class.getSimpleName();

  public void write(File file, Document document) throws TransformerException, IOException {
    document.setXmlStandalone(true);

    TransformerFactory tf = TransformerFactory.newInstance();
    tf.setAttribute("indent-number", new Integer(4));

    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    DOMSource source = new DOMSource(document);

//    StreamResult writeStream = new StreamResult(System.out);

    FileWriter fileWriter = new FileWriter(file);
    StreamResult writeStream = new StreamResult(fileWriter);

    transformer.transform(source, writeStream);
  }
}
