package com.banking.stepdefinitions;

import com.banking.pages.BillPayPage;
import com.banking.utils.DriverManager;
import com.banking.utils.WordReportGenerator;
import io.cucumber.java.en.*;
import org.testng.Assert;

public class BillPaySteps {
    BillPayPage billPayPage;

    @When("User pays bill with valid data")
    public void user_pays_bill_with_valid_data() {
        WordReportGenerator.setCurrentStep("When User pays bill with valid data");
        billPayPage = new BillPayPage(DriverManager.getDriver());
        // Static test data - you can move to XML later like Login
        billPayPage.payBill("Electric Company", "123 Main St", "Pune", "MH", "411001", "9876543210", "12345", "100");
    }

    @Then("User should see bill payment complete message")
    public void user_should_see_bill_payment_complete_message() {
        WordReportGenerator.setCurrentStep("Then User should see bill payment complete message");
        String title = billPayPage.getResultTitle();
        Assert.assertTrue(title.contains("Bill Payment Complete"), "Bill Pay failed. Actual: " + title);
    }
}