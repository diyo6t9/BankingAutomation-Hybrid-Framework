package com.banking.pages;

import com.banking.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class OpenAccountPage extends BasePage {

    private final By openNewAccountLink = By.linkText("Open New Account");
    private final By accountTypeDropdown = By.id("type");
    private final By fromAccountDropdown = By.id("fromAccountId");
    private final By openAccountButton = By.cssSelector("input[type='button'][value='Open New Account'].button");
    private final By newAccountId = By.id("newAccountId");
    private final By resultTitle = By.cssSelector("#openAccountResult h1");
    private final By errorMessage = By.cssSelector("#openAccountResult p.error, #openAccountError");

    public OpenAccountPage(WebDriver driver) {
        super(driver);
    }

    public String openNewAccount(String type) {
        System.out.println("STEP 1: Clicking Open New Account link");
        wait.until(ExpectedConditions.presenceOfElementLocated(openNewAccountLink));
        scrollTo(openNewAccountLink);
        jsClick(openNewAccountLink);

        System.out.println("STEP 2: Waiting for form");
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        longWait.until(ExpectedConditions.visibilityOfElementLocated(accountTypeDropdown));

        // DEBUG: check fromAccount options
        List<WebElement> options = driver.findElements(By.cssSelector("#fromAccountId option"));
        System.out.println("fromAccount options count: " + options.size());
        for(WebElement o : options) System.out.println(" - " + o.getText());

        selectByVisibleText(accountTypeDropdown, type);
        System.out.println("STEP 3: Selected " + type);

        System.out.println("STEP 4: Clicking Open button: " + openAccountButton);
        longWait.until(ExpectedConditions.elementToBeClickable(openAccountButton));
        click(openAccountButton); // use normal click now

        System.out.println("STEP 5: Waiting for result... Current URL: " + driver.getCurrentUrl());

        // Wait 30 sec for EITHER success or error
        try {
            longWait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(newAccountId),
                    ExpectedConditions.visibilityOfElementLocated(errorMessage),
                    ExpectedConditions.visibilityOfElementLocated(resultTitle)
            ));
        } catch (Exception e) {
            System.out.println("TIMEOUT PAGE SOURCE: " + driver.getPageSource());
            throw e;
        }

        if(isDisplayed(errorMessage)) {
            System.out.println("PARABANK ERROR: " + getText(errorMessage));
        }

        System.out.println("Result title: " + getText(resultTitle));
        return getText(newAccountId);
    }

    public String getResultTitle() {
        return getText(resultTitle);
    }
}