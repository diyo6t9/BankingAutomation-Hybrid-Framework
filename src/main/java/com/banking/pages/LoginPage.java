package com.banking.pages;

import com.banking.base.BasePage;
import com.banking.utils.DriverManager;
import com.banking.utils.WordReportGenerator;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoginPage extends BasePage {

    private By usernameField = By.name("username");
    private By passwordField = By.name("password");
    private By loginButton = By.xpath("//input[@value='Log In']");
    private By logOutLink = By.linkText("Log Out");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void login(String username, String password) {
        type(usernameField, username);
        type(passwordField, password);

        // Screenshot AFTER entering data, BEFORE click
        // Uses intermediate method so it doesn't break Step 2 naming
        try {
            Thread.sleep(800);
            WebDriver driver = DriverManager.getDriver();
            byte[] filledFormScreenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss"));
            WordReportGenerator.addIntermediateScreenshot("Data Entered", timestamp, filledFormScreenshot);
        } catch (Exception e) {
            System.out.println("Filled form screenshot failed: " + e.getMessage());
        }

        click(loginButton);
    }

    public boolean isAccountsOverviewDisplayed() {
        return isDisplayed(By.xpath("//h1[text()='Accounts Overview']"));
    }

    public void logOff() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {}
        click(logOutLink);
        try {
            Thread.sleep(1500); // wait for Customer Login page to load - fixes duplicate Accounts Overview image
        } catch (Exception e) {}
    }
}