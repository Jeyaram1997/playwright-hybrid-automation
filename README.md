# Playwright Java Hybrid Automation Framework

**Author:** Jeyaram K  
**Version:** 1.0.1  
**Date:** July 2025  
**Last Updated:** Java 17 Upgrade & Validation

## 🚀 Overview

This is a comprehensive test automation framework built with Playwright Java that supports UI, API, mobile, and performance testing. The framework integrates multiple testing frameworks (Cucumber, JUnit, TestNG) and provides advanced reporting capabilities with Allure and Extent Reports.

### 🆕 Recent Updates (January 2025)

**Java 17 Compatibility Upgrade** - The framework has been fully upgraded and validated for Java 17:

✅ **All Dependencies Updated**:
- Playwright Java 1.48.0 (with API compatibility fixes)
- Appium Java Client 8.6.0 (Java 17 compatible)
- Selenium WebDriver 4.18.1 (added for mobile testing)
- All Maven plugins updated to latest versions
- Java compiler source/target set to 17

✅ **API Compatibility Fixes**:
- **APIBase.java**: Updated RequestOptions API usage for Playwright 1.48+
- **PlaywrightBase.java**: Fixed device emulation without deprecated DeviceDescriptor
- **MobileBase.java**: Updated Appium capabilities for version 8.6.0
- **AdvancedReportGenerator.java**: Fixed PDF generation with iText7/8 API

✅ **Framework Validation**:
- All source and test files compile successfully
- All step definitions are error-free
- Mobile, API, UI, and Performance testing capabilities maintained
- Reporting utilities fully functional

### 🔧 Framework Status
```bash
# Current validation status (January 2025)
✅ Compilation: PASSED (mvn compile test-compile)
✅ Dependencies: RESOLVED (all Java 17 compatible)
✅ Core Classes: VALIDATED (PlaywrightBase, APIBase, MobileBase, PerformanceBase)
✅ Step Definitions: VALIDATED (UI, API, Mobile, Performance)
✅ Reporting: VALIDATED (Allure, Extent, PDF, Excel, CSV)
✅ Mobile Testing: VALIDATED (Appium 8.6.0 + Selenium 4.18.1)
✅ Performance Testing: VALIDATED (JMeter integration)
```

## 📋 Features

### Core Features
- **Multi-Platform Testing**: UI (Web), API (REST), Mobile (Android/iOS), Performance
- **Hybrid Framework**: Supports Cucumber BDD, JUnit 5, and TestNG
- **Advanced Reporting**: Allure Reports, Extent Reports, PDF, Excel, CSV outputs
- **AI-Powered Testing**: AI plugins for code generation, test optimization, and analytics
- **Data Management**: Support for CSV, JSON, Excel, .properties, .env files, encrypted secrets
- **Cross-Browser Testing**: Chrome, Firefox, Safari, Edge, WebKit
- **CI/CD Integration**: Ready for Jenkins, GitHub Actions, Azure DevOps
- **JIRA Integration**: Automated bug reporting on test failures
- **Email Notifications**: Auto-send reports with bug links after test completion

### Advanced Features
- **Screenshot Management**: Automatic screenshots on failures and key steps
- **Performance Metrics**: Built-in performance testing and monitoring
- **Security**: Encrypted configuration management
- **Command Line Tracking**: Detailed CLI usage statistics
- **Wait Utilities**: Smart wait strategies and polling mechanisms
- **Mobile Gestures**: Comprehensive mobile testing with gestures support
- **API Schema Validation**: JSONPath and schema validation utilities

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| Core Framework | Playwright Java | 1.48.0 |
| Build Tool | Maven | 3.9+ |
| Java Version | JDK | 17+ |
| BDD Framework | Cucumber | 7.18.0 |
| Test Frameworks | JUnit 5, TestNG | 5.10.2, 7.10.2 |
| **Integration** | **Cucumber-TestNG** | **Hybrid** |
| Reporting | Allure, Extent | 2.27.0, 5.1.2 |
| API Testing | REST Assured | 5.4.0 |
| Mobile Testing | Appium | 8.6.0 |
| Performance | JMeter | 5.6.3 |
| Data Processing | Apache POI, Jackson | 5.2.5, 2.17.1 |
| AI Integration | LangChain4J | 0.31.0 |

## 🥒 Cucumber-TestNG Integration

