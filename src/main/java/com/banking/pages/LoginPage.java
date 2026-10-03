package com.banking.pages;

import com.banking.base.BasePage;
import com.banking.utils.DriverManager;
import com.banking.utils.WordReportGenerator;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginPage extends BasePage {

    private static final Logger LOG = Logger.getLogger(LoginPage.class.getName());
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

    private By usernameField = By.name("username");
    private By passwordField = By.name("password");
    private By loginButton = By.xpath("//input[@value='Log In']");
    private By logOutLink = By.linkText("Log Out");
    private By errorMessage = By.cssSelector("#rightPanel p.error");
    private By customerLoginHeading = By.xpath("//h2[text()='Customer Login']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void login(String username, String password) {
        type(usernameField, username);
        type(passwordField, password);

        // Screenshot AFTER entering data, BEFORE click
        // Uses intermediate method so it doesn't break Step 2 naming
        try {
            WebDriver driver = DriverManager.getDriver();
            byte[] filledFormScreenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String timestamp = LocalDateTime.now().format(TIMESTAMP);
            WordReportGenerator.addIntermediateScreenshot("Data Entered", timestamp, filledFormScreenshot);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Filled form screenshot failed", e);
        }

        click(loginButton);
    }

    public boolean isAccountsOverviewDisplayed() {
        return isDisplayed(By.xpath("//h1[text()='Accounts Overview']"));
    }

    public void logOff() {
        click(logOutLink);
        // Wait for the Customer Login page instead of sleeping, so the next screenshot
        // is taken on the login page and not on the old Accounts Overview page
        wait.until(ExpectedConditions.visibilityOfElementLocated(customerLoginHeading));
    }

    public String getLoginError() {
        return getText(errorMessage);
    }

    public boolean isCustomerLoginDisplayed() {
        return isDisplayed(customerLoginHeading);
    }
}