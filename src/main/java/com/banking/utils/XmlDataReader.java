package com.banking.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XmlDataReader {
    public static String[] getLoginData(String xmlPath) {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(xmlPath));
            doc.getDocumentElement().normalize();
            Element user = (Element) doc.getElementsByTagName("user").item(0);
            String userId = user.getElementsByTagName("userId").item(0).getTextContent();
            String password = user.getElementsByTagName("password").item(0).getTextContent();
            return new String[]{userId, password};
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}