This framework provides a powerful **Cucumber-TestNG hybrid runner** that combines the best of both frameworks:

### Features:
- **BDD Scenarios**: Write tests in Gherkin syntax with Cucumber
- **TestNG Power**: Leverage TestNG's parallel execution, listeners, and configuration
- **Unified Reporting**: Single report combining Cucumber scenarios and TestNG results
- **Parallel Execution**: Run scenarios in parallel with TestNG's thread management
- **Advanced Listeners**: Enhanced test monitoring and failure handling
- **Jira Integration**: Automatic bug creation for failed scenarios
- **Email Notifications**: Comprehensive test results via email

### Quick Start with Cucumber-TestNG:

**Windows CLI (Batch File):**
```cmd
# Run smoke tests
run-cucumber-testng.bat smoke chromium development

# Run regression tests
run-cucumber-testng.bat regression firefox staging

# Run all tests with TestNG suite
run-cucumber-testng.bat testng chromium production

# Run specific test types
run-cucumber-testng.bat api     # API tests only
run-cucumber-testng.bat ui      # UI tests only  
run-cucumber-testng.bat mobile  # Mobile tests only
run-cucumber-testng.bat performance # Performance tests only
run-cucumber-testng.bat all     # All tests
```

**Cross-Platform Maven:**
```bash
# Using Maven directly (Linux/Mac/Windows)
mvn clean test -Dtest=CucumberTestNGRunner -Dcucumber.filter.tags="@smoke"
mvn clean test -DsuiteXmlFile=src/test/resources/testng.xml
```

### TestNG Configuration:
```xml
<!-- testng.xml supports multiple test suites -->
<suite name="Playwright-Cucumber-TestNG-Suite" parallel="methods" thread-count="3">
    <test name="Smoke-Tests">
        <parameter name="tags" value="@smoke"/>
    </test>
    <test name="API-Tests" parallel="methods">
        <parameter name="tags" value="@api"/>
    </test>
</suite>
```

## 📁 Project Structure

```
playwright-java-framework/
├── src/
│   ├── main/java/com/jeyaram/automation/
│   │   ├── base/
│   │   │   ├── PlaywrightBase.java       # Core UI automation base
│   │   │   ├── APIBase.java              # API testing utilities
│   │   │   ├── MobileBase.java           # Mobile testing base
│   │   │   └── PerformanceBase.java      # Performance testing utilities
│   │   ├── config/
│   │   │   ├── ConfigManager.java        # Configuration management
│   │   │   └── EnvironmentConfig.java    # Environment-specific configs
│   │   ├── utils/
│   │   │   ├── DataUtils.java            # Data handling utilities
│   │   │   ├── JsonUtils.java            # JSON processing
│   │   │   ├── ScreenshotUtils.java      # Screenshot management
│   │   │   ├── WaitUtils.java            # Wait strategies
│   │   │   ├── EmailUtils.java           # Email notifications
│   │   │   ├── JiraUtils.java            # JIRA integration
│   │   │   ├── SecurityUtils.java        # Encryption utilities
│   │   │   └── CommandLineTracker.java   # CLI usage tracking
│   │   ├── reporting/
│   │   │   ├── AllureManager.java        # Allure reporting
│   │   │   ├── ExtentManager.java        # Extent reporting
│   │   │   └── AdvancedReportGenerator.java # Multi-format reports
│   │   └── plugins/
│   │       └── AIPluginManager.java      # AI tools integration
│   └── test/java/com/jeyaram/automation/
│       ├── tests/
│       │   ├── ui/
│       │   │   └── SampleUITests.java    # UI test cases
│       │   └── api/
│       │       └── SampleAPITests.java   # API test cases
│       ├── runners/
│       │   ├── CucumberTestRunner.java   # Cucumber runner
│       │   ├── CucumberTestNGRunner.java # Cucumber-TestNG hybrid runner
│       │   └── TestNGBaseTest.java       # TestNG base runner
│       ├── listeners/
│       │   └── TestNGListener.java       # TestNG listener for enhanced reporting
│       ├── stepdefs/
│       │   ├── UIStepDefinitions.java    # UI step definitions
│       │   ├── APIStepDefinitions.java   # API step definitions
│       │   ├── MobileStepDefinitions.java # Mobile step definitions
│       │   └── PerformanceStepDefinitions.java # Performance step definitions
│       └── resources/
│           ├── features/                 # Cucumber feature files
│           │   ├── ui/
│           │   │   └── sample_ui_tests.feature
│           │   ├── api/
│           │   │   └── sample_api_tests.feature
│           │   ├── mobile/
│           │   │   └── sample_mobile_tests.feature
│           │   └── performance/
│           │       └── sample_performance_tests.feature
│           └── testng.xml               # TestNG suite configuration
├── test-data/                            # Test data files
│   ├── users.json                        # Sample user data
│   └── login_data.csv                    # Sample login data
├── test-results/                         # Test execution results
├── target/                               # Build outputs
├── pom.xml                              # Maven configuration
├── .env                                 # Environment variables
├── config.properties                    # Application configuration
├── run-cucumber-testng.bat              # CLI runner for Windows
└── README.md                           # This file
```

