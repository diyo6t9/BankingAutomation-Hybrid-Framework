package com.banking.tests;
import com.banking.base.BaseClass;
import org.testng.annotations.Test;

public class LoginTest extends BaseClass {

    @Test
    public void verifyTitle() {
        String title = driver.getTitle();
        System.out.println("Page Title is: " + title);
    }
}