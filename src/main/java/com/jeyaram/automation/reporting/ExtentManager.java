package com.jeyaram.automation.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Extent Report Manager for comprehensive HTML reporting
 * 
 * @author Jeyaram K
 * @version 1.0.0
 * @since 2025-01-01
 */
public class ExtentManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ExtentManager.class);
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static final String REPORTS_DIR = "test-results/extent-reports";
    
    static {
        createReportsDirectory();
    }
    
    /**
     * Initialize Extent Reports
     */
    public static synchronized void initReports() {
        if (extent == null) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String reportPath = REPORTS_DIR + "/ExtentReport_" + timestamp + ".html";
            
            ExtentSparkReporter htmlReporter = new ExtentSparkReporter(reportPath);
            configureHtmlReporter(htmlReporter);
            
            extent = new ExtentReports();
            extent.attachReporter(htmlReporter);
            
            setSystemInfo();
            
            logger.info("Extent Reports initialized: {}", reportPath);
        }
    }
    
    /**
     * Create a new test
     * 
     * @param testName Test name
     * @param description Test description
     */
    public static void createTest(String testName, String description) {
        ExtentTest extentTest = extent.createTest(testName, description);
        test.set(extentTest);
        logger.debug("Created Extent test: {}", testName);
    }
    
    /**
     * Create a new test with category
     * 
     * @param testName Test name
     * @param description Test description
     * @param category Test category
     */
    public static void createTest(String testName, String description, String category) {
        ExtentTest extentTest = extent.createTest(testName, description);
        extentTest.assignCategory(category);
        test.set(extentTest);
        logger.debug("Created Extent test: {} with category: {}", testName, category);
    }
    
    /**
     * Get current test instance
     * 
     * @return Current ExtentTest instance
     */
    public static ExtentTest getTest() {
        return test.get();
    }
    
    /**
     * Log info message
     * 
     * @param message Info message
     */
    public static void logInfo(String message) {
        if (test.get() != null) {
            test.get().log(Status.INFO, message);
            logger.debug("Logged info: {}", message);
        }
    }
    
    /**
     * Log pass message
     * 
     * @param message Pass message
     */
    public static void logPass(String message) {
        if (test.get() != null) {
            test.get().log(Status.PASS, message);
            logger.debug("Logged pass: {}", message);
        }
    }
    
    /**
     * Log fail message
     * 
     * @param message Fail message
     */
    public static void logFail(String message) {
        if (test.get() != null) {
            test.get().log(Status.FAIL, message);
            logger.debug("Logged fail: {}", message);
        }
    }
    
    /**
     * Log skip message
     * 
     * @param message Skip message
     */
    public static void logSkip(String message) {
        if (test.get() != null) {
            test.get().log(Status.SKIP, message);
            logger.debug("Logged skip: {}", message);
        }
    }
    
    /**
     * Log warning message
     * 
     * @param message Warning message
     */
    public static void logWarning(String message) {
        if (test.get() != null) {
            test.get().log(Status.WARNING, message);
            logger.debug("Logged warning: {}", message);
        }
    }
    
    /**
     * Add screenshot to report
     * 
     * @param screenshotPath Path to screenshot
     * @param description Screenshot description
     */
    public static void addScreenshot(String screenshotPath, String description) {
        if (test.get() != null && screenshotPath != null) {
            try {
                test.get().addScreenCaptureFromPath(screenshotPath, description);
                logger.debug("Added screenshot to Extent report: {}", description);
            } catch (Exception e) {
                logger.error("Failed to add screenshot to Extent report", e);
            }
        }
    }
    
    /**
     * Add base64 screenshot to report
     * 
     * @param base64Screenshot Base64 encoded screenshot
     * @param description Screenshot description
     */
    public static void addBase64Screenshot(String base64Screenshot, String description) {
        if (test.get() != null && base64Screenshot != null) {
            test.get().addScreenCaptureFromBase64String(base64Screenshot, description);
            logger.debug("Added base64 screenshot to Extent report: {}", description);
        }
    }
    
    /**
     * Assign category to current test
     * 
     * @param category Category name
     */
    public static void assignCategory(String category) {
        if (test.get() != null) {
            test.get().assignCategory(category);
            logger.debug("Assigned category to test: {}", category);
        }
    }
    
    /**
     * Assign author to current test
     * 
     * @param author Author name
     */
    public static void assignAuthor(String author) {
        if (test.get() != null) {
            test.get().assignAuthor(author);
            logger.debug("Assigned author to test: {}", author);
        }
    }
    
    /**
     * Add device information to current test
     * 
     * @param device Device name
     */
    public static void assignDevice(String device) {
        if (test.get() != null) {
            test.get().assignDevice(device);
            logger.debug("Assigned device to test: {}", device);
        }
    }
    
    /**
     * Create child test (for data-driven tests)
     * 
     * @param childTestName Child test name
     * @return Child ExtentTest instance
     */
    public static ExtentTest createChildTest(String childTestName) {
        if (test.get() != null) {
            ExtentTest childTest = test.get().createNode(childTestName);
            logger.debug("Created child test: {}", childTestName);
            return childTest;
        }
        return null;
    }
    
    /**
     * Flush reports and generate final report
     */
    public static synchronized void flushReports() {
        if (extent != null) {
            extent.flush();
            logger.info("Extent Reports flushed successfully");
        }
    }
    
    /**
     * Configure HTML reporter
     */
    private static void configureHtmlReporter(ExtentSparkReporter htmlReporter) {
        htmlReporter.config().setTheme(Theme.STANDARD);
        htmlReporter.config().setDocumentTitle("Playwright Java Hybrid Automation Report");
        htmlReporter.config().setReportName("Test Execution Report");
        htmlReporter.config().setEncoding("utf-8");
        
        // Custom CSS and JavaScript can be added here
        String customCSS = """
            .test-content { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
            .badge-primary { background-color: #007bff; }
            .badge-success { background-color: #28a745; }
            .badge-danger { background-color: #dc3545; }
            .badge-warning { background-color: #ffc107; color: #212529; }
            """;
        
        try {
            htmlReporter.config().setCss(customCSS);
        } catch (Exception e) {
            logger.warn("Failed to set custom CSS for Extent report", e);
        }
    }
    
    /**
     * Set system information
     */
    private static void setSystemInfo() {
        extent.setSystemInfo("Framework", "Playwright Java Hybrid Automation");
        extent.setSystemInfo("Author", "Jeyaram K");
        extent.setSystemInfo("Version", "1.0.0");
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("User", System.getProperty("user.name"));
        extent.setSystemInfo("Environment", System.getProperty("environment", "dev"));
        extent.setSystemInfo("Execution Time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
    
    /**
     * Create reports directory
     */
    private static void createReportsDirectory() {
        try {
            Path reportsPath = Paths.get(REPORTS_DIR);
            if (!Files.exists(reportsPath)) {
                Files.createDirectories(reportsPath);
            }
        } catch (IOException e) {
            logger.error("Failed to create reports directory", e);
        }
    }
    
    /**
     * Get reports directory path
     * 
     * @return Reports directory path
     */
    public static String getReportsDirectory() {
        return REPORTS_DIR;
    }
    
    /**
     * Clean up thread local
     */
    public static void removeTest() {
        test.remove();
    }
}
