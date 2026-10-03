package com.banking.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

public class XmlDataReader {

    private XmlDataReader() {
        // utility class - no objects needed
    }

    // Reads the first tag named tagName (case-insensitive) from testdata/<fileName>_Data.xml
    public static String getValue(String fileName, String tagName) {
        String file = "testdata/" + fileName + "_Data.xml";

        try (InputStream is = XmlDataReader.class.getClassLoader().getResourceAsStream(file)) {
            if (is == null) {
                throw new IllegalStateException("XML test data file not found: " + file);
            }

            Document doc = createSecureBuilder().parse(is);
            doc.getDocumentElement().normalize();

            NodeList allNodes = doc.getElementsByTagName("*");
            for (int i = 0; i < allNodes.getLength(); i++) {
                Node node = allNodes.item(i);
                if (node.getNodeName().equalsIgnoreCase(tagName)) {
                    return node.getTextContent().trim();
                }
            }
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new IllegalStateException("Could not read XML test data file: " + file, e);
        }

        throw new IllegalStateException("Tag '" + tagName + "' not found in XML test data file: " + file);
    }

    public static String[] getLoginData() {
        String[] data = new String[2];
        data[0] = getValue("Login", "Username");
        data[1] = getValue("Login", "Password");
        return data;
    }

    // Parser with external entities and DOCTYPE declarations disabled (protects against XXE)
    private static DocumentBuilder createSecureBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        return factory.newDocumentBuilder();
    }
}