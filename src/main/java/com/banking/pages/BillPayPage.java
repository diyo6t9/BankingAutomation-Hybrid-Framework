package com.banking.pages;

import com.banking.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.stream.Collectors;

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
    private final By anyError = By.cssSelector("span.error, p.error");

    private final WebDriverWait longWait;

    public BillPayPage(WebDriver driver) {
        super(driver);
        this.longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    // ---------- Shared private helpers (single copy of the form logic) ----------

    private void openBillPayForm() {
        wait.until(ExpectedConditions.presenceOfElementLocated(billPayLink));
        scrollTo(billPayLink);
        jsClick(billPayLink);
        longWait.until(ExpectedConditions.visibilityOfElementLocated(payeeName));
    }

    private void fillForm(String name, String addr, String cityName, String stateName, String zip,
                          String phoneNo, String accNo, String verifyAccNo, String amt) {
        type(payeeName, name);
        type(address, addr);
        type(city, cityName);
        type(state, stateName);
        type(zipCode, zip);
        type(phone, phoneNo);
        type(accountNumber, accNo);
        type(verifyAccount, verifyAccNo);
        type(amount, amt);
    }

    private void clickSendPayment() {
        // fromAccount is auto-selected, just wait for it to load
        longWait.until(ExpectedConditions.visibilityOfElementLocated(fromAccountDropdown));
        wait.until(ExpectedConditions.elementToBeClickable(sendPaymentButton));
        click(sendPaymentButton);
    }

    // ---------- Public actions ----------

    // Positive flow: fills the form, submits, and waits for the success title
    public void payBill(String name, String addr, String cityName, String stateName,
                        String zip, String phoneNo, String accNo, String amt) {
        submitBillPay(name, addr, cityName, stateName, zip, phoneNo, accNo, accNo, amt);
        longWait.until(ExpectedConditions.visibilityOfElementLocated(resultTitle));
    }

    // Negative flow: fills and submits, does NOT wait for the success title
    public void submitBillPay(String name, String addr, String cityName, String stateName,
                              String zip, String phoneNo, String accNo, String verifyAccNo, String amt) {
        openBillPayForm();
        fillForm(name, addr, cityName, stateName, zip, phoneNo, accNo, verifyAccNo, amt);
        clickSendPayment();
    }

    public void submitEmptyForm() {
        openBillPayForm();
        clickSendPayment();
    }

    // ---------- Result and error readers ----------

    public String getResultTitle() {
        return getText(resultTitle);
    }

    public String getResultAmount() {
        return getText(resultAmount);
    }

    // Waits until at least one error is actually visible, then returns only the visible errors.
    // The page can contain hidden error spans, so we must not rely on the first match being visible.
    public String getAllErrors() {
        return wait.until(d -> {
            String errors = d.findElements(anyError).stream()
                    .filter(WebElement::isDisplayed)
                    .map(e -> e.getText().trim())
                    .filter(t -> !t.isEmpty())
                    .collect(Collectors.joining(" | "));
            return errors.isEmpty() ? null : errors;
        });
    }
}