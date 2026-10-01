package com.banking.hooks;

import com.banking.utils.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.Duration;

public class Hooks {
    public static final String XML_PATH = "src/main/resources/data.BankingAutomation/Login_Data.xml";
    public static final String URL = "https://parabank.parasoft.com/parabank/index.htm";

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get(URL);
        DriverManager.setDriver(driver); // Save to ThreadLocal
    }

    @After
    public void tearDown(Scenario scenario) {
        // Screenshot on failure - mandatory for GEMS
        if (scenario.isFailed() && DriverManager.getDriver()!= null) {
            byte[] screenshot = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", scenario.getName());
        }
        DriverManager.quitDriver(); // Closes browser - your 1 line
    }
}