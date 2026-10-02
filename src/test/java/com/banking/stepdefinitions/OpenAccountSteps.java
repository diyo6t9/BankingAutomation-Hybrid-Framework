package com.banking.stepdefinitions;

import com.banking.pages.OpenAccountPage;
import com.banking.utils.DriverManager;
import com.banking.utils.WordReportGenerator;
import io.cucumber.java.en.*;
import org.testng.Assert;

public class OpenAccountSteps {
    OpenAccountPage openAccountPage;
    String newAccountNumber;

    @When("User opens a new SAVINGS account")
    public void user_opens_new_savings_account() {
        WordReportGenerator.setCurrentStep("When User opens a new SAVINGS account");
        openAccountPage = new OpenAccountPage(DriverManager.getDriver());
        newAccountNumber = openAccountPage.openNewAccount("SAVINGS");
    }

    @Then("User should see new account opened message")
    public void user_should_see_new_account_opened_message() {
        WordReportGenerator.setCurrentStep("Then User should see new account opened message - " + newAccountNumber);
        String title = openAccountPage.getResultTitle();
        Assert.assertTrue(title.contains("Account Opened!"), "Account not opened. Actual Title: " + title);
    }
}