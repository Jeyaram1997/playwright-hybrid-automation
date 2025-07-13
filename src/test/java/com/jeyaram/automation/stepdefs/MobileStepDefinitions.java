package com.jeyaram.automation.stepdefs;

import com.jeyaram.automation.base.MobileBase;
import com.jeyaram.automation.utils.ScreenshotUtils;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

/**
 * Step definitions for Mobile testing scenarios
 * 
 * @author Jeyaram K
 * @version 1.0.0
 * @since 2025-01-01
 */
public class MobileStepDefinitions {
    
    private static final Logger logger = LoggerFactory.getLogger(MobileStepDefinitions.class);
    private MobileBase mobileBase;
    private ScreenshotUtils screenshotUtils;
    private AppiumDriver driver;
    
    public MobileStepDefinitions() {
        this.mobileBase = new MobileBase();
        this.screenshotUtils = new ScreenshotUtils();
    }
    
    @Given("I start the mobile application")
    public void i_start_the_mobile_application() {
        logger.info("Starting mobile application");
        try {
            mobileBase.initializeAndroidDriver("", "emulator", "11.0", "", "");
            driver = mobileBase.getDriver();
            logger.info("Mobile application started successfully");
        } catch (Exception e) {
            logger.error("Failed to start mobile application", e);
            throw new RuntimeException("Failed to start mobile application", e);
        }
    }
    
    @Given("I start the mobile application on {string} platform")
    public void i_start_the_mobile_application_on_platform(String platform) {
        logger.info("Starting mobile application on platform: {}", platform);
        try {
            if ("android".equalsIgnoreCase(platform)) {
                mobileBase.initializeAndroidDriver("", "emulator", "11.0", "", "");
            } else if ("ios".equalsIgnoreCase(platform)) {
                mobileBase.initializeIOSDriver("", "iPhone Simulator", "15.0", "");
            }
            driver = mobileBase.getDriver();
            logger.info("Mobile application started successfully on {}", platform);
        } catch (Exception e) {
            logger.error("Failed to start mobile application on {}", platform, e);
            throw new RuntimeException("Failed to start mobile application on " + platform, e);
        }
    }
    
    @When("I tap on element with id {string}")
    public void i_tap_on_element_with_id(String elementId) {
        logger.info("Tapping on element with id: {}", elementId);
        try {
            mobileBase.tap(By.id(elementId), 10);
            logger.info("Successfully tapped on element: {}", elementId);
        } catch (Exception e) {
            logger.error("Failed to tap on element: {}", elementId, e);
            screenshotUtils.captureMobileScreenshot(driver, "tap_failed_" + elementId);
            throw new RuntimeException("Failed to tap on element: " + elementId, e);
        }
    }
    
    @When("I tap on element with text {string}")
    public void i_tap_on_element_with_text(String text) {
        logger.info("Tapping on element with text: {}", text);
        try {
            By locator = By.xpath("//*[@text='" + text + "']");
            mobileBase.tap(locator, 10);
            logger.info("Successfully tapped on element with text: {}", text);
        } catch (Exception e) {
            logger.error("Failed to tap on element with text: {}", text, e);
            screenshotUtils.captureMobileScreenshot(driver, "tap_text_failed");
            throw new RuntimeException("Failed to tap on element with text: " + text, e);
        }
    }
    
    @When("I enter {string} in field with id {string}")
    public void i_enter_in_field_with_id(String text, String elementId) {
        logger.info("Entering text '{}' in field with id: {}", text, elementId);
        try {
            mobileBase.type(By.id(elementId), text, true, 10);
            logger.info("Successfully entered text in field: {}", elementId);
        } catch (Exception e) {
            logger.error("Failed to enter text in field: {}", elementId, e);
            screenshotUtils.captureMobileScreenshot(driver, "enter_text_failed_" + elementId);
            throw new RuntimeException("Failed to enter text in field: " + elementId, e);
        }
    }
    
