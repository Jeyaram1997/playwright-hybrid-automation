package com.jeyaram.automation.base;

import com.jeyaram.automation.config.ConfigManager;
import com.jeyaram.automation.reporting.AllureManager;
import com.jeyaram.automation.utils.CommandLineTracker;
import com.jeyaram.automation.utils.ScreenshotUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Mobile Testing Base class providing comprehensive mobile automation capabilities
 * Supports both Android and iOS platforms with gesture support
 * 
 * @author Jeyaram K
 * @version 1.0.0
 * @since 2025-01-01
 */
public class MobileBase {
    
    private static final Logger logger = LoggerFactory.getLogger(MobileBase.class);
    
    protected static AppiumDriver driver;
    protected static AppiumDriverLocalService appiumService;
    protected static WebDriverWait wait;
    
    protected final ConfigManager configManager = ConfigManager.getInstance();
    protected final ScreenshotUtils screenshotUtils = new ScreenshotUtils();
    private static final CommandLineTracker cmdTracker = new CommandLineTracker();
    
    // Platform types
    public enum Platform {
        ANDROID, IOS
    }
    
    // Gesture directions
    public enum SwipeDirection {
        UP, DOWN, LEFT, RIGHT
    }
    
    /**
     * Start Appium server programmatically
     * 
     * @param port Server port
     * @param host Server host
     */
    public void startAppiumServer(int port, String host) {
        try {
            cmdTracker.trackMethodUsage("startAppiumServer", 
                Map.of("port", port, "host", host));
            
            AppiumServiceBuilder builder = new AppiumServiceBuilder()
                .withIPAddress(host)
                .usingPort(port)
                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                .withArgument(GeneralServerFlag.LOG_LEVEL, "error");
            
            appiumService = AppiumDriverLocalService.buildService(builder);
            appiumService.start();
            
            logger.info("Appium server started on {}:{}", host, port);
            AllureManager.addStep("Appium server started: " + host + ":" + port);
            
        } catch (Exception e) {
            logger.error("Failed to start Appium server", e);
            throw new RuntimeException("Appium server startup failed", e);
        }
    }
    
    /**
     * Initialize mobile driver for Android
     * 
     * @param appPath Path to APK file
     * @param deviceName Device name
     * @param platformVersion Android version
     * @param appPackage App package name
     * @param appActivity App activity name
     */
    public void initializeAndroidDriver(String appPath, String deviceName, String platformVersion,
                                      String appPackage, String appActivity) {
        try {
            cmdTracker.trackMethodUsage("initializeAndroidDriver", 
                Map.of("appPath", appPath, "deviceName", deviceName, 
                       "platformVersion", platformVersion, "appPackage", appPackage));
            
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("platformName", "Android");
            capabilities.setCapability("deviceName", deviceName);
            capabilities.setCapability("platformVersion", platformVersion);
            capabilities.setCapability("automationName", "UiAutomator2");
            
            if (appPath != null && !appPath.isEmpty()) {
                capabilities.setCapability("app", new File(appPath).getAbsolutePath());
            } else {
                capabilities.setCapability("appPackage", appPackage);
                capabilities.setCapability("appActivity", appActivity);
            }
            
            // Additional Android capabilities
            capabilities.setCapability("autoGrantPermissions", true);
            capabilities.setCapability("noReset", false);
            capabilities.setCapability("fullReset", false);
            capabilities.setCapability("newCommandTimeout", 300);
            
            URL serverUrl = appiumService != null ? appiumService.getUrl() : 
                new URL("http://localhost:4723/wd/hub");
            
            driver = new AndroidDriver(serverUrl, capabilities);
            wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            
            logger.info("Android driver initialized successfully");
            AllureManager.addStep("Android driver initialized");
            
        } catch (Exception e) {
            logger.error("Failed to initialize Android driver", e);
            throw new RuntimeException("Android driver initialization failed", e);
        }
    }
    
