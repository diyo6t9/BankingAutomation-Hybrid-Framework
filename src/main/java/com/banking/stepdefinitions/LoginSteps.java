package com.banking.stepdefinitions;

import com.banking.hooks.Hooks;
import com.banking.pages.LoginPage;
import com.banking.utils.DriverManager;
import com.banking.utils.XmlDataReader;
import io.cucumber.java.en.*;
import org.testng.Assert;

public class LoginSteps {
    LoginPage loginPage;

    @Given("User is on Parabank login page")
    public void user_is_on_login() {
        loginPage = new LoginPage();
    }

    @When("User logs in with valid data")
    public void user_logs_in() {
        String[] data = XmlDataReader.getLoginData(Hooks.XML_PATH);
        loginPage.login(data[0], data[1]);
    }

    @Then("User should see Accounts Overview")
    public void user_should_see_overview() {
        Assert.assertTrue(DriverManager.getDriver().getPageSource().contains("Accounts Overview"));
    }

    @And("User closes the browser")
    public void user_closes_browser() {
        DriverManager.quitDriver();
    }

    @Given("User is logged in to Parabank")
    public void user_is_logged_in() {
        String[] data = XmlDataReader.getLoginData(Hooks.XML_PATH);
        loginPage = new LoginPage();
        loginPage.login(data[0], data[1]);
    }
}