    @When("I swipe {string}")
    public void i_swipe(String direction) {
        logger.info("Swiping: {}", direction);
        try {
            // Use MobileBase.SwipeDirection enum
            switch (direction.toLowerCase()) {
                case "up":
                    mobileBase.swipe(MobileBase.SwipeDirection.UP, 0.6, 1000);
                    break;
                case "down":
                    mobileBase.swipe(MobileBase.SwipeDirection.DOWN, 0.6, 1000);
                    break;
                case "left":
                    mobileBase.swipe(MobileBase.SwipeDirection.LEFT, 0.6, 1000);
                    break;
                case "right":
                    mobileBase.swipe(MobileBase.SwipeDirection.RIGHT, 0.6, 1000);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid swipe direction: " + direction);
            }
            logger.info("Successfully swiped: {}", direction);
        } catch (Exception e) {
            logger.error("Failed to swipe: {}", direction, e);
            screenshotUtils.captureMobileScreenshot(driver, "swipe_failed_" + direction);
            throw new RuntimeException("Failed to swipe: " + direction, e);
        }
    }
    
    @When("I wait for {int} seconds")
    public void i_wait_for_seconds(int seconds) {
        logger.info("Waiting for {} seconds", seconds);
        try {
            Thread.sleep(seconds * 1000L);
            logger.info("Wait completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Wait was interrupted", e);
        }
    }
    
    @When("I scroll to element with text {string}")
    public void i_scroll_to_element_with_text(String text) {
        logger.info("Scrolling to element with text: {}", text);
        try {
            mobileBase.scrollToElement(By.xpath("//*[@text='" + text + "']"), 5, MobileBase.SwipeDirection.DOWN);
            logger.info("Successfully scrolled to element with text: {}", text);
        } catch (Exception e) {
            logger.error("Failed to scroll to element with text: {}", text, e);
            screenshotUtils.captureMobileScreenshot(driver, "scroll_failed");
            throw new RuntimeException("Failed to scroll to element with text: " + text, e);
        }
    }
    
    @Then("I should see element with id {string}")
    public void i_should_see_element_with_id(String elementId) {
        logger.info("Verifying element is visible with id: {}", elementId);
        try {
            WebElement element = driver.findElement(By.id(elementId));
            Assert.assertTrue(element.isDisplayed(), "Element should be visible: " + elementId);
            logger.info("Element is visible: {}", elementId);
        } catch (Exception e) {
            logger.error("Element verification failed: {}", elementId, e);
            screenshotUtils.captureMobileScreenshot(driver, "verification_failed_" + elementId);
            throw new RuntimeException("Element verification failed: " + elementId, e);
        }
    }
    
    @Then("I should see text {string}")
    public void i_should_see_text(String expectedText) {
        logger.info("Verifying text is visible: {}", expectedText);
        try {
            WebElement element = driver.findElement(By.xpath("//*[@text='" + expectedText + "']"));
            Assert.assertTrue(element.isDisplayed(), "Text should be visible: " + expectedText);
            logger.info("Text is visible: {}", expectedText);
        } catch (Exception e) {
            logger.error("Text verification failed: {}", expectedText, e);
            screenshotUtils.captureMobileScreenshot(driver, "text_verification_failed");
            throw new RuntimeException("Text verification failed: " + expectedText, e);
        }
    }
    
    @Then("the field with id {string} should contain {string}")
    public void the_field_with_id_should_contain(String elementId, String expectedText) {
        logger.info("Verifying field {} contains text: {}", elementId, expectedText);
        try {
            WebElement element = driver.findElement(By.id(elementId));
            String actualText = element.getText();
            Assert.assertTrue(actualText.contains(expectedText), 
                "Field should contain text: " + expectedText + ", but found: " + actualText);
            logger.info("Field text verification passed");
        } catch (Exception e) {
            logger.error("Field text verification failed: {}", elementId, e);
            screenshotUtils.captureMobileScreenshot(driver, "field_verification_failed_" + elementId);
            throw new RuntimeException("Field text verification failed: " + elementId, e);
        }
    }
    
    @Then("I take a mobile screenshot")
    public void i_take_a_mobile_screenshot() {
        logger.info("Taking mobile screenshot");
        try {
            screenshotUtils.captureMobileScreenshot(driver, "mobile_step_screenshot");
            logger.info("Mobile screenshot taken successfully");
        } catch (Exception e) {
            logger.error("Failed to take mobile screenshot", e);
        }
    }
    
    @Then("I close the mobile application")
    public void i_close_the_mobile_application() {
        logger.info("Closing mobile application");
        try {
            mobileBase.cleanup();
            logger.info("Mobile application closed successfully");
        } catch (Exception e) {
            logger.error("Failed to close mobile application", e);
        }
    }
}