## ⚙️ Setup and Installation

### Prerequisites
- Java 17 or higher
- Maven 3.9 or higher
- Node.js 16+ (for Playwright browsers)
- Git

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/your-repo/playwright-java-framework.git
   cd playwright-java-framework
   ```

2. **Install Dependencies**
   ```bash
   mvn clean install
   ```

3. **Install Playwright Browsers**
   ```bash
   # For CLI execution
   mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install"
   
   # Or during test compilation (automatic)
   mvn test-compile
   ```

4. **Verify Installation**
   ```bash
   # Compile and verify all dependencies
   mvn clean compile test-compile
   
   # Run a quick test to verify setup
   mvn test -Dtest=SampleUITests -Denvironment=development
   ```

5. **Configure Environment**
   Create a `.env` file in the project root:
   ```properties
   # Application Configuration
   APP_BASE_URL=https://your-app-url.com
   API_BASE_URL=https://api.your-app.com
   
   # Browser Configuration
   BROWSER=chromium
   HEADLESS=false
   VIEWPORT_WIDTH=1920
   VIEWPORT_HEIGHT=1080
   
   # Environment Settings
   ENVIRONMENT=development
   TEST_DATA_PATH=test-data
   SCREENSHOT_PATH=test-results/screenshots
   
   # JIRA Configuration
   JIRA_URL=https://your-jira-instance.atlassian.net
   JIRA_USERNAME=your-email@company.com
   JIRA_API_TOKEN=your-api-token
   JIRA_PROJECT_KEY=TEST
   
   # Email Configuration
   EMAIL_SMTP_HOST=smtp.gmail.com
   EMAIL_SMTP_PORT=587
   EMAIL_USERNAME=your-email@gmail.com
   EMAIL_PASSWORD=your-app-password
   EMAIL_RECIPIENTS=team@company.com,qa@company.com
   
   # AI Configuration
   AI_PLAYWRIGHT_CODEGEN_ENABLED=true
   AI_ELEMENT_LOCATOR_ENABLED=true
   AI_DATA_GENERATION_ENABLED=true
   AI_TEST_OPTIMIZATION_ENABLED=true
   ```

## 🏃‍♂️ Running Tests

### Command Line Options

#### Run All Tests
```bash
mvn clean test
```

#### Run Specific Test Types
```bash
# UI Tests only
mvn test -Dtest=**/*UITests*

# API Tests only
mvn test -Dtest=**/*APITests*

# Mobile Tests only
mvn test -Dtest=**/*MobileTests*

# Performance Tests only
mvn test -Dtest=**/*PerformanceTests*
```

#### Run with Different Browsers
```bash
# Chrome
mvn test -Dbrowser=chromium

# Firefox
mvn test -Dbrowser=firefox

# Safari
mvn test -Dbrowser=webkit

# Headless mode
mvn test -Dheadless=true
```

#### Run Cucumber Tests
```bash
mvn test -Dtest=CucumberTestRunner
```

#### Run with TestNG
```bash
mvn test -Dtest=TestNGBaseTest
```

### Environment-Specific Testing
```bash
# Development environment
mvn test -Denvironment=development

# Staging environment
mvn test -Denvironment=staging

# Production environment
mvn test -Denvironment=production
```

### Parallel Execution
```bash
# Run tests in parallel (4 threads)
mvn test -Dparallel.tests=4

# Run with custom thread count
mvn test -Dparallel.tests=8
```

## 📊 Reporting

### Report Generation

The framework automatically generates multiple report formats:

#### Allure Reports
```bash
# Generate Allure report
mvn allure:serve

