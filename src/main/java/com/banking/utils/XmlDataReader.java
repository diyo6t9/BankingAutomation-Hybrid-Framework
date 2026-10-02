package com.banking.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class XmlDataReader {

    public static String getValue(String fileName, String tagName) {
        try {
            String file = "testdata/" + fileName + "_Data.xml";
            InputStream is = XmlDataReader.class.getClassLoader().getResourceAsStream(file);

            if (is == null) {
                System.err.println("XML File NOT FOUND: " + file);
                return "";
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList allNodes = doc.getElementsByTagName("*");
            for (int i = 0; i < allNodes.getLength(); i++) {
                Node node = allNodes.item(i);
                if (node.getNodeName().equalsIgnoreCase(tagName)) {
                    return node.getTextContent().trim();
                }
            }
            System.err.println("Tag NOT FOUND: " + tagName + " in file: " + file);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String[] getLoginData(String xmlPath) {
        String[] data = new String[2];
        data[0] = getValue("Login", "Username");
        data[1] = getValue("Login", "Password");
        return data;
    }

    public static String getValue(String filePath, String parentTag, String childTag) {
        return getValue(parentTag, childTag);
    }
}