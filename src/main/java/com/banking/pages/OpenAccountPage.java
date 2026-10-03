package com.banking.pages;

import com.banking.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class OpenAccountPage extends BasePage {

    private final By openNewAccountLink = By.linkText("Open New Account");
    private final By accountTypeDropdown = By.id("type");
    private final By fromAccountOptions = By.cssSelector("#fromAccountId option");
    private final By openAccountButton = By.cssSelector("input[type='button'][value='Open New Account'].button");
    private final By newAccountId = By.id("newAccountId");
    private final By resultTitle = By.cssSelector("#openAccountResult h1");
    private final By errorMessage = By.cssSelector("#openAccountResult p.error, #openAccountError");

    private final WebDriverWait longWait;

    public OpenAccountPage(WebDriver driver) {
        super(driver);
        this.longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    // Opens a new account of the given type and returns the new account number
    public String openNewAccount(String type) {
        wait.until(ExpectedConditions.presenceOfElementLocated(openNewAccountLink));
        scrollTo(openNewAccountLink);
        jsClick(openNewAccountLink);

        longWait.until(ExpectedConditions.visibilityOfElementLocated(accountTypeDropdown));

        // The "from account" list is loaded by AJAX after the page opens.
        // Clicking the button before it is filled makes the request silently do nothing.
        longWait.until(ExpectedConditions.numberOfElementsToBeMoreThan(fromAccountOptions, 0));

        selectByVisibleText(accountTypeDropdown, type);

        longWait.until(ExpectedConditions.elementToBeClickable(openAccountButton));
        click(openAccountButton);

        // Wait for EITHER the success result or an error message
        longWait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(newAccountId),
                ExpectedConditions.visibilityOfElementLocated(errorMessage)
        ));

        // On success newAccountId is already present, so this check returns immediately
        if (driver.findElements(newAccountId).isEmpty()) {
            throw new IllegalStateException("Open account failed: " + getText(errorMessage));
        }

        return getText(newAccountId);
    }

    public String getResultTitle() {
        return getText(resultTitle);
    }
}