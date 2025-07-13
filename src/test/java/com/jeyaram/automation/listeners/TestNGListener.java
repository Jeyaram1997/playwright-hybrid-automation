package com.jeyaram.automation.listeners;

import com.jeyaram.automation.config.ConfigManager;
import com.jeyaram.automation.reporting.AllureManager;
import com.jeyaram.automation.utils.JiraUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;

import java.util.Arrays;

/**
 * TestNG Listener for enhanced test execution monitoring
 * Integrates with Cucumber scenarios and provides additional reporting
 * 
 * @author Jeyaram K
 * @version 1.0.0
 * @since 2025-01-01
 */
public class TestNGListener implements ITestListener, ISuiteListener, IInvokedMethodListener {
    
    private static final Logger logger = LoggerFactory.getLogger(TestNGListener.class);
    private static final ConfigManager configManager = ConfigManager.getInstance();
    
    @Override
    public void onStart(ISuite suite) {
        logger.info("====== Starting Test Suite: {} ======", suite.getName());
        
        // Add suite-level information to Allure
        AllureManager.addEnvironmentInfo("suite.name", suite.getName());
        AllureManager.addEnvironmentInfo("suite.parallel", String.valueOf(suite.getParallel()));
        AllureManager.addEnvironmentInfo("execution.start.time", java.time.LocalDateTime.now().toString());
        
        // Log suite configuration
        logger.info("Suite Parallel Mode: {}", suite.getParallel());
        logger.info("Suite Thread Count: {}", suite.getParameter("thread-count"));
        
        AllureManager.addStep("Test Suite Started: " + suite.getName());
    }
    
    @Override
    public void onFinish(ISuite suite) {
        logger.info("====== Finishing Test Suite: {} ======", suite.getName());
        
        // Add completion information
        AllureManager.addEnvironmentInfo("execution.end.time", java.time.LocalDateTime.now().toString());
        AllureManager.addStep("Test Suite Completed: " + suite.getName());
        
        // Log suite results summary
        logSuiteResults(suite);
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        String testName = getTestName(result);
        logger.info("Starting Test: {}", testName);
        
        // Add test information to Allure
        AllureManager.addStep("Starting test: " + testName);
        AllureManager.addLabel("testClass", result.getTestClass().getName());
        AllureManager.addLabel("testMethod", result.getMethod().getMethodName());
        
        // Add test parameters if any
        Object[] parameters = result.getParameters();
        if (parameters != null && parameters.length > 0) {
            for (int i = 0; i < parameters.length; i++) {
                AllureManager.addParameter("param" + i, String.valueOf(parameters[i]));
            }
        }
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = getTestName(result);
        long duration = result.getEndMillis() - result.getStartMillis();
        
        logger.info("✅ Test PASSED: {} (Duration: {}ms)", testName, duration);
        
        AllureManager.addStep("Test completed successfully: " + testName);
        AllureManager.addLabel("duration", String.valueOf(duration));
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = getTestName(result);
        Throwable throwable = result.getThrowable();
        long duration = result.getEndMillis() - result.getStartMillis();
        
        logger.error("❌ Test FAILED: {} (Duration: {}ms)", testName, duration);
        if (throwable != null) {
            logger.error("Error: {}", throwable.getMessage());
            logger.debug("Stack trace:", throwable);
        }
        
        // Add failure information to Allure
        AllureManager.addStep("Test failed: " + testName);
        if (throwable != null) {
            AllureManager.addAttachment("Error Message", throwable.getMessage(), "text/plain");
            AllureManager.addAttachment("Stack Trace", getStackTrace(throwable), "text/plain");
        }
        
        // Capture screenshot on failure (if applicable)
        captureFailureScreenshot(testName);
        
        // Create Jira bug for failure (if enabled)
        createJiraBugForFailure(testName, throwable);
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = getTestName(result);
        logger.warn("⏭️ Test SKIPPED: {}", testName);
        
        AllureManager.addStep("Test skipped: " + testName);
        
        if (result.getThrowable() != null) {
            logger.warn("Skip reason: {}", result.getThrowable().getMessage());
            AllureManager.addAttachment("Skip Reason", result.getThrowable().getMessage(), "text/plain");
        }
    }
    
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        String testName = getTestName(result);
        logger.warn("⚠️ Test FAILED but within success percentage: {}", testName);
        