    /**
     * Initialize mobile driver for iOS
     * 
     * @param appPath Path to IPA file
     * @param deviceName Device name
     * @param platformVersion iOS version
     * @param bundleId App bundle ID
     */
    public void initializeIOSDriver(String appPath, String deviceName, String platformVersion,
                                   String bundleId) {
        try {
            cmdTracker.trackMethodUsage("initializeIOSDriver", 
                Map.of("appPath", appPath, "deviceName", deviceName, 
                       "platformVersion", platformVersion, "bundleId", bundleId));
            
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("platformName", "iOS");
            capabilities.setCapability("deviceName", deviceName);
            capabilities.setCapability("platformVersion", platformVersion);
            capabilities.setCapability("automationName", "XCUITest");
            
            if (appPath != null && !appPath.isEmpty()) {
                capabilities.setCapability("app", new File(appPath).getAbsolutePath());
            } else {
                capabilities.setCapability("bundleId", bundleId);
            }
            
            // Additional iOS capabilities
            capabilities.setCapability("noReset", false);
            capabilities.setCapability("fullReset", false);
            capabilities.setCapability("newCommandTimeout", 300);
            capabilities.setCapability("wdaLocalPort", 8100);
            
            URL serverUrl = appiumService != null ? appiumService.getUrl() : 
                new URL("http://localhost:4723/wd/hub");
            
            driver = new IOSDriver(serverUrl, capabilities);
            wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            
            logger.info("iOS driver initialized successfully");
            AllureManager.addStep("iOS driver initialized");
            
        } catch (Exception e) {
            logger.error("Failed to initialize iOS driver", e);
            throw new RuntimeException("iOS driver initialization failed", e);
        }
    }
    
    /**
     * Find element with enhanced error handling
     * 
     * @param by Locator strategy
     * @param timeout Timeout in seconds
     * @return WebElement
     */
    public WebElement findElement(By by, int timeout) {
        try {
            cmdTracker.trackMethodUsage("findElement", 
                Map.of("locator", by.toString(), "timeout", timeout));
            
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            WebElement element = customWait.until(ExpectedConditions.presenceOfElementLocated(by));
            
            logger.info("Element found: {}", by);
            AllureManager.addStep("Element found: " + by);
            
            return element;
            
        } catch (Exception e) {
            logger.error("Element not found: {}", by, e);
            takeScreenshot("Element not found: " + by);
            throw new RuntimeException("Element not found: " + by, e);
        }
    }
    
    /**
     * Find elements with enhanced error handling
     * 
     * @param by Locator strategy
     * @param timeout Timeout in seconds
     * @return List of WebElements
     */
    public List<WebElement> findElements(By by, int timeout) {
        try {
            cmdTracker.trackMethodUsage("findElements", 
                Map.of("locator", by.toString(), "timeout", timeout));
            
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            List<WebElement> elements = customWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
            
            logger.info("Elements found: {} count: {}", by, elements.size());
            AllureManager.addStep("Elements found: " + by + " count: " + elements.size());
            
            return elements;
            
        } catch (Exception e) {
            logger.error("Elements not found: {}", by, e);
            takeScreenshot("Elements not found: " + by);
            throw new RuntimeException("Elements not found: " + by, e);
        }
    }
    
    /**
     * Tap on element
     * 
     * @param by Locator strategy
     * @param timeout Timeout in seconds
     */
    public void tap(By by, int timeout) {
        try {
            cmdTracker.trackMethodUsage("tap", Map.of("locator", by.toString(), "timeout", timeout));
            
            WebElement element = findElement(by, timeout);
            element.click();
            
            logger.info("Tapped element: {}", by);
            AllureManager.addStep("Tapped element: " + by);
            
        } catch (Exception e) {
            logger.error("Tap failed on element: {}", by, e);
            takeScreenshot("Tap failed: " + by);
            throw new RuntimeException("Tap failed on: " + by, e);
        }
    }
    
