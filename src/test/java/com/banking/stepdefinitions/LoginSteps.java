package com.banking.stepdefinitions;

import com.banking.pages.LoginPage;
import com.banking.utils.ConfigReader;
import com.banking.utils.DriverManager;
import com.banking.utils.WordReportGenerator;
import com.banking.utils.XmlDataReader;
import io.cucumber.java.en.*;
import org.testng.Assert;

public class LoginSteps {
    LoginPage loginPage;

    // ===================== COMMON STEPS =====================

    @Given("User is on Parabank login page")
    public void user_is_on_login() {
        WordReportGenerator.setCurrentStep("Given User is on Parabank login page");
        loginPage = new LoginPage(DriverManager.getDriver());
    }

    @When("User logs in with valid data")
    public void user_logs_in() {
        WordReportGenerator.setCurrentStep("When User logs in with valid data");
        String[] data = XmlDataReader.getLoginData();
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
        String[] data = XmlDataReader.getLoginData();
        loginPage = new LoginPage(DriverManager.getDriver());
        loginPage.login(data[0], data[1]);
    }

    // ===================== NEGATIVE STEPS =====================

    @When("User logs in with username {string} and password {string}")
    public void user_logs_in_with_username_and_password(String username, String password) {
        WordReportGenerator.setCurrentStep(
                "When User logs in with username \"" + username + "\" and password \"" + password + "\"");
        loginPage.login(username, password);
    }

    @Then("User should see login error {string}")
    public void user_should_see_login_error(String expectedMessage) {
        WordReportGenerator.setCurrentStep("Then User should see login error \"" + expectedMessage + "\"");
        String actual = loginPage.getLoginError().trim();
        Assert.assertEquals(actual, expectedMessage, "Unexpected login error message");
    }

    @When("User opens the Accounts Overview page directly")
    public void user_opens_accounts_overview_directly() {
        WordReportGenerator.setCurrentStep("When User opens the Accounts Overview page directly");
        String overviewUrl = ConfigReader.getUrl().replace("index.htm", "overview.htm");
        DriverManager.getDriver().get(overviewUrl);
    }

    @Then("User should be redirected to the login page")
    public void user_should_be_redirected_to_login_page() {
        WordReportGenerator.setCurrentStep("Then User should be redirected to the login page");
        Assert.assertTrue(loginPage.isCustomerLoginDisplayed(),
                "Protected page was reachable after logout - login page not shown");
    }
}