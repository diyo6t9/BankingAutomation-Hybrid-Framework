package com.banking.config;
import java.io.FileInputStream;
import java.util.Properties;

public class ConfigReader {
    public static Properties prop;

    public static Properties initProp() {
        prop = new Properties();
        try {
            FileInputStream ip = new FileInputStream("src/main/java/com/banking/config/config.properties");
            prop.load(ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prop;
    }
}