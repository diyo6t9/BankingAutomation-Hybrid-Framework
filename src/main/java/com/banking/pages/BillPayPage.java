package com.banking.pages;

import com.banking.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class BillPayPage extends BasePage {

    private final By billPayLink = By.linkText("Bill Pay");
    private final By payeeName = By.name("payee.name");
    private final By address = By.name("payee.address.street");
    private final By city = By.name("payee.address.city");
    private final By state = By.name("payee.address.state");
    private final By zipCode = By.name("payee.address.zipCode");
    private final By phone = By.name("payee.phoneNumber");
    private final By accountNumber = By.name("payee.accountNumber");
    private final By verifyAccount = By.name("verifyAccount");
    private final By amount = By.name("amount");
    private final By fromAccountDropdown = By.name("fromAccountId");
    private final By sendPaymentButton = By.cssSelector("input[type='button'][value='Send Payment']");
    private final By resultTitle = By.cssSelector("#billpayResult h1");
    private final By resultAmount = By.id("amount");

    public BillPayPage(WebDriver driver) {
        super(driver);
    }

    public void payBill(String name, String addr, String cityName, String stateName, String zip, String phoneNo, String accNo, String amt) {
        wait.until(ExpectedConditions.presenceOfElementLocated(billPayLink));
        scrollTo(billPayLink);
        jsClick(billPayLink);

        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        longWait.until(ExpectedConditions.visibilityOfElementLocated(payeeName));

        type(payeeName, name);
        type(address, addr);
        type(city, cityName);
        type(state, stateName);
        type(zipCode, zip);
        type(phone, phoneNo);
        type(accountNumber, accNo);
        type(verifyAccount, accNo);
        type(amount, amt);

        // fromAccount auto-selected, just wait for it
        longWait.until(ExpectedConditions.visibilityOfElementLocated(fromAccountDropdown));

        wait.until(ExpectedConditions.elementToBeClickable(sendPaymentButton));
        click(sendPaymentButton);

        longWait.until(ExpectedConditions.visibilityOfElementLocated(resultTitle));
    }

    public String getResultTitle() {
        return getText(resultTitle);
    }

    public String getResultAmount() {
        return getText(resultAmount);
    }
}