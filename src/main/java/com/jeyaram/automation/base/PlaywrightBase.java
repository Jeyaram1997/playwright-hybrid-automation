package com.jeyaram.automation.base;

import com.jeyaram.automation.config.ConfigManager;
import com.jeyaram.automation.config.EnvironmentConfig;
import com.jeyaram.automation.reporting.AllureManager;
import com.jeyaram.automation.reporting.ExtentManager;
import com.jeyaram.automation.utils.*;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Base class for Playwright automation providing comprehensive reusable methods
 * Supports UI, API, Mobile, and Performance testing capabilities
 * 
 * @author Jeyaram K
 * @version 1.0.1
 * @since 2025-01-01
 */
public class PlaywrightBase {
    
    private static final Logger logger = LoggerFactory.getLogger(PlaywrightBase.class);
    
    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext context;
    protected static Page page;
    protected static APIRequestContext apiContext;
    
    protected final ConfigManager configManager = ConfigManager.getInstance();
    protected final EnvironmentConfig envConfig = configManager.getEnvironmentConfig();
    protected final ScreenshotUtils screenshotUtils = new ScreenshotUtils();
    protected final WaitUtils waitUtils = new WaitUtils();
    protected final DataUtils dataUtils = new DataUtils();
    protected final SecurityUtils securityUtils = new SecurityUtils();
    
    // Command line usage tracker
    private static final CommandLineTracker cmdTracker = new CommandLineTracker();
    
    /**
     * Initialize Playwright browser instance
     * 
     * @param browserType Browser type (chromium, firefox, webkit)
     * @param headless Whether to run in headless mode
     * @param slowMo Slow motion delay for debugging
     */
    public void initializeBrowser(String browserType, boolean headless, int slowMo) {
        try {
            cmdTracker.trackMethodUsage("initializeBrowser", 
                Map.of("browserType", browserType, "headless", headless, "slowMo", slowMo));
            
            playwright = Playwright.create();
            
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(slowMo)
                .setArgs(List.of("--disable-web-security", "--allow-running-insecure-content"));
            
            switch (browserType.toLowerCase()) {
                case "chromium":
                    browser = playwright.chromium().launch(launchOptions);
                    break;
                case "firefox":
                    browser = playwright.firefox().launch(launchOptions);
                    break;
                case "webkit":
                    browser = playwright.webkit().launch(launchOptions);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported browser type: " + browserType);
            }
            
            // Create browser context with additional options
            Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(envConfig.getViewportWidth(), envConfig.getViewportHeight())
                .setLocale(envConfig.getLocale())
                .setTimezoneId(envConfig.getTimezone())
                .setPermissions(List.of("geolocation", "notifications"))
                .setRecordVideoDir(Paths.get("test-results/videos"));
                
            context = browser.newContext(contextOptions);
            page = context.newPage();
            
            // Initialize API context
            apiContext = playwright.request().newContext();
            
            logger.info("Browser initialized successfully: {}", browserType);
            AllureManager.addStep("Browser initialized: " + browserType);
            
        } catch (Exception e) {
            logger.error("Failed to initialize browser: {}", e.getMessage(), e);
            throw new RuntimeException("Browser initialization failed", e);
        }
    }
    
