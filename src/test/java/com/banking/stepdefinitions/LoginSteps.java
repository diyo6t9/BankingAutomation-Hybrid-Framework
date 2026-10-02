package com.banking.stepdefinitions;

import com.banking.pages.LoginPage;
import com.banking.utils.DriverManager;
import com.banking.utils.WordReportGenerator;
import com.banking.utils.XmlDataReader;
import io.cucumber.java.en.*;
import org.testng.Assert;

public class LoginSteps {
    LoginPage loginPage;
    private static final String XML_PATH = "src/test/resources/testdata/Login_Data.xml";

    @Given("User is on Parabank login page")
    public void user_is_on_login() {
        WordReportGenerator.setCurrentStep("Given User is on Parabank login page");
        loginPage = new LoginPage(DriverManager.getDriver());
    }

    @When("User logs in with valid data")
    public void user_logs_in() {
        WordReportGenerator.setCurrentStep("When User logs in with valid data");
        String[] data = XmlDataReader.getLoginData(XML_PATH);
        loginPage.login(data[0], data[1]);
    }

    @Then("User should see Accounts Overview")
    public void user_should_see_overview() {
        WordReportGenerator.setCurrentStep("Then User should see Accounts Overview");
        boolean isDisplayed = DriverManager.getDriver().getPageSource().contains("Accounts Overview");
        Assert.assertTrue(isDisplayed, "Accounts Overview not displayed - Login Failed");
    }

    @And("User logs off")
    public void user_logs_off() {
        WordReportGenerator.setCurrentStep("And User logs off");
        loginPage.logOff();
    }

    @Given("User is logged in to Parabank")
    public void user_is_logged_in() {
        WordReportGenerator.setCurrentStep("Given User is logged in to Parabank");
        String[] data = XmlDataReader.getLoginData(XML_PATH);
        loginPage = new LoginPage(DriverManager.getDriver());
        loginPage.login(data[0], data[1]);
    }
}