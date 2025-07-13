@echo off
echo ===============================================
echo   AI Plugin Manager Demo - Playwright Java
echo ===============================================
echo.

echo Running AI-Enhanced Tests...
echo.

REM Run the AI Enhanced Tests
mvn test -Dtest=AIEnhancedTests -Dquiet

echo.
echo ===============================================
echo Demo completed! Check the console output above
echo for AI plugin execution results.
echo ===============================================
echo.

REM Run usage examples
echo Running AI Plugin Usage Examples...
mvn test -Dtest=AIPluginUsageExamples -Dquiet

echo.
echo ===============================================
echo All AI Plugin demonstrations completed!
echo ===============================================

pause
