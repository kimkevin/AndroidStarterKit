package com.androidstarterkit.tool;


import org.w3c.dom.Document;

public class XmlDomWriter {
  private static final String TAG = XmlDomWriter.class.getSimpleName();

  private Document document;

  public XmlDomWriter(Document document) {
    this.document = document;
  }

  public void write() {
//    File file = new File("/Users/kevin/Documents/git/AndroidStarterKit/ask-module/src/main/res/layout/activity_main.xml");
//    try {
//      XmlDomParser parser = new XmlDomParser(file);
//
//      // file:/Users/kevin/Documents/git/AndroidStarterKit/ask-module/src/main/res/layout/activity_sample_main.xml
////      System.out.println(parser.getDocument().getDocumentURI());
////      System.out.println(parser.getDocument().getXmlVersion());
////      System.out.println(parser.getDocument().getXmlEncoding());
//
//      TransformerFactory tf = TransformerFactory.newInstance();
//      tf.setAttribute("indent-number", new Integer(4));
//
//      // output DOM XML to console
//      Transformer transformer = tf.newTransformer();
//      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
//      DOMSource source = new DOMSource(parser.getDocument());
//
////      FileWriter fileWriter = new FileWriter(file);
//      StreamResult console = new StreamResult(System.out);
////      StreamResult console = new StreamResult(fileWriter);
//      transformer.transform(source, console);
//
//    } catch (ParserConfigurationException e) {
//      e.printStackTrace();
//    } catch (IOException e) {
//      e.printStackTrace();
//    } catch (SAXException e) {
//      e.printStackTrace();
//    } catch (TransformerConfigurationException e) {
//      e.printStackTrace();
//    } catch (TransformerException e) {
//      e.printStackTrace();
//    }
  }
}