    /**
     * Type text in element
     * 
     * @param by Locator strategy
     * @param text Text to type
     * @param clearFirst Whether to clear field first
     * @param timeout Timeout in seconds
     */
    public void type(By by, String text, boolean clearFirst, int timeout) {
        try {
            cmdTracker.trackMethodUsage("type", 
                Map.of("locator", by.toString(), "text", text, "clearFirst", clearFirst));
            
            WebElement element = findElement(by, timeout);
            
            if (clearFirst) {
                element.clear();
            }
            
            element.sendKeys(text);
            
            logger.info("Typed text in element: {}", by);
            AllureManager.addStep("Typed text in: " + by);
            
        } catch (Exception e) {
            logger.error("Type failed on element: {}", by, e);
            takeScreenshot("Type failed: " + by);
            throw new RuntimeException("Type failed on: " + by, e);
        }
    }
    
    /**
     * Get text from element
     * 
     * @param by Locator strategy
     * @param timeout Timeout in seconds
     * @return Element text
     */
    public String getText(By by, int timeout) {
        try {
            cmdTracker.trackMethodUsage("getText", 
                Map.of("locator", by.toString(), "timeout", timeout));
            
            WebElement element = findElement(by, timeout);
            String text = element.getText();
            
            logger.info("Retrieved text from element: {}", by);
            AllureManager.addStep("Retrieved text from: " + by);
            
            return text;
            
        } catch (Exception e) {
            logger.error("Get text failed on element: {}", by, e);
            takeScreenshot("Get text failed: " + by);
            throw new RuntimeException("Get text failed on: " + by, e);
        }
    }
    
    /**
     * Swipe gesture
     * 
     * @param direction Swipe direction
     * @param distance Swipe distance (0.1 to 0.9)
     * @param duration Swipe duration in milliseconds
     */
    public void swipe(SwipeDirection direction, double distance, int duration) {
        try {
            cmdTracker.trackMethodUsage("swipe", 
                Map.of("direction", direction, "distance", distance, "duration", duration));
            
            Dimension screenSize = driver.manage().window().getSize();
            int startX, startY, endX, endY;
            
            switch (direction) {
                case UP:
                    startX = screenSize.width / 2;
                    startY = (int) (screenSize.height * (1 - distance));
                    endX = startX;
                    endY = (int) (screenSize.height * distance);
                    break;
                case DOWN:
                    startX = screenSize.width / 2;
                    startY = (int) (screenSize.height * distance);
                    endX = startX;
                    endY = (int) (screenSize.height * (1 - distance));
                    break;
                case LEFT:
                    startX = (int) (screenSize.width * (1 - distance));
                    startY = screenSize.height / 2;
                    endX = (int) (screenSize.width * distance);
                    endY = startY;
                    break;
                case RIGHT:
                    startX = (int) (screenSize.width * distance);
                    startY = screenSize.height / 2;
                    endX = (int) (screenSize.width * (1 - distance));
                    endY = startY;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid swipe direction: " + direction);
            }
            
            performSwipe(startX, startY, endX, endY, duration);
            
            logger.info("Performed swipe: {} with distance: {}", direction, distance);
            AllureManager.addStep("Swipe performed: " + direction);
            
        } catch (Exception e) {
            logger.error("Swipe failed", e);
            takeScreenshot("Swipe failed");
            throw new RuntimeException("Swipe failed", e);
        }
    }
    
    /**
     * Scroll to element
     * 
     * @param by Locator strategy
     * @param maxScrolls Maximum scroll attempts
     * @param direction Scroll direction
     */
    public void scrollToElement(By by, int maxScrolls, SwipeDirection direction) {
        try {
            cmdTracker.trackMethodUsage("scrollToElement", 
                Map.of("locator", by.toString(), "maxScrolls", maxScrolls, "direction", direction));
            
            for (int i = 0; i < maxScrolls; i++) {
                try {
                    WebElement element = driver.findElement(by);
                    if (element.isDisplayed()) {
                        logger.info("Element found after {} scrolls: {}", i, by);
                        AllureManager.addStep("Element found after scrolling: " + by);
                        return;
                    }
                } catch (Exception ignored) {
                    // Element not found, continue scrolling
                }
                
                swipe(direction, 0.7, 1000);
                Thread.sleep(500);
            }
            
            throw new RuntimeException("Element not found after " + maxScrolls + " scrolls: " + by);
            
        } catch (Exception e) {
            logger.error("Scroll to element failed: {}", by, e);
            takeScreenshot("Scroll to element failed");
            throw new RuntimeException("Scroll to element failed: " + by, e);
        }
    }
    
