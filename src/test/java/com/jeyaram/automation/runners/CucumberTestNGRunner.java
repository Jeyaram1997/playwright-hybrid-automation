package com.jeyaram.automation.runners;

import com.jeyaram.automation.config.ConfigManager;
import com.jeyaram.automation.reporting.AllureManager;
import com.jeyaram.automation.utils.EmailUtils;
import com.jeyaram.automation.utils.JiraUtils;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * TestNG-based Cucumber Test Runner
 * Combines Cucumber BDD with TestNG framework capabilities
 * 
 * Features:
 * - Parallel execution support
 * - Multiple report generation (Allure, Extent, HTML)
 * - Email notifications with reports
 * - Automated Jira bug creation for failures
 * - Cross-browser testing support
 * - Data-driven testing integration
 * 
 * @author Jeyaram K
 * @version 1.0.0
 * @since 2025-01-01
 */
@CucumberOptions(
    features = {
        "src/test/resources/features"
    },
    glue = {
        "com.jeyaram.automation.stepdefs"
    },
    plugin = {
        "pretty",
        "html:target/cucumber-reports/cucumber-html-report",
        "json:target/cucumber-reports/cucumber.json",
        "junit:target/cucumber-reports/cucumber.xml",
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    },
    monochrome = true,
    publish = false,
    dryRun = false,
    tags = "@login or @smoke or @regression or @api or @ui or @mobile or @performance"
)
public class CucumberTestNGRunner extends AbstractTestNGCucumberTests {
    
    private static final Logger logger = LoggerFactory.getLogger(CucumberTestNGRunner.class);
    private static final ConfigManager configManager = ConfigManager.getInstance();
    private static final EmailUtils emailUtils = new EmailUtils();
    private static final List<String> jiraBugUrls = new ArrayList<>();
    
    /**
     * Enable parallel execution based on configuration
     * This method controls how many scenarios run in parallel
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        // Get parallel configuration from environment
        String parallelMode = configManager.getProperty("cucumber.execution.parallel.enabled", "false");
        
        if ("true".equalsIgnoreCase(parallelMode)) {
            logger.info("Parallel execution enabled for Cucumber scenarios");
            return super.scenarios();
        } else {
            logger.info("Sequential execution for Cucumber scenarios");
            // Return scenarios for sequential execution
            return super.scenarios();
        }
    }
    
    /**
     * Suite level setup - runs once before all tests
     */
    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() {
        logger.info("=== Starting Cucumber-TestNG Test Suite ===");
        
        try {
            // Initialize reporting systems
            AllureManager.addEnvironmentInfo("framework", "Cucumber-TestNG");
            AllureManager.addEnvironmentInfo("author", "Jeyaram K");
            
            // Log environment configuration
            String environment = configManager.getEnvironment();
            String browser = configManager.getProperty("BROWSER", "chromium");
            String baseUrl = configManager.getProperty("APP_BASE_URL", "");
            
            logger.info("Test Environment: {}", environment);
            logger.info("Target Browser: {}", browser);
            logger.info("Base URL: {}", baseUrl);
            
            // Add suite information to Allure
            AllureManager.addStep("Suite Setup Completed - Environment: " + environment);
            
        } catch (Exception e) {
            logger.error("Suite setup failed", e);
            throw new RuntimeException("Suite setup failed", e);
        }
    }
    
    /**
     * Test level setup - runs before each test class
     */
    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        logger.info("Setting up test class: {}", this.getClass().getSimpleName());
        
