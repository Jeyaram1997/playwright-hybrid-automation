# M2P Fintech Workflow Admin Portal - Login Test Execution Script (Windows)
# Framework: Playwright Java + Cucumber + TestNG
# Author: Jeyaram K
# Version: 1.0.0

Write-Host "🥒 M2P Fintech Login Test Automation" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green
Write-Host "Framework: Playwright Java + Cucumber + TestNG" -ForegroundColor Yellow
Write-Host "Target: https://uat-workflow.m2pfintech.dev/admin/" -ForegroundColor Yellow
Write-Host "Test Type: Login Functionality with Security Features" -ForegroundColor Yellow
Write-Host ""

# Set environment variables
$env:BROWSER = "chromium"
$env:HEADED = "true"
$env:ENVIRONMENT = "uat"
$env:APP_BASE_URL = "https://uat-workflow.m2pfintech.dev/admin/"
$env:TIMEOUT = "30000"

Write-Host "🔧 Configuration:" -ForegroundColor Cyan
Write-Host "  Browser: $($env:BROWSER) (Headed Mode)" -ForegroundColor White
Write-Host "  Environment: $($env:ENVIRONMENT)" -ForegroundColor White
Write-Host "  URL: $($env:APP_BASE_URL)" -ForegroundColor White
Write-Host "  Timeout: $($env:TIMEOUT)ms" -ForegroundColor White
Write-Host ""

Write-Host "🚀 Starting Login Test Execution..." -ForegroundColor Green

# Run all login tests
Write-Host "📋 Running Complete Login Test Suite..." -ForegroundColor Yellow
mvn clean test `
  -D"test=CucumberTestNGRunner" `
  -D"cucumber.filter.tags=@login" `
  -D"BROWSER=$($env:BROWSER)" `
  -D"HEADED=$($env:HEADED)" `
  -D"ENVIRONMENT=$($env:ENVIRONMENT)" `
  -D"APP_BASE_URL=$($env:APP_BASE_URL)" `
  -D"email.notifications.enabled=true" `
  -D"jira.bug.creation.enabled=false" `
  -D"allure.results.directory=target/allure-results"

Write-Host ""
Write-Host "📊 Generating Reports..." -ForegroundColor Cyan

# Generate Allure report
if (Test-Path "target/allure-results") {
    Write-Host "🎯 Generating Allure Report..." -ForegroundColor Yellow
    allure generate target/allure-results --clean -o target/allure-report
    Write-Host "✅ Allure Report: target/allure-report/index.html" -ForegroundColor Green
}

# Check Cucumber reports
if (Test-Path "target/cucumber-reports") {
    Write-Host "🥒 Cucumber Reports: target/cucumber-reports/" -ForegroundColor Green
}

Write-Host ""
Write-Host "✅ Login Test Execution Complete!" -ForegroundColor Green
Write-Host ""
Write-Host "📁 Report Locations:" -ForegroundColor Cyan
Write-Host "  • Allure Report: target/allure-report/index.html" -ForegroundColor White
Write-Host "  • Cucumber HTML: target/cucumber-reports/cucumber-html-report/index.html" -ForegroundColor White
Write-Host "  • Cucumber JSON: target/cucumber-reports/cucumber.json" -ForegroundColor White
Write-Host "  • Cucumber XML: target/cucumber-reports/cucumber.xml" -ForegroundColor White
Write-Host ""
Write-Host "🔍 Test Features Covered:" -ForegroundColor Cyan
Write-Host "  ✅ Valid/Invalid Login Scenarios" -ForegroundColor Green
Write-Host "  ✅ UI Element Validation" -ForegroundColor Green
Write-Host "  ✅ Security Testing (Encryption, Masking)" -ForegroundColor Green
Write-Host "  ✅ Accessibility Testing" -ForegroundColor Green
Write-Host "  ✅ Performance Testing" -ForegroundColor Green
Write-Host "  ✅ Responsive Design Testing" -ForegroundColor Green
Write-Host "  ✅ Session Management" -ForegroundColor Green
Write-Host "  ✅ Remember Me Functionality" -ForegroundColor Green
Write-Host "  ✅ Forgot Password Flow" -ForegroundColor Green
Write-Host "  ✅ Logout Functionality" -ForegroundColor Green
Write-Host ""
Write-Host "📧 Email notifications will be sent if configured." -ForegroundColor Yellow
Write-Host "🎉 Happy Testing with M2P Fintech Automation!" -ForegroundColor Magenta

# Open reports if they exist
if (Test-Path "target/allure-report/index.html") {
    $openReport = Read-Host "Would you like to open the Allure report? (y/n)"
    if ($openReport -eq "y" -or $openReport -eq "Y") {
        Start-Process "target/allure-report/index.html"
    }
}
