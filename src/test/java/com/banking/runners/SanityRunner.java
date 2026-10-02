package com.banking.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.banking.stepdefinitions", "com.banking.hooks"},
        plugin = {"pretty", "html:output/cucumber.html", "json:output/cucumber.json"},
        monochrome = true
)
public class SanityRunner extends AbstractTestNGCucumberTests {
}