    /**
     * Pinch zoom gesture
     * 
     * @param scale Zoom scale (0.5 for zoom out, 2.0 for zoom in)
     * @param centerX Center X coordinate
     * @param centerY Center Y coordinate
     */
    public void pinchZoom(double scale, int centerX, int centerY) {
        try {
            cmdTracker.trackMethodUsage("pinchZoom", 
                Map.of("scale", scale, "centerX", centerX, "centerY", centerY));
            
            // Calculate finger positions
            int distance = 100;
            int finger1StartX = centerX - distance;
            int finger1StartY = centerY;
            int finger2StartX = centerX + distance;
            int finger2StartY = centerY;
            
            int finger1EndX = (int) (centerX - distance * scale);
            int finger1EndY = centerY;
            int finger2EndX = (int) (centerX + distance * scale);
            int finger2EndY = centerY;
            
            // Create pointer inputs
            PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
            PointerInput finger2 = new PointerInput(PointerInput.Kind.TOUCH, "finger2");
            
            // Create sequences
            Sequence finger1Sequence = new Sequence(finger1, 1);
            Sequence finger2Sequence = new Sequence(finger2, 1);
            
            finger1Sequence.addAction(finger1.createPointerMove(Duration.ZERO, 
                PointerInput.Origin.viewport(), finger1StartX, finger1StartY));
            finger1Sequence.addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            finger1Sequence.addAction(finger1.createPointerMove(Duration.ofMillis(1000), 
                PointerInput.Origin.viewport(), finger1EndX, finger1EndY));
            finger1Sequence.addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            
            finger2Sequence.addAction(finger2.createPointerMove(Duration.ZERO, 
                PointerInput.Origin.viewport(), finger2StartX, finger2StartY));
            finger2Sequence.addAction(finger2.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            finger2Sequence.addAction(finger2.createPointerMove(Duration.ofMillis(1000), 
                PointerInput.Origin.viewport(), finger2EndX, finger2EndY));
            finger2Sequence.addAction(finger2.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            
            driver.perform(Arrays.asList(finger1Sequence, finger2Sequence));
            
            logger.info("Performed pinch zoom with scale: {}", scale);
            AllureManager.addStep("Pinch zoom performed: " + scale);
            
        } catch (Exception e) {
            logger.error("Pinch zoom failed", e);
            takeScreenshot("Pinch zoom failed");
            throw new RuntimeException("Pinch zoom failed", e);
        }
    }
    
    /**
     * Long press gesture
     * 
     * @param by Locator strategy
     * @param duration Press duration in milliseconds
     * @param timeout Timeout in seconds
     */
    public void longPress(By by, int duration, int timeout) {
        try {
            cmdTracker.trackMethodUsage("longPress", 
                Map.of("locator", by.toString(), "duration", duration, "timeout", timeout));
            
            WebElement element = findElement(by, timeout);
            Point location = element.getLocation();
            Dimension size = element.getSize();
            
            int centerX = location.x + size.width / 2;
            int centerY = location.y + size.height / 2;
            
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence sequence = new Sequence(finger, 1);
            
            sequence.addAction(finger.createPointerMove(Duration.ZERO, 
                PointerInput.Origin.viewport(), centerX, centerY));
            sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            sequence.addAction(finger.createPointerMove(Duration.ofMillis(duration), 
                PointerInput.Origin.viewport(), centerX, centerY));
            sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            
            driver.perform(Arrays.asList(sequence));
            
            logger.info("Performed long press on element: {}", by);
            AllureManager.addStep("Long press performed: " + by);
            
        } catch (Exception e) {
            logger.error("Long press failed on element: {}", by, e);
            takeScreenshot("Long press failed");
            throw new RuntimeException("Long press failed on: " + by, e);
        }
    }
    
    /**
     * Hide mobile keyboard
     */
    public void hideKeyboard() {
        try {
            cmdTracker.trackMethodUsage("hideKeyboard", Map.of());
            
            if (driver instanceof AndroidDriver) {
                ((AndroidDriver) driver).hideKeyboard();
            } else if (driver instanceof IOSDriver) {
                // For iOS, tap outside or use specific gesture
                Dimension screenSize = driver.manage().window().getSize();
                int x = screenSize.width / 2;
                int y = screenSize.height - 50;
                
                PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
                Sequence sequence = new Sequence(finger, 1);
                sequence.addAction(finger.createPointerMove(Duration.ZERO, 
                    PointerInput.Origin.viewport(), x, y));
                sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
                sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
                
                driver.perform(Arrays.asList(sequence));
            }
            
            logger.info("Keyboard hidden");
            AllureManager.addStep("Keyboard hidden");
            
        } catch (Exception e) {
            logger.warn("Failed to hide keyboard", e);
        }
    }
    
    /**
     * Take screenshot for mobile
     * 
     * @param description Screenshot description
     * @return Screenshot path
     */
    public String takeScreenshot(String description) {
        try {
            String screenshotPath = screenshotUtils.captureMobileScreenshot(driver, description);
            
            logger.info("Mobile screenshot captured: {}", description);
            AllureManager.addStep("Screenshot captured: " + description);
            
            return screenshotPath;
            
        } catch (Exception e) {
            logger.error("Mobile screenshot capture failed", e);
            return null;
        }
    }
    
    /**
     * Perform swipe with coordinates
     */
    private void performSwipe(int startX, int startY, int endX, int endY, int duration) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence sequence = new Sequence(finger, 1);
        
        sequence.addAction(finger.createPointerMove(Duration.ZERO, 
            PointerInput.Origin.viewport(), startX, startY));
        sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        sequence.addAction(finger.createPointerMove(Duration.ofMillis(duration), 
            PointerInput.Origin.viewport(), endX, endY));
        sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        
        driver.perform(Arrays.asList(sequence));
    }
    
    /**
     * Wait for element to be visible
     * 
     * @param by Locator strategy
     * @param timeout Timeout in seconds
     * @return WebElement
     */
    public WebElement waitForElementVisible(By by, int timeout) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            return customWait.until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (Exception e) {
            takeScreenshot("Element not visible: " + by);
            throw new RuntimeException("Element not visible: " + by, e);
        }
    }
    
