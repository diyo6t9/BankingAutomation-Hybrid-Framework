package com.banking.utils;

import org.openqa.selenium.WebDriver;

public class DriverManager {

    // ThreadLocal for parallel execution
    private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    public static void setDriver(WebDriver driver) {
        tlDriver.set(driver);
    }

    public static WebDriver getDriver() {
        return tlDriver.get();
    }

    public static void quitDriver() {
        if (tlDriver.get()!= null) {
            tlDriver.get().quit();
            tlDriver.remove();
        }
    }
}