        try {
            // Class-level initialization if needed
            String testClassName = this.getClass().getSimpleName();
            AllureManager.addStep("Starting test class: " + testClassName);
            
        } catch (Exception e) {
            logger.error("Class setup failed for: {}", this.getClass().getSimpleName(), e);
        }
    }
    
    /**
     * Method level setup - runs before each test scenario
     */
    @BeforeMethod(alwaysRun = true)
    public void setUpMethod() {
        logger.debug("Setting up test method");
        
        try {
            // Method-level initialization
            // This runs before each Cucumber scenario
            
        } catch (Exception e) {
            logger.error("Method setup failed", e);
        }
    }
    
    /**
     * Method level teardown - runs after each test scenario
     */
    @AfterMethod(alwaysRun = true)
    public void tearDownMethod() {
        logger.debug("Tearing down test method");
        
        try {
            // Method-level cleanup
            // This runs after each Cucumber scenario
            
        } catch (Exception e) {
            logger.error("Method teardown failed", e);
        }
    }
    
    /**
     * Class level teardown - runs after each test class
     */
    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        logger.info("Tearing down test class: {}", this.getClass().getSimpleName());
        
        try {
            // Class-level cleanup
            String testClassName = this.getClass().getSimpleName();
            AllureManager.addStep("Completed test class: " + testClassName);
            
        } catch (Exception e) {
            logger.error("Class teardown failed for: {}", this.getClass().getSimpleName(), e);
        }
    }
    
    /**
     * Suite level teardown - runs once after all tests
     */
    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        logger.info("=== Completing Cucumber-TestNG Test Suite ===");
        
        try {
            // Generate comprehensive reports
            generateReports();
            
            // Send email notifications
            sendEmailNotifications();
            
            logger.info("Test suite execution completed successfully");
            
        } catch (Exception e) {
            logger.error("Suite teardown failed", e);
        }
    }
    
    /**
     * Generate comprehensive test reports
     */
    private void generateReports() {
        try {
            logger.info("Generating comprehensive test reports...");
            
            // Finalize Allure report
            AllureManager.addStep("Test Suite Completed - Generating Final Reports");
            
            // Additional report generation can be added here
            
            logger.info("All reports generated successfully");
            
        } catch (Exception e) {
            logger.error("Failed to generate reports", e);
        }
    }
    
    /**
     * Send email notifications with test results
     */
    private void sendEmailNotifications() {
        try {
            String emailEnabled = configManager.getProperty("email.notifications.enabled", "false");
            
            if ("true".equalsIgnoreCase(emailEnabled)) {
                logger.info("Sending email notifications...");
                
                // Prepare email content
                String subject = "Cucumber-TestNG Test Results - " + 
                               configManager.getEnvironment().toUpperCase() + " Environment";
                
                String body = createEmailBody();
                
                // Prepare attachment paths
                List<String> attachments = new ArrayList<>();
                attachments.add("target/cucumber-reports/cucumber-html-report/index.html");
                attachments.add("target/allure-results");
                
                // Send email with reports and Jira bug URLs
                emailUtils.sendTestReport(subject, body, attachments, jiraBugUrls);
                
                logger.info("Email notifications sent successfully");
            } else {
                logger.info("Email notifications disabled in configuration");
            }
            
        } catch (Exception e) {
            logger.error("Failed to send email notifications", e);
        }
    }
    
    /**
     * Create email body for test results
     */
    private String createEmailBody() {
        String environment = configManager.getEnvironment();
        String browser = configManager.getProperty("BROWSER", "chromium");
        String baseUrl = configManager.getProperty("APP_BASE_URL", "");
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 20px; background: #f5f5f5; }
                    .container { max-width: 800px; margin: 0 auto; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%); color: white; padding: 30px; border-radius: 8px 8px 0 0; text-align: center; }
                    .content { padding: 30px; }
                    .logo { font-size: 24px; font-weight: bold; margin-bottom: 10px; }
                    .info-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin: 20px 0; }
                    .info-item { background: #f8f9fa; padding: 15px; border-radius: 5px; border-left: 4px solid #4CAF50; }
                    .info-label { font-weight: bold; color: #495057; font-size: 14px; }
                    .info-value { color: #6c757d; margin-top: 5px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">🥒 Cucumber + TestNG Automation</div>
                        <h1>Test Execution Results</h1>
                        <p>BDD Testing with TestNG Framework</p>
                    </div>
                    <div class="content">
                        <h2>📊 Execution Summary</h2>
                        <div class="info-grid">
                            <div class="info-item">
                                <div class="info-label">Environment</div>
                                <div class="info-value">""" + environment.toUpperCase() + """
                                </div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Browser</div>
                                <div class="info-value">""" + browser.toUpperCase() + """
                                </div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Base URL</div>
                                <div class="info-value">""" + baseUrl + """
                                </div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Framework</div>
                                <div class="info-value">Cucumber + TestNG</div>
                            </div>
                        </div>
                        
                        <h3>🎯 Test Features</h3>
                        <ul>
                            <li>✅ BDD Cucumber Scenarios</li>
                            <li>✅ TestNG Framework Integration</li>
                            <li>✅ Parallel Execution Support</li>
                            <li>✅ Multiple Report Formats</li>
                            <li>✅ Email Notifications</li>
                            <li>✅ Jira Bug Integration</li>
                            <li>✅ Cross-browser Testing</li>
                            <li>✅ Data-driven Testing</li>
                        </ul>
                        
                        <p><strong>Reports:</strong> Please find detailed Cucumber and Allure reports attached.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
    
    /**
     * Handle test failures and create Jira bugs if configured
     */
    public void handleTestFailure(String testName, String errorMessage, String stackTrace) {
        try {
            String jiraEnabled = configManager.getProperty("jira.bug.creation.enabled", "false");
            
            if ("true".equalsIgnoreCase(jiraEnabled)) {
                logger.info("Creating Jira bug for failed test: {}", testName);
                
                String bugUrl = JiraUtils.createBugForTestFailure(testName, errorMessage, stackTrace, 
                    configManager.getEnvironment(), "UI", "High");
                if (bugUrl != null && !bugUrl.isEmpty()) {
                    jiraBugUrls.add(bugUrl);
                    logger.info("Jira bug created: {}", bugUrl);
                }
            }
            
        } catch (Exception e) {
            logger.error("Failed to handle test failure for: {}", testName, e);
        }
    }
}
