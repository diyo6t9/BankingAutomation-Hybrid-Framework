package com.banking.pages;

import com.banking.utils.DriverManager;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {
    @FindBy(name = "username") WebElement txtUser;
    @FindBy(name = "password") WebElement txtPass;
    @FindBy(xpath = "//input[@value='Log In']") WebElement btnLogin;

    public LoginPage() {
        PageFactory.initElements(DriverManager.getDriver(), this);
    }

    public void login(String user, String pass) {
        txtUser.sendKeys(user);
        txtPass.sendKeys(pass);
        btnLogin.click();
    }
}