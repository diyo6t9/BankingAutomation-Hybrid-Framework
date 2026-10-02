package com.banking.hooks;

import com.banking.utils.ConfigReader;
import com.banking.utils.Constants;
import com.banking.utils.DriverManager;
import com.banking.utils.WordReportGenerator;
import io.cucumber.java.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Hooks {

    private static boolean reportStarted = false;
    private static int stepCounter = 0;
    private static String currentScenario = "";
    private static String currentFeature = "";
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

    @Before(order = 0)
    public void initWordReport() {
        if (!reportStarted) {
            WordReportGenerator.initReport();
            reportStarted = true;
        }
    }

    @Before(order = 1)
    public void setUp(Scenario scenario) {
        stepCounter = 0;
        currentScenario = scenario.getName();
        try {
            String uri = scenario.getUri().toString();
            currentFeature = uri.substring(uri.lastIndexOf("/") + 1).replace(".feature", "");
        } catch (Exception e) {
            currentFeature = "Parabank";
        }

        WordReportGenerator.addScenarioHeader(currentFeature, currentScenario);

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getExplicitWait()));
        driver.get(ConfigReader.getUrl()); // <-- reads from your config.properties
        DriverManager.setDriver(driver);
    }

    @AfterStep
    public void afterEachStep(Scenario scenario) {
        WebDriver driver = DriverManager.getDriver();
        if (driver == null) return;

        try {
            Thread.sleep(1000); // wait for page to load - fixes redirect issue

            stepCounter++;

            // If LoginPage already set step via intermediate screenshot, don't override
            // Reflection is now backup only
            String gherkinText = "";
            try {
                java.lang.reflect.Field f = scenario.getClass().getDeclaredField("testCaseState");
                f.setAccessible(true);
                Object state = f.get(scenario);
                java.lang.reflect.Method m1 = state.getClass().getMethod("getLastTestStep");
                Object lastStep = m1.invoke(state);
                if (lastStep != null) {
                    java.lang.reflect.Method m2 = lastStep.getClass().getMethod("getStepText");
                    Object text = m2.invoke(lastStep);
                    if (text != null) {
                        gherkinText = text.toString();
                        try {
                            java.lang.reflect.Method m3 = lastStep.getClass().getMethod("getKeyword");
                            String keyword = (String) m3.invoke(lastStep);
                            if (keyword != null && !keyword.isBlank()) {
                                gherkinText = keyword.trim() + " " + gherkinText;
                            }
                        } catch (Exception ignore) {}
                    }
                }
            } catch (Exception ignore) {}

            if (!gherkinText.isEmpty()) {
                // Only set if not already set by LoginSteps / LoginPage intermediate
                WordReportGenerator.setCurrentStep(gherkinText);
            }

            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String timestamp = LocalDateTime.now().format(dtf);

            WordReportGenerator.addStepResult(currentFeature, currentScenario, stepCounter, timestamp, screenshot);
            scenario.attach(screenshot, "image/png", "Step-" + stepCounter);

        } catch (Exception e) {
            System.out.println("Screenshot failed at Step-" + stepCounter + ": " + e.getMessage());
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        String status = scenario.isFailed() ? "FAILED" : "PASSED";
        String timestamp = LocalDateTime.now().format(dtf);
        WordReportGenerator.addScenarioFooter(currentScenario, status, timestamp);
        WordReportGenerator.saveReport();

        if (DriverManager.getDriver() != null) {
            DriverManager.quitDriver();
        }
    }
}