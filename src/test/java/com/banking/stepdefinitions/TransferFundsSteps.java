package com.banking.stepdefinitions;

import com.banking.pages.TransferFundsPage;
import com.banking.utils.DriverManager;
import com.banking.utils.WordReportGenerator;
import com.banking.utils.XmlDataReader;
import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class TransferFundsSteps {

    private WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    @And("User navigates to Transfer Funds page")
    public void user_navigates_to_transfer_funds_page() {
        WordReportGenerator.setCurrentStep("And User navigates to Transfer Funds page");
        TransferFundsPage transferPage = new TransferFundsPage(getDriver());
        transferPage.navigateToTransferFunds();
    }

    @When("User transfers amount")
    public void user_transfers_amount() {
        String amount = XmlDataReader.getValue("TransferFunds", "Amount");
        System.out.println("Transfer Amount from XML: " + amount);

        WordReportGenerator.setCurrentStep("When User transfers amount: $" + amount);

        TransferFundsPage transferPage = new TransferFundsPage(getDriver());
        transferPage.transferFunds(amount);
    }

    @Then("Transfer should be successful")
    public void transfer_should_be_successful() {
        WordReportGenerator.setCurrentStep("Then Transfer should be successful");
        TransferFundsPage transferPage = new TransferFundsPage(getDriver());
        Assert.assertTrue(transferPage.isTransferSuccessful(), "Transfer Failed - Success message not displayed!");
    }

    // ===================== NEGATIVE STEPS =====================

    @When("User transfers the amount {string}")
    public void user_transfers_the_amount(String amount) {
        WordReportGenerator.setCurrentStep("When User transfers the amount \"" + amount + "\"");
        TransferFundsPage transferPage = new TransferFundsPage(getDriver());
        transferPage.transferFunds(amount);
    }

    @Then("Transfer should not be successful")
    public void transfer_should_not_be_successful() {
        WordReportGenerator.setCurrentStep("Then Transfer should not be successful");
        TransferFundsPage transferPage = new TransferFundsPage(getDriver());
        Assert.assertFalse(transferPage.isTransferSuccessfulQuick(), "Transfer succeeded for an invalid amount");
    }
}