#!/bin/bash

# M2P Fintech Workflow Admin Portal - Login Test Execution Script
# Framework: Playwright Java + Cucumber + TestNG
# Author: Jeyaram K
# Version: 1.0.0

echo "🥒 M2P Fintech Login Test Automation"
echo "====================================="
echo "Framework: Playwright Java + Cucumber + TestNG"
echo "Target: https://uat-workflow.m2pfintech.dev/admin/"
echo "Test Type: Login Functionality with Security Features"
echo ""

# Set environment variables
export BROWSER=chromium
export HEADED=true
export ENVIRONMENT=uat
export APP_BASE_URL=https://uat-workflow.m2pfintech.dev/admin/
export TIMEOUT=30000

echo "🔧 Configuration:"
echo "  Browser: $BROWSER (Headed Mode)"
echo "  Environment: $ENVIRONMENT"
echo "  URL: $APP_BASE_URL"
echo "  Timeout: ${TIMEOUT}ms"
echo ""

echo "🚀 Starting Login Test Execution..."

# Run all login tests
echo "📋 Running Complete Login Test Suite..."
mvn clean test \
  -Dtest=CucumberTestNGRunner \
  -Dcucumber.filter.tags="@login" \
  -DBROWSER=$BROWSER \
  -DHEADED=$HEADED \
  -DENVIRONMENT=$ENVIRONMENT \
  -DAPP_BASE_URL=$APP_BASE_URL \
  -Demail.notifications.enabled=true \
  -Djira.bug.creation.enabled=false \
  -Dallure.results.directory=target/allure-results

echo ""
echo "📊 Generating Reports..."

# Generate Allure report
if [ -d "target/allure-results" ]; then
    echo "🎯 Generating Allure Report..."
    allure generate target/allure-results --clean -o target/allure-report
    echo "✅ Allure Report: target/allure-report/index.html"
fi

# Check Cucumber reports
if [ -d "target/cucumber-reports" ]; then
    echo "🥒 Cucumber Reports: target/cucumber-reports/"
fi

echo ""
echo "✅ Login Test Execution Complete!"
echo ""
echo "📁 Report Locations:"
echo "  • Allure Report: target/allure-report/index.html"
echo "  • Cucumber HTML: target/cucumber-reports/cucumber-html-report/index.html"
echo "  • Cucumber JSON: target/cucumber-reports/cucumber.json"
echo "  • Cucumber XML: target/cucumber-reports/cucumber.xml"
echo ""
echo "🔍 Test Features Covered:"
echo "  ✅ Valid/Invalid Login Scenarios"
echo "  ✅ UI Element Validation"
echo "  ✅ Security Testing (Encryption, Masking)"
echo "  ✅ Accessibility Testing"
echo "  ✅ Performance Testing"
echo "  ✅ Responsive Design Testing"
echo "  ✅ Session Management"
echo "  ✅ Remember Me Functionality"
echo "  ✅ Forgot Password Flow"
echo "  ✅ Logout Functionality"
echo ""
echo "📧 Email notifications will be sent if configured."
echo "🎉 Happy Testing with M2P Fintech Automation!"
