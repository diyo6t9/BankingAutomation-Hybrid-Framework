package com.banking.pages;

import com.banking.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class TransferFundsPage extends BasePage {

    private By transferFundsLink = By.linkText("Transfer Funds");
    private By amountInput = By.id("amount");
    private By fromAccountDropdown = By.id("fromAccountId");
    private By toAccountDropdown = By.id("toAccountId");
    private By transferButton = By.xpath("//input[@value='Transfer']");
    private By transferCompleteMsg = By.xpath("//h1[text()='Transfer Complete!']");

    public TransferFundsPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToTransferFunds() {
        click(transferFundsLink);
    }

    public void transferFunds(String amount) {
        type(amountInput, amount);

        // Select From account - first account
        Select fromSelect = new Select(driver.findElement(fromAccountDropdown));
        fromSelect.selectByIndex(0);

        // Select To account - second account if available
        Select toSelect = new Select(driver.findElement(toAccountDropdown));
        if (toSelect.getOptions().size() > 1) {
            toSelect.selectByIndex(1);
        } else {
            toSelect.selectByIndex(0);
        }

        click(transferButton);
    }

    public boolean isTransferSuccessful() {
        return isDisplayed(transferCompleteMsg);
    }
}