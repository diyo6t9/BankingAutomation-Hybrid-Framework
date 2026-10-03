package com.banking.stepdefinitions;

import com.banking.pages.BillPayPage;
import com.banking.utils.DriverManager;
import com.banking.utils.WordReportGenerator;
import com.banking.utils.XmlDataReader;
import io.cucumber.java.en.*;
import org.testng.Assert;

public class BillPaySteps {
    BillPayPage billPayPage;

    // Reads a value from src/test/resources/testdata/BillPay_Data.xml
    private static String data(String tag) {
        return XmlDataReader.getValue("BillPay", tag);
    }

    @When("User pays bill with valid data")
    public void user_pays_bill_with_valid_data() {
        WordReportGenerator.setCurrentStep("When User pays bill with valid data");
        billPayPage = new BillPayPage(DriverManager.getDriver());
        billPayPage.payBill(
                data("PayeeName"), data("Address"), data("City"), data("State"),
                data("ZipCode"), data("Phone"), data("AccountNumber"), data("Amount"));
    }

    @Then("User should see bill payment complete message")
    public void user_should_see_bill_payment_complete_message() {
        WordReportGenerator.setCurrentStep("Then User should see bill payment complete message");
        String title = billPayPage.getResultTitle();
        Assert.assertTrue(title.contains("Bill Payment Complete"), "Bill Pay failed. Actual: " + title);
    }

    // ===================== NEGATIVE STEPS =====================

    @When("User submits the bill pay form without entering any data")
    public void user_submits_empty_bill_pay_form() {
        WordReportGenerator.setCurrentStep("When User submits the bill pay form without entering any data");
        billPayPage = new BillPayPage(DriverManager.getDriver());
        billPayPage.submitEmptyForm();
    }

    // Payee details come from the XML; only account, verify account and amount vary per scenario
    @When("User submits bill pay with account {string}, verify account {string} and amount {string}")
    public void user_submits_bill_pay_with_account_and_amount(String account, String verifyAccount, String amount) {
        WordReportGenerator.setCurrentStep("When User submits bill pay with account \"" + account
                + "\", verify account \"" + verifyAccount + "\" and amount \"" + amount + "\"");
        billPayPage = new BillPayPage(DriverManager.getDriver());
        billPayPage.submitBillPay(
                data("PayeeName"), data("Address"), data("City"), data("State"),
                data("ZipCode"), data("Phone"), account, verifyAccount, amount);
    }

    @Then("User should see bill pay error {string}")
    public void user_should_see_bill_pay_error(String expectedError) {
        WordReportGenerator.setCurrentStep("Then User should see bill pay error \"" + expectedError + "\"");
        String allErrors = billPayPage.getAllErrors();
        Assert.assertTrue(allErrors.contains(expectedError),
                "Expected error '" + expectedError + "' but found: " + allErrors);
    }
}