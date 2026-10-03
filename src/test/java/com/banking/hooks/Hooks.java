package com.banking.hooks;

import com.banking.utils.ConfigReader;
import com.banking.utils.DriverManager;
import com.banking.utils.WordReportGenerator;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Hooks {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

    // Cucumber creates a new Hooks object for every scenario, so these are safe in parallel runs
    private int stepCounter = 0;
    private String currentFeature = "";
    private String currentScenario = "";

    @Before
    public void setUp(Scenario scenario) {
        stepCounter = 0;
        currentScenario = scenario.getName();

        String uri = scenario.getUri().toString();
        currentFeature = uri.substring(uri.lastIndexOf('/') + 1).replace(".feature", "");

        WordReportGenerator.startScenario(currentFeature, currentScenario);

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        DriverManager.setDriver(driver); // set first, so @After can always quit it
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getExplicitWait()));
        driver.get(ConfigReader.getUrl());
    }

    @AfterStep
    public void afterEachStep(Scenario scenario) {
        WebDriver driver = DriverManager.getDriver();
        if (driver == null) return;

        try {
            waitForPageLoad(driver);
            stepCounter++;

            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String timestamp = LocalDateTime.now().format(DATE_TIME_FORMAT);

            // Step text comes from WordReportGenerator.setCurrentStep(...) called in each step definition
            WordReportGenerator.addStepResult(stepCounter, timestamp, screenshot, scenario.isFailed());
            scenario.attach(screenshot, "image/png", "Step-" + stepCounter);

        } catch (WebDriverException e) {
            scenario.log("Screenshot failed at Step-" + stepCounter + ": " + e.getMessage());
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            WordReportGenerator.endScenario(scenario.getStatus().name(), LocalDateTime.now().format(DATE_TIME_FORMAT));
        } finally {
            DriverManager.quitDriver(); // always runs, even if report saving fails
        }
    }

    // Waits until the browser reports the page as fully loaded, so the screenshot
    // is never taken in the middle of a navigation or redirect
    private void waitForPageLoad(WebDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(d ->
                "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState")));
    }
}