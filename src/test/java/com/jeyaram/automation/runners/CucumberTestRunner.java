package com.jeyaram.automation.runners;

import io.cucumber.junit.platform.engine.Cucumber;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.FEATURES_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Cucumber Test Runner for JUnit Platform
 * Provides comprehensive BDD test execution with reporting integration
 * 
 * @author Jeyaram K
 * @version 1.0.0
 * @since 2025-01-01
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = FEATURES_PROPERTY_NAME, value = "src/test/resources/features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.jeyaram.automation.stepdefinitions")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "pretty," +
            "html:target/cucumber-reports," +
            "json:target/cucumber-reports/Cucumber.json," +
            "junit:target/cucumber-reports/Cucumber.xml," +
            "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm," +
            "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
)
@Cucumber
public class CucumberTestRunner {
    // This class remains empty, it is used only as a holder for the annotations above
}
