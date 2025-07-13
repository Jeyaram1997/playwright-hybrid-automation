@echo off
REM Cucumber-TestNG Test Runner Script
REM Author: Jeyaram K
REM 
REM This script provides easy execution of Cucumber-TestNG tests with various options
REM Usage: run-cucumber-testng.bat [test-type] [browser] [environment]

echo ====================================
echo Playwright Cucumber-TestNG Runner
echo ====================================

REM Set default values
set TEST_TYPE=%1
set BROWSER=%2
set ENVIRONMENT=%3

if "%TEST_TYPE%"=="" set TEST_TYPE=smoke
if "%BROWSER%"=="" set BROWSER=chromium
if "%ENVIRONMENT%"=="" set ENVIRONMENT=development

echo Test Type: %TEST_TYPE%
echo Browser: %BROWSER%
echo Environment: %ENVIRONMENT%
echo.

REM Set environment variables
set BROWSER=%BROWSER%
set ENVIRONMENT=%ENVIRONMENT%

REM Clean previous results
echo Cleaning previous test results...
if exist target\allure-results rmdir /s /q target\allure-results
if exist target\cucumber-reports rmdir /s /q target\cucumber-reports
if exist target\surefire-reports rmdir /s /q target\surefire-reports

REM Run tests based on type
echo Starting %TEST_TYPE% tests...

if "%TEST_TYPE%"=="smoke" (
    echo Running Smoke Tests...
    mvn clean test -Dtest=CucumberTestNGRunner -Dcucumber.filter.tags="@smoke" -Dbrowser=%BROWSER% -Denvironment=%ENVIRONMENT%
) else if "%TEST_TYPE%"=="regression" (
    echo Running Regression Tests...
    mvn clean test -Dtest=CucumberTestNGRunner -Dcucumber.filter.tags="@regression" -Dbrowser=%BROWSER% -Denvironment=%ENVIRONMENT%
) else if "%TEST_TYPE%"=="api" (
    echo Running API Tests...
    mvn clean test -Dtest=CucumberTestNGRunner -Dcucumber.filter.tags="@api" -Dbrowser=%BROWSER% -Denvironment=%ENVIRONMENT%
) else if "%TEST_TYPE%"=="ui" (
    echo Running UI Tests...
    mvn clean test -Dtest=CucumberTestNGRunner -Dcucumber.filter.tags="@ui" -Dbrowser=%BROWSER% -Denvironment=%ENVIRONMENT%
) else if "%TEST_TYPE%"=="mobile" (
    echo Running Mobile Tests...
    mvn clean test -Dtest=CucumberTestNGRunner -Dcucumber.filter.tags="@mobile" -Dbrowser=%BROWSER% -Denvironment=%ENVIRONMENT%
) else if "%TEST_TYPE%"=="performance" (
    echo Running Performance Tests...
    mvn clean test -Dtest=CucumberTestNGRunner -Dcucumber.filter.tags="@performance" -Dbrowser=%BROWSER% -Denvironment=%ENVIRONMENT%
) else if "%TEST_TYPE%"=="all" (
    echo Running All Tests...
    mvn clean test -Dtest=CucumberTestNGRunner -Dbrowser=%BROWSER% -Denvironment=%ENVIRONMENT%
) else if "%TEST_TYPE%"=="testng" (
    echo Running TestNG Suite...
    mvn clean test -DsuiteXmlFile=src/test/resources/testng.xml -Dbrowser=%BROWSER% -Denvironment=%ENVIRONMENT%
) else (
    echo Invalid test type: %TEST_TYPE%
    echo Valid options: smoke, regression, api, ui, mobile, performance, all, testng
    exit /b 1
)

REM Check test results
if %ERRORLEVEL% neq 0 (
    echo.
    echo ❌ Tests FAILED!
    echo Check the logs and reports for details.
    exit /b 1
) else (
    echo.
    echo ✅ Tests COMPLETED!
)

REM Generate Allure report if available
if exist target\allure-results (
    echo.
    echo Generating Allure report...
    allure generate target\allure-results -o target\allure-report --clean
    echo Allure report generated: target\allure-report\index.html
)

REM Display report locations
echo.
echo ====================================
echo Test Reports Available:
echo ====================================
if exist target\cucumber-reports\cucumber-html-report\index.html (
    echo 📊 Cucumber HTML Report: target\cucumber-reports\cucumber-html-report\index.html
)
if exist target\allure-report\index.html (
    echo 📈 Allure Report: target\allure-report\index.html
)
if exist target\surefire-reports (
    echo 📋 TestNG Reports: target\surefire-reports\
)
echo.

echo ====================================
echo Test execution completed!
echo ====================================
pause
