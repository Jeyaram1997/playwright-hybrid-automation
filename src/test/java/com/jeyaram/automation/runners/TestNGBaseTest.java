package com.jeyaram.automation.runners;

import com.jeyaram.automation.base.PlaywrightBase;
import com.jeyaram.automation.config.ConfigManager;
import com.jeyaram.automation.reporting.AllureManager;
import com.jeyaram.automation.reporting.ExtentManager;
import com.jeyaram.automation.utils.EmailUtils;
import com.jeyaram.automation.utils.JiraUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * TestNG Base Test Class with comprehensive reporting and failure handling
 * Provides setup/teardown, screenshot capture, and automated reporting
 * 
 * @author Jeyaram K
 * @version 1.0.0
 * @since 2025-01-01
 */
public class TestNGBaseTest extends PlaywrightBase implements ITestListener {
    
    private static final Logger logger = LoggerFactory.getLogger(TestNGBaseTest.class);
    private static final List<String> jiraBugUrls = new ArrayList<>();
    
    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        try {
            logger.info("Starting TestNG test suite execution");
            
            // Initialize reports
            ExtentManager.initReports();
            
            // Add environment information to Allure
            ConfigManager config = ConfigManager.getInstance();
            AllureManager.addEnvironmentInfo("Environment", config.getEnvironment());
            AllureManager.addEnvironmentInfo("Browser", config.getEnvironmentConfig().getBrowser());
            AllureManager.addEnvironmentInfo("Base URL", config.getEnvironmentConfig().getBaseUrl());
            AllureManager.addEnvironmentInfo("Framework", "Playwright Java Hybrid Automation");
            AllureManager.addEnvironmentInfo("Author", "Jeyaram K");
            
            logger.info("Test suite setup completed successfully");
            
        } catch (Exception e) {
            logger.error("Test suite setup failed", e);
            throw new RuntimeException("Test suite setup failed", e);
        }
    }
    
    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        try {
            logger.info("Setting up test class: {}", this.getClass().getSimpleName());
            
            // Initialize browser
            String browser = configManager.getEnvironmentConfig().getBrowser();
            boolean headless = configManager.getEnvironmentConfig().isHeadless();
            
            initializeBrowser(browser, headless, 0);
            
            logger.info("Test class setup completed: {}", this.getClass().getSimpleName());
            
        } catch (Exception e) {
            logger.error("Test class setup failed", e);
            throw new RuntimeException("Test class setup failed", e);
        }
    }
    
    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(java.lang.reflect.Method method) {
        try {
            String testName = method.getName();
            String className = this.getClass().getSimpleName();
            
            logger.info("Starting test method: {}.{}", className, testName);
            
            // Create Extent test
            Test testAnnotation = method.getAnnotation(Test.class);
            String description = testAnnotation != null ? testAnnotation.description() : "";
            ExtentManager.createTest(testName, description);
            ExtentManager.assignAuthor("Jeyaram K");
            ExtentManager.assignCategory(className);
            
            // Add Allure annotations
            AllureManager.addStep("Test started: " + testName);
            
        } catch (Exception e) {
            logger.error("Test method setup failed", e);
        }
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result) {
        try {
            String testName = result.getMethod().getMethodName();
            String className = result.getTestClass().getRealClass().getSimpleName();
            
            if (result.getStatus() == ITestResult.FAILURE) {
                handleTestFailure(result, testName, className);
            } else if (result.getStatus() == ITestResult.SUCCESS) {
                handleTestSuccess(testName);
            } else if (result.getStatus() == ITestResult.SKIP) {
                handleTestSkip(testName, result.getThrowable());
            }
            
            // Clean up Extent test
            ExtentManager.removeTest();
            
            logger.info("Test method completed: {}.{} with status: {}", 
                className, testName, getStatusString(result.getStatus()));
            
        } catch (Exception e) {
            logger.error("Test method teardown failed", e);
        }
    }
    
    @AfterClass(alwaysRun = true)
    public void afterClass() {
        try {
            logger.info("Tearing down test class: {}", this.getClass().getSimpleName());
            
            // Cleanup browser resources
            cleanup();
            
            logger.info("Test class teardown completed: {}", this.getClass().getSimpleName());
            
        } catch (Exception e) {
            logger.error("Test class teardown failed", e);
        }
    }
    
    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        try {
            logger.info("Completing TestNG test suite execution");
            
            // Flush reports
            ExtentManager.flushReports();
            
            // Send email notifications
            sendEmailNotifications();
            
            // Print usage statistics
            PlaywrightBase.printHelp();
            
            logger.info("Test suite teardown completed successfully");
            
        } catch (Exception e) {
            logger.error("Test suite teardown failed", e);
        }
    }
    
    /**
     * Handle test failure
     */
    private void handleTestFailure(ITestResult result, String testName, String className) {
        try {
            Throwable throwable = result.getThrowable();
            String errorMessage = throwable != null ? throwable.getMessage() : "Unknown error";
            
            logger.error("Test failed: {}.{} - {}", className, testName, errorMessage);
            
            // Capture screenshot
            String screenshotPath = takeScreenshot("FAILURE_" + testName);
            
            // Log failure in reports
            ExtentManager.logFail("Test failed: " + errorMessage);
            if (screenshotPath != null) {
                ExtentManager.addScreenshot(screenshotPath, "Failure Screenshot");
            }
            
            AllureManager.addStep("Test failed: " + errorMessage, io.qameta.allure.model.Status.FAILED);
            
            // Create Jira bug if enabled
            createJiraBug(testName, errorMessage, screenshotPath);
            
        } catch (Exception e) {
            logger.error("Failed to handle test failure", e);
        }
    }
    
    /**
     * Handle test success
     */
    private void handleTestSuccess(String testName) {
        try {
            logger.info("Test passed: {}", testName);
            
            ExtentManager.logPass("Test passed successfully");
            AllureManager.addStep("Test passed successfully");
            
        } catch (Exception e) {
            logger.error("Failed to handle test success", e);
        }
    }
    
    /**
     * Handle test skip
     */
    private void handleTestSkip(String testName, Throwable throwable) {
        try {
            String reason = throwable != null ? throwable.getMessage() : "Unknown reason";
            logger.warn("Test skipped: {} - {}", testName, reason);
            
            ExtentManager.logSkip("Test skipped: " + reason);
            AllureManager.addStep("Test skipped: " + reason, io.qameta.allure.model.Status.SKIPPED);
            
        } catch (Exception e) {
            logger.error("Failed to handle test skip", e);
        }
    }
    
    /**
     * Create Jira bug for test failure
     */
    private void createJiraBug(String testName, String errorMessage, String screenshotPath) {
        try {
            boolean jiraEnabled = configManager.getBooleanProperty("jira.enabled", false);
            if (jiraEnabled) {
                String environment = configManager.getProperty("environment", "development");
                String priority = "High"; // Default priority for test failures
                
                String jiraBugUrl = JiraUtils.createBugForTestFailure(
                    testName, 
                    errorMessage, 
                    getStackTrace(), 
                    environment, 
                    "UI", 
                    priority
                );
                
                if (jiraBugUrl != null) {
                    logger.info("JIRA bug created: {}", jiraBugUrl);
                    ExtentManager.logInfo("JIRA bug created: " + jiraBugUrl);
                    AllureManager.addStep("JIRA Bug Created: " + jiraBugUrl);
                    
                    // Store bug URL for email notification
                    storeBugUrl(jiraBugUrl);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to create JIRA bug", e);
        }
    }
    
    /**
     * Send email notifications with reports and JIRA bug links
     */
    private void sendEmailNotifications() {
        try {
            boolean emailEnabled = configManager.getBooleanProperty("email.enabled", false);
            if (emailEnabled) {
                EmailUtils emailUtils = new EmailUtils();
                
                // Get all report paths
                List<String> reportPaths = new ArrayList<>();
                reportPaths.add("target/allure-results");
                reportPaths.add("target/extent-report.html");
                reportPaths.add("target/reports");
                
                // Send email with reports and JIRA bug URLs
                emailUtils.sendTestReport(
                    "Test Execution Completed - " + configManager.getProperty("environment", "development"),
                    "Please find attached test execution reports. JIRA bugs have been created for failed tests.",
                    reportPaths,
                    getJiraBugUrls()
                );
                
                logger.info("Email notification sent successfully with {} JIRA bug links", getJiraBugUrls().size());
            }
        } catch (Exception e) {
            logger.error("Failed to send email notifications", e);
        }
    }
    
    /**
     * Get status string from TestNG status code
     */
    private String getStatusString(int status) {
        switch (status) {
            case ITestResult.SUCCESS:
                return "PASSED";
            case ITestResult.FAILURE:
                return "FAILED";
            case ITestResult.SKIP:
                return "SKIPPED";
            default:
                return "UNKNOWN";
        }
    }
    
    /**
     * Get stack trace as string
     */
    private String getStackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
    
    /**
     * Store JIRA bug URL for email notification
     */
    private void storeBugUrl(String bugUrl) {
        if (bugUrl != null && !jiraBugUrls.contains(bugUrl)) {
            jiraBugUrls.add(bugUrl);
        }
    }
    
    /**
     * Get all stored JIRA bug URLs
     */
    public static List<String> getJiraBugUrls() {
        return new ArrayList<>(jiraBugUrls);
    }
    
    // TestNG Listener methods
    @Override
    public void onTestStart(ITestResult result) {
        logger.debug("TestNG listener: Test started - {}", result.getMethod().getMethodName());
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        logger.debug("TestNG listener: Test success - {}", result.getMethod().getMethodName());
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        logger.debug("TestNG listener: Test failure - {}", result.getMethod().getMethodName());
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        logger.debug("TestNG listener: Test skipped - {}", result.getMethod().getMethodName());
    }
}
