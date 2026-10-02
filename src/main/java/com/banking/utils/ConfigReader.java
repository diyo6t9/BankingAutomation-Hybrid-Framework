package com.banking.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties prop;

    static {
        try {
            prop = new Properties();
            FileInputStream fis = new FileInputStream(Constants.CONFIG_FILE_PATH);
            prop.load(fis);
            fis.close();
        } catch (IOException e) {
            throw new RuntimeException("Config file not found: " + Constants.CONFIG_FILE_PATH, e);
        }
    }

    public static String getUrl() {
        return prop.getProperty("url");
    }

    public static String getBrowser() {
        return prop.getProperty("browser");
    }

    public static int getImplicitWait() {
        return Integer.parseInt(prop.getProperty("implicitWait"));
    }

    public static int getExplicitWait() {
        return Integer.parseInt(prop.getProperty("explicitWait"));
    }
}