package io.oasp.ide.eclipse.configurator.core;

import io.oasp.ide.eclipse.configurator.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;


/**
 * Class to resolve variables within xml files.
 * @author trippl
 *
 */
public class XmlHandler {

  /**
   * {@link Resolver} to resolve variables within xml files.
   */
  private Resolver resolver;

  /**
   * Creates a new {@link XmlHandler} that uses the given {@link Resolver}
   * to resolve variables within xml files..
   * @param resolver - resolver for variables.
   */
  public XmlHandler(Resolver resolver) {

    this.resolver = resolver;
  }

  /**
   * Writes the resolved content of the xmlFile to the destination file.
   * @param xmlFile - xmlFile to be solved and written to the destination.
   * @param destination - destination file for the resolved content of the xmlFile.
   */
  public void update(File xmlFile, File destination) {

    try {

      DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

      Document document = docBuilder.parse(xmlFile);

      resolveVariables(document);

      writeDocument(document, destination);

    } catch (ParserConfigurationException e) {
    	Log.LOGGER
        .log(Level.ALL, "An parse error occurred during read of file: " + xmlFile.getAbsolutePath(), e.getStackTrace());
    } catch (SAXException e) {

    } catch (IOException e) {
      Log.LOGGER
          .log(Level.ALL, "An io error occurred during read of file: " + xmlFile.getAbsolutePath(), e.getStackTrace());
    }
  }

  /**
   * Writes the specified document to the specified destination file.
   * @param document - document to be written.
   * @param destination - destination file for the document.
   */
  private void writeDocument(Document document, File destination) {

    File parentDir = destination.getParentFile();
    if (!parentDir.exists()) {
      if (!parentDir.mkdirs()) {
        Log.LOGGER.log(Level.ALL, "Could not create required directories for file: " + destination.getAbsolutePath());
        return;
      }
    }

    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(document);
      StreamResult result = new StreamResult(destination);
      transformer.transform(source, result);
    } catch (TransformerException e) {
      Log.LOGGER.log(Level.ALL, "An transform error occurred during creatrion of file: " + destination.getAbsolutePath(),
          e.getStackTrace());
    }
  }

  /**
   * Calls {@link #resolveElement(Element)} for every element in the document.
   * @param document - xml document to be resolved.
   */
  private void resolveVariables(Document document) {

    NodeList nodeList = document.getElementsByTagName("*");
    for (int i = 0; i < nodeList.getLength(); i++) {
      Element element = (Element) nodeList.item(i);
      resolveElement(element);
    }
  }

  /**
   * Resolves the variables within the element's Text and calls
   * {@link #resolveAttributes(NamedNodeMap)}
   * to resolve it's attributes.
   * @param element - element to be resolved.
   */
  private void resolveElement(Element element) {
    resolveAttributes(element.getAttributes());
    NodeList nodeList = element.getChildNodes();

    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node instanceof Text) {
        Text text = (Text) node;
        text.setNodeValue(resolver.resolveVariables(text.getNodeValue()));
      }
    }
  }

  /**
   * Resolves the variables in the value of every attribute within the NamedNodeMap.
   * @param attributes - attributes to be resolved.
   */
  private void resolveAttributes(NamedNodeMap attributes) {
    for (int i = 0; i < attributes.getLength(); i++) {
      Attr attribute = (Attr) attributes.item(i);
      attribute.setValue(resolver.resolveVariables(attribute.getValue()));
    }
  }

}