# Or generate static report
mvn allure:report
```
Access at: `target/site/allure-maven-plugin/index.html`

#### Extent Reports
Generated automatically at: `target/reports/extent-report.html`

#### Multi-Format Reports
- **PDF**: `target/reports/pdf/`
- **Excel**: `target/reports/excel/`
- **CSV**: `target/reports/csv/`
- **HTML**: `target/reports/html/`

### Report Features
- **Screenshots**: Automatic capture on failures and key steps
- **Test Metrics**: Execution time, pass/fail ratios, performance data
- **Environment Info**: Browser, OS, test environment details
- **Error Analysis**: Detailed stack traces and error categorization
- **Trend Analysis**: Historical test execution trends

## 🤖 AI-Powered Features

### Available AI Plugins

1. **Playwright Code Generation**
   ```java
   AIPluginManager aiManager = AIPluginManager.getInstance();
   String code = aiManager.generatePlaywrightCode("Click login button and verify dashboard", "https://app.com/login");
   ```

2. **Element Locator Optimization**
   ```java
   String optimizedSelector = aiManager.optimizeSelector("xpath=//div[1]/span[2]", pageContent);
   ```

3. **Test Data Generation**
   ```java
   Map<String, Object> testData = aiManager.generateTestData("user", 10, constraints);
   ```

4. **Performance Analysis**
   ```java
   Map<String, Object> analysis = aiManager.analyzeTestPerformance(testMetrics);
   ```

### Configuration
Enable/disable AI features in `.env`:
```properties
AI_PLAYWRIGHT_CODEGEN_ENABLED=true
AI_ELEMENT_LOCATOR_ENABLED=true
AI_DATA_GENERATION_ENABLED=true
AI_TEST_OPTIMIZATION_ENABLED=true
AI_TEST_ANALYTICS_ENABLED=true
```

## 📱 Mobile Testing

### Setup for Mobile Testing

1. **Install Appium**
   ```bash
   npm install -g appium@2.x
   npm install -g appium-doctor
   appium-doctor --android  # Verify Android setup
   appium-doctor --ios      # Verify iOS setup
   ```

2. **Configure Mobile Capabilities**
   ```java
   MobileBase mobileBase = new MobileBase();
   mobileBase.initializeAndroidDriver("path/to/app.apk", "device_id");
   ```

   **Note**: The framework uses Appium Java Client 8.6.0 for Java 17 compatibility. This version provides stable mobile automation capabilities with string-based capabilities instead of deprecated enums.

### Mobile Test Example
```java
@Test
public void testMobileLogin() {
    mobileBase.initializeAndroidDriver("app.apk", "emulator-5554");
    
    // Perform mobile gestures
    mobileBase.tapElement("login_button");
    mobileBase.typeText("username_field", "testuser");
    mobileBase.swipeUp();
    
    // Take screenshot
    screenshotUtils.captureMobileScreenshot(driver, "mobile_login");
}
```

## 🌐 API Testing

### API Test Example
```java
@Test
public void testCreateUser() {
    APIBase apiBase = new APIBase();
    apiBase.initializeAPI("https://api.example.com", 30000);
    
    Map<String, Object> userData = new HashMap<>();
    userData.put("name", "John Doe");
    userData.put("email", "john@example.com");
    
    APIResponse response = apiBase.post("/users", userData, headers);
    
    assertEquals(response.status(), 201);
    assertTrue(response.text().contains("John Doe"));
}
```

### Schema Validation
```java
// Validate JSON schema
apiBase.validateJsonSchema(response.text(), "user-schema.json");

// Extract data with JSONPath
String userName = apiBase.extractJsonPath(response.text(), "$.data.name");
```

## ⚡ Performance Testing

### Performance Test Example
```java
@Test
public void testPageLoadPerformance() {
    PerformanceBase perfBase = new PerformanceBase();
    perfBase.startPerformanceMonitoring();
    
    page.navigate("https://example.com");
    
    Map<String, Object> metrics = perfBase.getPerformanceMetrics();
    perfBase.generatePerformanceReport(metrics, "page_load_test");
    
    // Assert performance criteria
    assertTrue((Long) metrics.get("loadTime") < 3000); // Less than 3 seconds
}
```

## 🔐 Security and Configuration

### Encrypted Configuration
```java
// Encrypt sensitive data
String encrypted = SecurityUtils.encrypt("sensitive_password");