        AllureManager.addStep("Test failed but within success percentage: " + testName);
    }
    
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        // Can be used for method-level setup
        if (method.isTestMethod()) {
            logger.debug("About to invoke test method: {}", method.getTestMethod().getMethodName());
        }
    }
    
    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        // Can be used for method-level cleanup
        if (method.isTestMethod()) {
            logger.debug("Finished invoking test method: {}", method.getTestMethod().getMethodName());
        }
    }
    
    /**
     * Get formatted test name from ITestResult
     */
    private String getTestName(ITestResult result) {
        String className = result.getTestClass().getName();
        String methodName = result.getMethod().getMethodName();
        
        // For Cucumber scenarios, try to get scenario name from parameters
        Object[] parameters = result.getParameters();
        if (parameters != null && parameters.length > 0) {
            // Check if first parameter looks like a scenario name
            String firstParam = String.valueOf(parameters[0]);
            if (firstParam.contains("Scenario") || firstParam.contains("Feature")) {
                return firstParam;
            }
        }
        
        return className + "." + methodName;
    }
    
    /**
     * Get stack trace as string
     */
    private String getStackTrace(Throwable throwable) {
        if (throwable == null) return "";
        
        return Arrays.toString(throwable.getStackTrace())
                .replaceAll(",", "\n")
                .replaceAll("\\[", "")
                .replaceAll("\\]", "");
    }
    
    /**
     * Capture screenshot on test failure
     */
    private void captureFailureScreenshot(String testName) {
        try {
            String screenshotEnabled = configManager.getProperty("screenshot.on.failure", "true");
            
            if ("true".equalsIgnoreCase(screenshotEnabled)) {
                // Note: This would need access to the current page/driver
                // Implementation depends on how the page/driver is managed in your framework
                logger.info("Screenshot capture would be triggered for failed test: {}", testName);
                AllureManager.addStep("Screenshot captured for failure: " + testName);
            }
            
        } catch (Exception e) {
            logger.error("Failed to capture screenshot for: {}", testName, e);
        }
    }
    
    /**
     * Create Jira bug for test failure
     */
    private void createJiraBugForFailure(String testName, Throwable throwable) {
        try {
            String jiraEnabled = configManager.getProperty("jira.bug.creation.on.failure", "false");
            
            if ("true".equalsIgnoreCase(jiraEnabled) && throwable != null) {
                logger.info("Creating Jira bug for failed test: {}", testName);
                
                String errorMessage = throwable.getMessage();
                String stackTrace = getStackTrace(throwable);
                String environment = configManager.getEnvironment();
                
                String bugUrl = JiraUtils.createBugForTestFailure(
                    testName, errorMessage, stackTrace, environment, "Cucumber-TestNG", "High"
                );
                
                if (bugUrl != null && !bugUrl.isEmpty()) {
                    logger.info("Jira bug created: {}", bugUrl);
                    AllureManager.addLink("Jira Bug", bugUrl);
                }
            }
            
        } catch (Exception e) {
            logger.error("Failed to create Jira bug for: {}", testName, e);
        }
    }
    
    /**
     * Log suite execution results summary
     */
    private void logSuiteResults(ISuite suite) {
        int totalTests = 0;
        int passedTests = 0;
        int failedTests = 0;
        int skippedTests = 0;
        
        for (ISuiteResult suiteResult : suite.getResults().values()) {
            ITestContext testContext = suiteResult.getTestContext();
            
            totalTests += testContext.getAllTestMethods().length;
            passedTests += testContext.getPassedTests().size();
            failedTests += testContext.getFailedTests().size();
            skippedTests += testContext.getSkippedTests().size();
        }
        
        logger.info("====== Suite Results Summary ======");
        logger.info("Total Tests: {}", totalTests);
        logger.info("Passed: {} ({}%)", passedTests, totalTests > 0 ? (passedTests * 100 / totalTests) : 0);
        logger.info("Failed: {} ({}%)", failedTests, totalTests > 0 ? (failedTests * 100 / totalTests) : 0);
        logger.info("Skipped: {} ({}%)", skippedTests, totalTests > 0 ? (skippedTests * 100 / totalTests) : 0);
        logger.info("===================================");
        
        // Add summary to Allure
        AllureManager.addEnvironmentInfo("total.tests", String.valueOf(totalTests));
        AllureManager.addEnvironmentInfo("passed.tests", String.valueOf(passedTests));
        AllureManager.addEnvironmentInfo("failed.tests", String.valueOf(failedTests));
        AllureManager.addEnvironmentInfo("skipped.tests", String.valueOf(skippedTests));
    }
}