    /**
     * Wait for element to be clickable
     * 
     * @param by Locator strategy
     * @param timeout Timeout in seconds
     * @return WebElement
     */
    public WebElement waitForElementClickable(By by, int timeout) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            return customWait.until(ExpectedConditions.elementToBeClickable(by));
        } catch (Exception e) {
            takeScreenshot("Element not clickable: " + by);
            throw new RuntimeException("Element not clickable: " + by, e);
        }
    }
    
    /**
     * Stop Appium server
     */
    public void stopAppiumServer() {
        try {
            if (appiumService != null && appiumService.isRunning()) {
                appiumService.stop();
                logger.info("Appium server stopped");
                AllureManager.addStep("Appium server stopped");
            }
        } catch (Exception e) {
            logger.error("Failed to stop Appium server", e);
        }
    }
    
    /**
     * Cleanup mobile resources
     */
    public void cleanup() {
        try {
            if (driver != null) {
                driver.quit();
            }
            stopAppiumServer();
            
            logger.info("Mobile cleanup completed");
            
        } catch (Exception e) {
            logger.error("Mobile cleanup failed", e);
        }
    }
    
    /**
     * Get mobile driver
     * 
     * @return AppiumDriver instance
     */
    public AppiumDriver getDriver() {
        return driver;
    }
    
    /**
     * Get command line usage statistics
     * 
     * @return Usage statistics
     */
    public static Map<String, Object> getUsageStatistics() {
        return cmdTracker.getUsageStatistics();
    }
}
