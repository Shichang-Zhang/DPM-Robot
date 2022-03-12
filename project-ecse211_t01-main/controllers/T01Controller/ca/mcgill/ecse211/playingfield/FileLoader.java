package ca.mcgill.ecse211.playingfield;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Loads a playing field layout into resources from a file. 
 * 
 * @author Younes Boubekeur
 */
public class FileLoader {

  /** Returns a map of the layout in the specified file. */
  public static Map<String, Object> loadFrom(String filename) {
    var result = new HashMap<String, Object>();

    try {
      var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filename));
      doc.getDocumentElement().normalize();
      var nodeList = doc.getElementsByTagName("value");
      for (int i = 0; i < nodeList.getLength(); i++) {
        var node = nodeList.item(i);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          var element = (Element) node;
          result.put(element.getAttribute("key"), new BigDecimal(element.getTextContent()));
        }
      }
    } catch (SAXException | IOException | ParserConfigurationException e) {
      System.err.println("An error occurred while parsing the XML file: " + e.getMessage());
    }

    return result;
  }

}