// Store in config
configManager.setProperty("database.password.encrypted", encrypted);

// Retrieve and decrypt
String password = configManager.getEncryptedProperty("database.password.encrypted", "");
```

### Environment Management
```java
// Environment-specific configuration
EnvironmentConfig envConfig = new EnvironmentConfig();
String dbUrl = envConfig.getDatabaseUrl(); // Automatically selects based on environment
String apiKey = envConfig.getApiKey();
```

## 🐛 JIRA Integration

### Automatic Bug Reporting
```java
// Configure in TestNG base or test listener
@Override
public void onTestFailure(ITestResult result) {
    String jiraUrl = JiraUtils.createBugForTestFailure(
        result.getMethod().getMethodName(),
        result.getThrowable().getMessage(),
        getStackTrace(result.getThrowable()),
        System.getProperty("environment", "development"),
        "UI",
        "High"
    );
    
    if (jiraUrl != null) {
        logger.info("JIRA bug created: {}", jiraUrl);
        AllureManager.addStep("JIRA Bug Created: " + jiraUrl);
    }
}
```

### JIRA Configuration
```properties
# In .env file
JIRA_URL=https://company.atlassian.net
JIRA_USERNAME=automation@company.com
JIRA_API_TOKEN=ATATT3xFfGF0...
JIRA_PROJECT_KEY=TEST
JIRA_ISSUE_TYPE=Bug
```

## 📧 Email Notifications

### Automatic Email Reports
```java
@AfterSuite
public void sendEmailReport() {
    EmailUtils emailUtils = new EmailUtils();
    
    // Collect report paths
    List<String> attachments = Arrays.asList(
        "target/allure-results",
        "target/extent-report.html",
        "target/reports/pdf/test-report.pdf"
    );
    
    // Send email with reports
    emailUtils.sendTestReport(
        "Test Execution Completed",
        "Please find attached test execution reports.",
        attachments,
        jiraBugUrls
    );
}
```

## 📝 Data Management

### Supported Data Formats

#### Excel Data
```java
// Read Excel data
List<Map<String, Object>> data = DataUtils.readExcelData("test-data/users.xlsx", "Sheet1");

// Write Excel data
DataUtils.writeExcelData(results, "test-results/execution-report.xlsx");
```

#### CSV Data
```java
// Read CSV
List<Map<String, String>> csvData = DataUtils.readCsvData("test-data/test-cases.csv");

// Write CSV
DataUtils.writeCsvData(testResults, "test-results/results.csv");
```

#### JSON Data
```java
// Parse JSON
Map<String, Object> jsonData = JsonUtils.parseJsonFile("test-data/config.json");

// Generate JSON
String jsonString = JsonUtils.toJson(testObject);
```

## 🔧 Advanced Configuration

### Maven Profiles
```xml
<!-- Development Profile -->
<profile>
    <id>development</id>
    <properties>
        <environment>development</environment>
        <browser>chromium</browser>
        <headless>false</headless>
    </properties>
</profile>

<!-- CI/CD Profile -->
<profile>
    <id>ci</id>
    <properties>
        <environment>staging</environment>
        <browser>chromium</browser>
        <headless>true</headless>
        <parallel.tests>4</parallel.tests>
    </properties>
</profile>
```

### Browser Configuration
```java
// Custom browser configuration
PlaywrightBase.BrowserConfig config = new PlaywrightBase.BrowserConfig()
    .setBrowser("chromium")
    .setHeadless(false)
    .setViewport(1920, 1080)
    .setSlowMo(100)
    .setVideoRecording(true)
    .setTracing(true);

playwrightBase.initializeBrowser(config);
```

## 🚀 CI/CD Integration

### Jenkins Pipeline
```groovy
pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/your-repo/playwright-java-framework.git'
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn clean test -Pci'
            }
        }
        
        stage('Report') {
            steps {
                allure([
                    includeProperties: false,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'target/allure-results']]
                ])
            }
        }
    }
    
    post {
        always {
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/reports',
                reportFiles: 'extent-report.html',
                reportName: 'Extent Report'
            ])
        }
    }
}
```

### GitHub Actions
```yaml
name: Test Automation

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Install Playwright
      run: mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install --with-deps"
    
    - name: Run tests
      run: mvn clean test -Pci
    
    - name: Generate Allure Report
      if: always()
      run: mvn allure:report
    
    - name: Upload Allure Results
      if: always()
      uses: actions/upload-artifact@v3
      with:
        name: allure-results
        path: target/allure-results
