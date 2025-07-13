package com.jeyaram.automation.utils;

import com.microsoft.playwright.Page;
import io.appium.java_client.AppiumDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Screenshot utilities for capturing and managing screenshots
 * Supports Playwright, Selenium, and Appium drivers
 * 
 * @author Jeyaram K
 * @version 1.0.0
 * @since 2025-01-01
 */
public class ScreenshotUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = "test-results/screenshots";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");
    
    static {
        // Create screenshots directory if it doesn't exist
        try {
            Path screenshotPath = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(screenshotPath)) {
                Files.createDirectories(screenshotPath);
            }
        } catch (IOException e) {
            logger.error("Failed to create screenshot directory", e);
        }
    }
    
    /**
     * Capture screenshot using Playwright Page
     * 
     * @param page Playwright Page instance
     * @param description Screenshot description
     * @return Screenshot file path
     */
    public String captureScreenshot(Page page, String description) {
        try {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String fileName = sanitizeFileName(description) + "_" + timestamp + ".png";
            Path screenshotPath = Paths.get(SCREENSHOT_DIR, fileName);
            
            // Capture screenshot
            page.screenshot(new Page.ScreenshotOptions()
                .setPath(screenshotPath)
                .setFullPage(true));
            
            logger.info("Screenshot captured: {}", screenshotPath);
            return screenshotPath.toString();
            
        } catch (Exception e) {
            logger.error("Failed to capture Playwright screenshot", e);
            return null;
        }
    }
    
    /**
     * Capture screenshot using mobile driver (Appium)
     * 
     * @param driver Appium driver instance
     * @param description Screenshot description
     * @return Screenshot file path
     */
    public String captureMobileScreenshot(AppiumDriver driver, String description) {
        try {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String fileName = sanitizeFileName(description) + "_mobile_" + timestamp + ".png";
            Path screenshotPath = Paths.get(SCREENSHOT_DIR, fileName);
            
            // Capture screenshot
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            FileUtils.writeByteArrayToFile(screenshotPath.toFile(), screenshot);
            
            logger.info("Mobile screenshot captured: {}", screenshotPath);
            return screenshotPath.toString();
            
        } catch (Exception e) {
            logger.error("Failed to capture mobile screenshot", e);
            return null;
        }
    }
    
    /**
     * Capture element screenshot using Playwright
     * 
     * @param page Playwright Page instance
     * @param selector Element selector
     * @param description Screenshot description
     * @return Screenshot file path
     */
    public String captureElementScreenshot(Page page, String selector, String description) {
        try {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String fileName = sanitizeFileName(description) + "_element_" + timestamp + ".png";
            Path screenshotPath = Paths.get(SCREENSHOT_DIR, fileName);
            
            // Capture element screenshot
            page.locator(selector).screenshot(new com.microsoft.playwright.Locator.ScreenshotOptions()
                .setPath(screenshotPath));
            
            logger.info("Element screenshot captured: {}", screenshotPath);
            return screenshotPath.toString();
            
        } catch (Exception e) {
            logger.error("Failed to capture element screenshot", e);
            return null;
        }
    }
    
    /**
     * Capture screenshot on test failure
     * 
     * @param page Playwright Page instance
     * @param testName Test name
     * @param errorMessage Error message
     * @return Screenshot file path
     */
    public String captureFailureScreenshot(Page page, String testName, String errorMessage) {
        String description = String.format("FAILURE_%s_%s", testName, 
            errorMessage.replaceAll("[^a-zA-Z0-9]", "_"));
        return captureScreenshot(page, description);
    }
    
    /**
     * Capture mobile screenshot on test failure
     * 
     * @param driver Appium driver instance
     * @param testName Test name
     * @param errorMessage Error message
     * @return Screenshot file path
     */
    public String captureMobileFailureScreenshot(AppiumDriver driver, String testName, String errorMessage) {
        String description = String.format("FAILURE_%s_%s", testName, 
            errorMessage.replaceAll("[^a-zA-Z0-9]", "_"));
        return captureMobileScreenshot(driver, description);
    }
    
    /**
     * Create comparison screenshot for visual testing
     * 
     * @param page Playwright Page instance
     * @param baselineName Baseline screenshot name
     * @param description Screenshot description
     * @return Screenshot file path
     */
    public String captureComparisonScreenshot(Page page, String baselineName, String description) {
        try {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String fileName = String.format("%s_comparison_%s_%s.png", 
                sanitizeFileName(baselineName), sanitizeFileName(description), timestamp);
            Path screenshotPath = Paths.get(SCREENSHOT_DIR, fileName);
            
            page.screenshot(new Page.ScreenshotOptions()
                .setPath(screenshotPath)
                .setFullPage(true));
            
            logger.info("Comparison screenshot captured: {}", screenshotPath);
            return screenshotPath.toString();
            
        } catch (Exception e) {
            logger.error("Failed to capture comparison screenshot", e);
            return null;
        }
    }
    
    /**
     * Capture screenshot with custom options
     * 
     * @param page Playwright Page instance
     * @param description Screenshot description
     * @param fullPage Whether to capture full page
     * @param quality Image quality (0-100)
     * @return Screenshot file path
     */
    public String captureScreenshotWithOptions(Page page, String description, boolean fullPage, Integer quality) {
        try {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String fileName = sanitizeFileName(description) + "_" + timestamp + ".png";
            Path screenshotPath = Paths.get(SCREENSHOT_DIR, fileName);
            
            Page.ScreenshotOptions options = new Page.ScreenshotOptions()
                .setPath(screenshotPath)
                .setFullPage(fullPage);
            
            if (quality != null) {
                options.setQuality(quality);
            }
            
            page.screenshot(options);
            
            logger.info("Custom screenshot captured: {}", screenshotPath);
            return screenshotPath.toString();
            
        } catch (Exception e) {
            logger.error("Failed to capture custom screenshot", e);
            return null;
        }
    }
    
    /**
     * Get base64 encoded screenshot
     * 
     * @param page Playwright Page instance
     * @return Base64 encoded screenshot
     */
    public String getBase64Screenshot(Page page) {
        try {
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            return java.util.Base64.getEncoder().encodeToString(screenshot);
        } catch (Exception e) {
            logger.error("Failed to get base64 screenshot", e);
            return null;
        }
    }
    
    /**
     * Get base64 encoded mobile screenshot
     * 
     * @param driver Appium driver instance
     * @return Base64 encoded screenshot
     */
    public String getBase64MobileScreenshot(AppiumDriver driver) {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            logger.error("Failed to get base64 mobile screenshot", e);
            return null;
        }
    }
    
    /**
     * Clean up old screenshots
     * 
     * @param daysOld Number of days old to delete
     */
    public void cleanupOldScreenshots(int daysOld) {
        try {
            Path screenshotDir = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(screenshotDir)) {
                return;
            }
            
            long cutoffTime = System.currentTimeMillis() - (daysOld * 24L * 60L * 60L * 1000L);
            
            Files.walk(screenshotDir)
                .filter(Files::isRegularFile)
                .filter(path -> {
                    try {
                        return Files.getLastModifiedTime(path).toMillis() < cutoffTime;
                    } catch (IOException e) {
                        return false;
                    }
                })
                .forEach(path -> {
                    try {
                        Files.delete(path);
                        logger.debug("Deleted old screenshot: {}", path);
                    } catch (IOException e) {
                        logger.warn("Failed to delete old screenshot: {}", path, e);
                    }
                });
            
            logger.info("Cleanup completed for screenshots older than {} days", daysOld);
            
        } catch (Exception e) {
            logger.error("Failed to cleanup old screenshots", e);
        }
    }
    
    /**
     * Get screenshot directory path
     * 
     * @return Screenshot directory path
     */
    public String getScreenshotDirectory() {
        return SCREENSHOT_DIR;
    }
    
    /**
     * Sanitize file name for cross-platform compatibility
     * 
     * @param fileName Original file name
     * @return Sanitized file name
     */
    private String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "screenshot";
        }
        
        // Replace invalid characters with underscores
        return fileName.replaceAll("[^a-zA-Z0-9\\-_.]", "_")
                      .replaceAll("_{2,}", "_")
                      .trim();
    }
    
    /**
     * Check if screenshot directory exists and is writable
     * 
     * @return true if directory is accessible
     */
    public boolean isScreenshotDirectoryAccessible() {
        try {
            Path screenshotDir = Paths.get(SCREENSHOT_DIR);
            return Files.exists(screenshotDir) && Files.isWritable(screenshotDir);
        } catch (Exception e) {
            logger.error("Failed to check screenshot directory accessibility", e);
            return false;
        }
    }
}