    /**
     * Initialize mobile browser context for mobile testing
     * 
     * @param deviceName Device name (iPhone 13, Pixel 5, etc.)
     * @param orientation Device orientation (portrait/landscape)
     */
    public void initializeMobileBrowser(String deviceName, String orientation) {
        try {
            cmdTracker.trackMethodUsage("initializeMobileBrowser", 
                Map.of("deviceName", deviceName, "orientation", orientation));
            
            playwright = Playwright.create();
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            
            // Create context with device emulation
            Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();
            
            // Set common mobile device properties
            switch (deviceName.toLowerCase()) {
                case "iphone 12":
                case "iphone12":
                    contextOptions.setViewportSize(390, 844);
                    contextOptions.setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1");
                    contextOptions.setDeviceScaleFactor(3.0);
                    contextOptions.setIsMobile(true);
                    contextOptions.setHasTouch(true);
                    break;
                case "pixel 5":
                case "pixel5":
                    contextOptions.setViewportSize(393, 851);
                    contextOptions.setUserAgent("Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.91 Mobile Safari/537.36");
                    contextOptions.setDeviceScaleFactor(2.75);
                    contextOptions.setIsMobile(true);
                    contextOptions.setHasTouch(true);
                    break;
                case "ipad":
                    contextOptions.setViewportSize(820, 1180);
                    contextOptions.setUserAgent("Mozilla/5.0 (iPad; CPU OS 14_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1");
                    contextOptions.setDeviceScaleFactor(2.0);
                    contextOptions.setIsMobile(true);
                    contextOptions.setHasTouch(true);
                    break;
                default:
                    // Default mobile settings
                    contextOptions.setViewportSize(375, 667);
                    contextOptions.setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1");
                    contextOptions.setDeviceScaleFactor(2.0);
                    contextOptions.setIsMobile(true);
                    contextOptions.setHasTouch(true);
                    break;
            }
            
            // Set orientation
            if ("landscape".equalsIgnoreCase(orientation)) {
                // For landscape, we need to swap width and height manually
                switch (deviceName.toLowerCase()) {
                    case "iphone 12":
                    case "iphone12":
                        contextOptions.setViewportSize(844, 390);
                        break;
                    case "pixel 5":
                    case "pixel5":
                        contextOptions.setViewportSize(851, 393);
                        break;
                    case "ipad":
                        contextOptions.setViewportSize(1180, 820);
                        break;
                    default:
                        contextOptions.setViewportSize(667, 375);
                        break;
                }
            }
            
            context = browser.newContext(contextOptions);
            page = context.newPage();
            
            logger.info("Mobile browser initialized: {} - {}", deviceName, orientation);
            AllureManager.addStep("Mobile browser initialized: " + deviceName);
            
        } catch (Exception e) {
            logger.error("Failed to initialize mobile browser: {}", e.getMessage(), e);
            throw new RuntimeException("Mobile browser initialization failed", e);
        }
    }

    
    
    /**
     * Navigate to URL with enhanced error handling and reporting
     * 
     * @param url Target URL
     * @param waitForLoad Whether to wait for page load
     */
    public void navigateTo(String url, boolean waitForLoad) {
        try {
            cmdTracker.trackMethodUsage("navigateTo", Map.of("url", url, "waitForLoad", waitForLoad));
            
            page.navigate(url);
            
            if (waitForLoad) {
                page.waitForLoadState(LoadState.NETWORKIDLE);
            }
            
            // Take screenshot for reporting
            takeScreenshot("Navigation to: " + url);
            
            logger.info("Navigated to URL: {}", url);
            AllureManager.addStep("Navigated to: " + url);
            
        } catch (Exception e) {
            logger.error("Failed to navigate to URL: {}", url, e);
            takeScreenshot("Navigation failed: " + url);
            throw new RuntimeException("Navigation failed to: " + url, e);
        }
    }
    
    /**
     * Enhanced click method with multiple selector strategies
     * 
     * @param selector Element selector
     * @param timeout Timeout in milliseconds
     * @param force Whether to force click
     */
    public void click(String selector, long timeout, boolean force) {
        try {
            cmdTracker.trackMethodUsage("click", 
                Map.of("selector", selector, "timeout", timeout, "force", force));
            
            Locator element = page.locator(selector);
            element.click(new Locator.ClickOptions()
                .setTimeout(timeout)
                .setForce(force));
            
            logger.info("Clicked element: {}", selector);
            AllureManager.addStep("Clicked: " + selector);
            
        } catch (Exception e) {
            logger.error("Failed to click element: {}", selector, e);
            takeScreenshot("Click failed: " + selector);
            throw new RuntimeException("Click failed on: " + selector, e);
        }
    }
    