```

## 🎯 Best Practices

### Test Organization
1. **Use Page Object Model**: Create separate page objects for each application page
2. **Data-Driven Testing**: Externalize test data using Excel, CSV, or JSON files
3. **Parallel Execution**: Configure parallel execution for faster test runs
4. **Error Handling**: Implement comprehensive error handling and recovery mechanisms
5. **Reporting**: Use meaningful test descriptions and attach relevant screenshots/logs

### Code Quality
1. **Naming Conventions**: Use descriptive names for tests, methods, and variables
2. **Documentation**: Add comprehensive JavaDoc comments
3. **Code Reusability**: Create utility methods for common operations
4. **Version Control**: Use Git with meaningful commit messages
5. **Code Reviews**: Implement peer review process for all changes

### Performance Optimization
1. **Browser Management**: Reuse browser instances when possible
2. **Wait Strategies**: Use explicit waits instead of Thread.sleep()
3. **Resource Cleanup**: Properly close browsers and drivers after tests
4. **Parallel Testing**: Configure optimal thread count for parallel execution
5. **Test Data**: Use efficient data structures and minimize test data size

## 🐛 Troubleshooting

### Common Issues

#### Compilation Errors (Java 17 Upgrade)
```bash
# If you encounter compilation errors after upgrade, clean and reinstall
mvn clean install -U

# Force dependency refresh
mvn dependency:resolve-sources dependency:resolve

# Verify Java version
java -version  # Should show Java 17+
mvn -version   # Should show Maven 3.9+
```

#### API Compatibility Issues
The framework has been updated for Playwright 1.48+ API changes:
- **RequestOptions**: Now uses individual `setHeader()` and `setQueryParam()` calls
- **Device Emulation**: Manual device configuration replaces deprecated `DeviceDescriptor`
- **Mobile Capabilities**: String-based capabilities for Appium 8.6.0

#### Browser Installation Issues
```bash
# Reinstall browsers
mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install --force"

# Check installed browsers  
mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install-deps"
```

#### Memory Issues
```bash
# Increase Maven memory
export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=512m"

# Or set in pom.xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <argLine>-Xmx2048m</argLine>
    </configuration>
</plugin>
```

#### Test Flakiness
1. **Increase Timeouts**: Adjust default wait timeouts in configuration
2. **Retry Mechanism**: Implement test retry for flaky tests
3. **Better Selectors**: Use stable selectors (data-testid, CSS classes)
4. **Wait Strategies**: Use appropriate wait conditions
5. **Test Environment**: Ensure stable test environment

#### Mobile Testing Issues
```bash
# Check Appium installation
appium-doctor

# Start Appium server
appium --port 4723

# Check connected devices
adb devices
```

## 📈 Metrics and Analytics

### Test Execution Metrics
- Test execution time
- Pass/fail ratios
- Browser-specific results
- Environment-specific results
- Historical trends

### Performance Metrics
- Page load times
- API response times
- Resource utilization
- Memory usage
- Network analysis

### Quality Metrics
- Code coverage
- Test coverage
- Defect density
- Test maintenance effort
- Automation ROI

## 🤝 Contributing

### Development Setup
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Run the test suite
6. Submit a pull request

### Coding Standards
- Follow Java naming conventions
- Add comprehensive tests
- Update documentation
- Use meaningful commit messages
- Follow the existing code style

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions:

- **Author**: Jeyaram K
- **Email**: jeyaram.qa@example.com
- **GitHub**: https://github.com/jeyaram-qa
- **LinkedIn**: https://linkedin.com/in/jeyaram-qa

### Documentation
- **Framework Documentation**: `docs/`
- **API Documentation**: Generated JavaDocs in `target/site/apidocs`
- **Test Reports**: `target/reports/`

### Community
- **Issues**: Report bugs and feature requests on GitHub Issues
- **Discussions**: Join our GitHub Discussions for Q&A
- **Wiki**: Check our Wiki for advanced topics and tutorials

---

**Happy Testing! 🎉**

*This framework is designed to provide a robust, scalable, and maintainable solution for modern test automation needs. It combines the power of Playwright with industry best practices to deliver high-quality software testing capabilities.*
