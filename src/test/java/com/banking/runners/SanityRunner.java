package com.banking.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/Features",
        glue = {"com.banking.hooks", "com.banking.stepdefinitions"},
        tags = "@Sanity",
        plugin = {"pretty", "html:output/cucumber.html"},
        monochrome = true
)
public class SanityRunner extends AbstractTestNGCucumberTests {
}