    /**
     * Enhanced text input with validation
     * 
     * @param selector Element selector
     * @param text Text to input
     * @param clearFirst Whether to clear field first
     * @param validate Whether to validate input
     */
    public void type(String selector, String text, boolean clearFirst, boolean validate) {
        try {
            cmdTracker.trackMethodUsage("type", 
                Map.of("selector", selector, "text", text, "clearFirst", clearFirst));
            
            Locator element = page.locator(selector);
            
            if (clearFirst) {
                element.clear();
            }
            
            element.fill(text);
            
            if (validate) {
                String actualValue = element.inputValue();
                if (!text.equals(actualValue)) {
                    throw new AssertionError("Input validation failed. Expected: " + text + ", Actual: " + actualValue);
                }
            }
            
            logger.info("Typed text in element: {}", selector);
            AllureManager.addStep("Typed text in: " + selector);
            
        } catch (Exception e) {
            logger.error("Failed to type in element: {}", selector, e);
            takeScreenshot("Type failed: " + selector);
            throw new RuntimeException("Type failed on: " + selector, e);
        }
    }
    
    /**
     * Wait for element with multiple strategies
     * 
     * @param selector Element selector
     * @param state Wait state (visible, hidden, attached, detached)
     * @param timeout Timeout in milliseconds
     */
    public void waitForElement(String selector, String state, long timeout) {
        try {
            cmdTracker.trackMethodUsage("waitForElement", 
                Map.of("selector", selector, "state", state, "timeout", timeout));
            
            WaitForSelectorState waitState = WaitForSelectorState.valueOf(state.toUpperCase());
            page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                .setState(waitState)
                .setTimeout(timeout));
            
            logger.info("Element found: {} in state: {}", selector, state);
            AllureManager.addStep("Element found: " + selector);
            
        } catch (Exception e) {
            logger.error("Element not found: {} in state: {}", selector, state, e);
            takeScreenshot("Element wait failed: " + selector);
            throw new RuntimeException("Element wait failed: " + selector, e);
        }
    }
    
    /**
     * Get text from element with enhanced error handling
     * 
     * @param selector Element selector
     * @param trim Whether to trim whitespace
     * @return Element text
     */
    public String getText(String selector, boolean trim) {
        try {
            cmdTracker.trackMethodUsage("getText", Map.of("selector", selector, "trim", trim));
            
            String text = page.locator(selector).textContent();
            if (text != null && trim) {
                text = text.trim();
            }
            
            logger.info("Retrieved text from element: {}", selector);
            AllureManager.addStep("Retrieved text from: " + selector);
            
            return text;
            
        } catch (Exception e) {
            logger.error("Failed to get text from element: {}", selector, e);
            takeScreenshot("Get text failed: " + selector);
            throw new RuntimeException("Get text failed from: " + selector, e);
        }
    }
    
    /**
     * Select dropdown option by value, text, or index
     * 
     * @param selector Dropdown selector
     * @param option Option to select
     * @param selectionType Type of selection (value, text, index)
     */
    public void selectDropdown(String selector, String option, String selectionType) {
        try {
            cmdTracker.trackMethodUsage("selectDropdown", 
                Map.of("selector", selector, "option", option, "selectionType", selectionType));
            
            Locator dropdown = page.locator(selector);
            
            switch (selectionType.toLowerCase()) {
                case "value":
                    dropdown.selectOption(new SelectOption().setValue(option));
                    break;
                case "text":
                    dropdown.selectOption(new SelectOption().setLabel(option));
                    break;
                case "index":
                    dropdown.selectOption(new SelectOption().setIndex(Integer.parseInt(option)));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid selection type: " + selectionType);
            }
            
            logger.info("Selected dropdown option: {} by {}", option, selectionType);
            AllureManager.addStep("Selected dropdown option: " + option);
            
        } catch (Exception e) {
            logger.error("Failed to select dropdown option: {}", option, e);
            takeScreenshot("Dropdown selection failed: " + selector);
            throw new RuntimeException("Dropdown selection failed: " + selector, e);
        }
    }
    
    /**
     * Drag and drop operation
     * 
     * @param sourceSelector Source element selector
     * @param targetSelector Target element selector
     */
    public void dragAndDrop(String sourceSelector, String targetSelector) {
        try {
            cmdTracker.trackMethodUsage("dragAndDrop", 
                Map.of("sourceSelector", sourceSelector, "targetSelector", targetSelector));
            
            page.dragAndDrop(sourceSelector, targetSelector);
            
            logger.info("Performed drag and drop from {} to {}", sourceSelector, targetSelector);
            AllureManager.addStep("Drag and drop performed");
            
        } catch (Exception e) {
            logger.error("Drag and drop failed", e);
            takeScreenshot("Drag and drop failed");
            throw new RuntimeException("Drag and drop failed", e);
        }
    }
    
    /**
     * Handle file upload
     * 
     * @param selector File input selector
     * @param filePath Path to file
     */
    public void uploadFile(String selector, String filePath) {
        try {
            cmdTracker.trackMethodUsage("uploadFile", 
                Map.of("selector", selector, "filePath", filePath));
            
            page.setInputFiles(selector, Paths.get(filePath));
            
            logger.info("File uploaded: {}", filePath);
            AllureManager.addStep("File uploaded: " + filePath);
            
        } catch (Exception e) {
            logger.error("File upload failed: {}", filePath, e);
            takeScreenshot("File upload failed");
            throw new RuntimeException("File upload failed: " + filePath, e);
        }
    }
    
    /**
     * Handle JavaScript alerts
     * 
     * @param action Action to perform (accept, dismiss)
     * @param text Text to enter in prompt (optional)
     */
    public void handleAlert(String action, String text) {
        try {
            cmdTracker.trackMethodUsage("handleAlert", Map.of("action", action, "text", text));
            
            page.onDialog(dialog -> {
                switch (action.toLowerCase()) {
                    case "accept":
                        if (text != null && !text.isEmpty()) {
                            dialog.accept(text);
                        } else {
                            dialog.accept();
                        }
                        break;
                    case "dismiss":
                        dialog.dismiss();
                        break;
                }
            });
            
            logger.info("Alert handled: {}", action);
            AllureManager.addStep("Alert handled: " + action);
            
        } catch (Exception e) {
            logger.error("Alert handling failed", e);
            throw new RuntimeException("Alert handling failed", e);
        }
    }
    
    /**
     * Switch to frame/iframe
     * 
     * @param frameSelector Frame selector
     */
    public void switchToFrame(String frameSelector) {
        try {
            cmdTracker.trackMethodUsage("switchToFrame", Map.of("frameSelector", frameSelector));
            
            Frame frame = page.frame(frameSelector);
            if (frame == null) {
                throw new RuntimeException("Frame not found: " + frameSelector);
            }
            
            logger.info("Switched to frame: {}", frameSelector);
            AllureManager.addStep("Switched to frame: " + frameSelector);
            
        } catch (Exception e) {
            logger.error("Frame switch failed: {}", frameSelector, e);
            throw new RuntimeException("Frame switch failed: " + frameSelector, e);
        }
    }
    
    /**
     * Execute JavaScript code
     * 
     * @param script JavaScript code
     * @param args Arguments for the script
     * @return Execution result
     */
    public Object executeJavaScript(String script, Object... args) {
        try {
            cmdTracker.trackMethodUsage("executeJavaScript", Map.of("script", script));
            
            Object result = page.evaluate(script, args);
            
            logger.info("JavaScript executed successfully");
            AllureManager.addStep("JavaScript executed");
            
            return result;
            
        } catch (Exception e) {
            logger.error("JavaScript execution failed", e);
            throw new RuntimeException("JavaScript execution failed", e);
        }
    }
    
    /**
     * Take screenshot with enhanced reporting integration
     * 
     * @param description Screenshot description
     * @return Screenshot path
     */
    public String takeScreenshot(String description) {
        try {
            String screenshotPath = screenshotUtils.captureScreenshot(page, description);
            
            // Add to Allure report
            byte[] screenshot = page.screenshot();
            Allure.addAttachment(description, new ByteArrayInputStream(screenshot));
            
            // Add to Extent report
            ExtentManager.addScreenshot(screenshotPath, description);
            
            logger.info("Screenshot captured: {}", description);
            return screenshotPath;
            
        } catch (Exception e) {
            logger.error("Screenshot capture failed", e);
            return null;
        }
    }
    
    /**
     * Scroll to element
     * 
     * @param selector Element selector
     */
    public void scrollToElement(String selector) {
        try {
            cmdTracker.trackMethodUsage("scrollToElement", Map.of("selector", selector));
            
            page.locator(selector).scrollIntoViewIfNeeded();
            
            logger.info("Scrolled to element: {}", selector);
            AllureManager.addStep("Scrolled to element: " + selector);
            
        } catch (Exception e) {
            logger.error("Scroll to element failed: {}", selector, e);
            throw new RuntimeException("Scroll to element failed: " + selector, e);
        }
    }
    
    /**
     * Get page performance metrics
     * 
     * @return Performance metrics map
     */
    public Map<String, Object> getPerformanceMetrics() {
        try {
            cmdTracker.trackMethodUsage("getPerformanceMetrics", Map.of());
            
            String script = """
                return {
                    loadTime: window.performance.timing.loadEventEnd - window.performance.timing.navigationStart,
                    domContentLoaded: window.performance.timing.domContentLoadedEventEnd - window.performance.timing.navigationStart,
                    firstPaint: window.performance.getEntriesByType('paint')[0]?.startTime || 0,
                    firstContentfulPaint: window.performance.getEntriesByType('paint')[1]?.startTime || 0,
                    largestContentfulPaint: window.performance.getEntriesByType('largest-contentful-paint')[0]?.startTime || 0
                };
                """;
            
            @SuppressWarnings("unchecked")
            Map<String, Object> metrics = (Map<String, Object>) page.evaluate(script);
            
            logger.info("Performance metrics retrieved");
            AllureManager.addStep("Performance metrics retrieved");
            
            return metrics;
            
        } catch (Exception e) {
            logger.error("Failed to get performance metrics", e);
            return Map.of();
        }
    }
    
    /**
     * Enhanced cleanup method for proper resource management
     */
    public void cleanup() {
        try {
            if (page != null) {
                page.close();
            }
            if (context != null) {
                context.close();
            }
            if (browser != null) {
                browser.close();
            }
            if (playwright != null) {
                playwright.close();
            }
            if (apiContext != null) {
                apiContext.dispose();
            }
            
            logger.info("Cleanup completed successfully");
            
        } catch (Exception e) {
            logger.error("Cleanup failed", e);
        }
    }
    
    /**
     * Get current page instance
     * 
     * @return Current page
     */
    public Page getPage() {
        return page;
    }
    
    /**
     * Get current browser context
     * 
     * @return Current browser context
     */
    public BrowserContext getContext() {
        return context;
    }
    
    /**
     * Get API request context
     * 
     * @return API request context
     */
    public APIRequestContext getApiContext() {
        return apiContext;
    }
    
    /**
     * Wait for page to fully load
     */
    public void waitForPageLoad() {
        try {
            if (page != null) {
                page.waitForLoadState(LoadState.NETWORKIDLE);
                logger.info("Page loaded successfully");
                AllureManager.addStep("Page loaded");
            }
        } catch (Exception e) {
            logger.error("Page load failed", e);
            takeScreenshot("Page load failed");
            throw new RuntimeException("Page load failed", e);
        }
    }

    /**
     * Wait for element with Locator
     * 
     * @param locator The locator to wait for
     * @param timeout Timeout in milliseconds
     */
    public void waitForElement(Locator locator, long timeout) {
        try {
            locator.waitFor(new Locator.WaitForOptions().setTimeout(timeout));
            logger.info("Element found using locator");
            AllureManager.addStep("Element found using locator");
        } catch (Exception e) {
            logger.error("Element not found using locator", e);
            takeScreenshot("Element wait failed");
            throw new RuntimeException("Element wait failed", e);
        }
    }

    /**
     * Get command line usage statistics
     * 
     * @return Usage statistics
     */
    public static Map<String, Object> getUsageStatistics() {
        return cmdTracker.getUsageStatistics();
    }
    
    /**
     * Print command line help for available methods
     */
    public static void printHelp() {
        cmdTracker.printHelp();
    }
}
