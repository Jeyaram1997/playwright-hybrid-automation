package com.jeyaram.automation.stepdefs;

import com.jeyaram.automation.base.PlaywrightBase;
import com.jeyaram.automation.page.LoginPage;
import com.jeyaram.automation.utils.ScreenshotUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

/**
 * Step definitions for M2P Fintech Workflow Admin Portal Login Testing
 * 
 * @author Jeyaram K
 * @version 2.0.0
 * @since 2025-01-01
 */
public class UIStepDefinitions {
    
    private static final Logger logger = LoggerFactory.getLogger(UIStepDefinitions.class);
    private ScreenshotUtils screenshotUtils;
    private PlaywrightBase playwrightBase;
    private LoginPage loginPage;
    
    // Security Improvement: Remove hard-coded credentials and use configuration
    // Credentials should be loaded from environment variables or config files
    private final String validUsername;
    private final String validPassword;
    
    public UIStepDefinitions() {
        this.screenshotUtils = new ScreenshotUtils();
        this.playwrightBase = new PlaywrightBase();
        this.loginPage = new LoginPage();
        
        // Load credentials from environment or config - more secure approach
        this.validUsername = System.getenv("TEST_USERNAME") != null ? 
            System.getenv("TEST_USERNAME") : 
            System.getProperty("test.username", "defaultUser");
        this.validPassword = System.getenv("TEST_PASSWORD") != null ? 
            System.getenv("TEST_PASSWORD") : 
            System.getProperty("test.password", "defaultPass");
            
        if ("defaultUser".equals(validUsername) || "defaultPass".equals(validPassword)) {
            logger.warn("Using default test credentials. For security, set TEST_USERNAME and TEST_PASSWORD environment variables.");
        }
    }
    
    // Background Step
    @Given("I navigate to the M2P Fintech Admin Portal login page")
    public void i_navigate_to_the_m2p_fintech_admin_portal_login_page() {
        logger.info("Navigating to M2P Fintech Admin Portal login page");
        try {
            playwrightBase.initializeBrowser("chromium", true, 30000); // headed mode as requested
            loginPage.navigateToLoginPage();
            loginPage.verifyLoginPageLoaded();
            logger.info("Successfully navigated to login page and verified elements");
        } catch (Exception e) {
            logger.error("Failed to navigate to login page", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "login_page_navigation_failed");
            throw new RuntimeException("Failed to navigate to login page", e);
        }
    }
    
    // Positive Login Scenarios
    @When("I enter valid username and password")
    public void i_enter_valid_username_and_password() {
        logger.info("Entering valid credentials");
        try {
            loginPage.enterUsername(validUsername)
                    .enterPassword(validPassword);
            logger.info("Valid credentials entered successfully");
        } catch (Exception e) {
            logger.error("Failed to enter valid credentials", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "credential_entry_failed");
            throw new RuntimeException("Failed to enter credentials", e);
        }
    }
    
    @When("I login using stored encrypted credentials")
    public void i_login_using_stored_encrypted_credentials() {
        logger.info("Logging in using stored encrypted credentials");
        try {
            loginPage.storeCredentialsSecurely(validUsername, validPassword);
            LoginPage result = loginPage.loginWithStoredCredentials();
            Assert.assertNotNull(result, "Login with stored credentials should succeed");
            logger.info("Successfully logged in using stored encrypted credentials");
        } catch (Exception e) {
            logger.error("Failed to login with stored credentials", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "stored_login_failed");
            throw new RuntimeException("Failed to login with stored credentials", e);
        }
    }
    
    @And("I click the login button")
    public void i_click_the_login_button() {
        logger.info("Clicking login button");
        try {
            loginPage.clickLoginButton();
            logger.info("Login button clicked successfully");
        } catch (Exception e) {
            logger.error("Failed to click login button", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "login_button_click_failed");
            throw new RuntimeException("Failed to click login button", e);
        }
    }
    
    // Negative Login Scenarios
    @When("I enter invalid username {string} and valid password")
    public void i_enter_invalid_username_and_valid_password(String invalidUsername) {
        logger.info("Entering invalid username: {}", invalidUsername);
        try {
            loginPage.enterUsername(invalidUsername)
                    .enterPassword(validPassword);
            logger.info("Invalid username and valid password entered");
        } catch (Exception e) {
            logger.error("Failed to enter invalid credentials", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "invalid_credential_entry_failed");
            throw new RuntimeException("Failed to enter invalid credentials", e);
        }
    }
    
    @When("I enter valid username and invalid password {string}")
    public void i_enter_valid_username_and_invalid_password(String invalidPassword) {
        logger.info("Entering valid username and invalid password");
        try {
            loginPage.enterUsername(validUsername)
                    .enterPassword(invalidPassword);
            logger.info("Valid username and invalid password entered");
        } catch (Exception e) {
            logger.error("Failed to enter credentials with invalid password", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "invalid_password_entry_failed");
            throw new RuntimeException("Failed to enter credentials with invalid password", e);
        }
    }
    
    @When("I leave username and password fields empty")
    public void i_leave_username_and_password_fields_empty() {
        logger.info("Leaving username and password fields empty");
        try {
            loginPage.enterUsername("")
                    .enterPassword("");
            logger.info("Empty credentials entered");
        } catch (Exception e) {
            logger.error("Failed to clear credential fields", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "empty_credential_entry_failed");
            throw new RuntimeException("Failed to clear credential fields", e);
        }
    }
    
    // Verification Steps
    @Then("I should be redirected to the dashboard")
    public void i_should_be_redirected_to_the_dashboard() {
        logger.info("Verifying redirection to dashboard");
        try {
            boolean isOnDashboard = loginPage.isOnDashboard();
            Assert.assertTrue(isOnDashboard, "Should be redirected to dashboard after login");
            logger.info("Successfully verified redirection to dashboard");
        } catch (Exception e) {
            logger.error("Dashboard redirection verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "dashboard_verification_failed");
            throw new RuntimeException("Dashboard redirection verification failed", e);
        }
    }
    
    @And("I should see the user profile section")
    public void i_should_see_the_user_profile_section() {
        logger.info("Verifying user profile section visibility");
        try {
            boolean profileVisible = loginPage.isUserProfileVisible();
            Assert.assertTrue(profileVisible, "User profile section should be visible");
            logger.info("User profile section is visible");
        } catch (Exception e) {
            logger.error("User profile verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "profile_verification_failed");
            throw new RuntimeException("User profile verification failed", e);
        }
    }
    
    @And("the login should be successful")
    public void the_login_should_be_successful() {
        logger.info("Verifying login success");
        try {
            boolean loginSuccess = loginPage.isLoginSuccessful();
            Assert.assertTrue(loginSuccess, "Login should be successful");
            logger.info("Login verification successful");
        } catch (Exception e) {
            logger.error("Login success verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "login_success_verification_failed");
            throw new RuntimeException("Login success verification failed", e);
        }
    }
    
    @Then("I should see an error message")
    public void i_should_see_an_error_message() {
        logger.info("Verifying error message visibility");
        try {
            boolean errorVisible = loginPage.isErrorMessageVisible();
            Assert.assertTrue(errorVisible, "Error message should be visible for invalid credentials");
            logger.info("Error message is visible as expected");
        } catch (Exception e) {
            logger.error("Error message verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "error_message_verification_failed");
            throw new RuntimeException("Error message verification failed", e);
        }
    }
    
    @And("I should remain on the login page")
    public void i_should_remain_on_the_login_page() {
        logger.info("Verifying user remains on login page");
        try {
            boolean onLoginPage = loginPage.isOnLoginPage();
            Assert.assertTrue(onLoginPage, "Should remain on login page for failed login");
            logger.info("User correctly remains on login page");
        } catch (Exception e) {
            logger.error("Login page verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "login_page_verification_failed");
            throw new RuntimeException("Login page verification failed", e);
        }
    }
    
    @Then("I should see validation error messages")
    public void i_should_see_validation_error_messages() {
        logger.info("Verifying validation error messages");
        try {
            boolean validationErrors = loginPage.hasValidationErrors();
            Assert.assertTrue(validationErrors, "Validation errors should be visible for empty fields");
            logger.info("Validation error messages are visible");
        } catch (Exception e) {
            logger.error("Validation error verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "validation_error_verification_failed");
            throw new RuntimeException("Validation error verification failed", e);
        }
    }
    
    // UI Element Verification Steps
    @Then("I should see the username field")
    public void i_should_see_the_username_field() {
        logger.info("Verifying username field visibility");
        try {
            boolean usernameVisible = loginPage.isUsernameFieldVisible();
            Assert.assertTrue(usernameVisible, "Username field should be visible");
            logger.info("Username field is visible");
        } catch (Exception e) {
            logger.error("Username field verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "username_field_verification_failed");
            throw new RuntimeException("Username field verification failed", e);
        }
    }
    
    @And("I should see the password field")
    public void i_should_see_the_password_field() {
        logger.info("Verifying password field visibility");
        try {
            boolean passwordVisible = loginPage.isPasswordFieldVisible();
            Assert.assertTrue(passwordVisible, "Password field should be visible");
            logger.info("Password field is visible");
        } catch (Exception e) {
            logger.error("Password field verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "password_field_verification_failed");
            throw new RuntimeException("Password field verification failed", e);
        }
    }
    
    @And("I should see the login button")
    public void i_should_see_the_login_button() {
        logger.info("Verifying login button visibility");
        try {
            boolean loginButtonVisible = loginPage.isLoginButtonVisible();
            Assert.assertTrue(loginButtonVisible, "Login button should be visible");
            logger.info("Login button is visible");
        } catch (Exception e) {
            logger.error("Login button verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "login_button_verification_failed");
            throw new RuntimeException("Login button verification failed", e);
        }
    }
    
    @And("the login page should be properly loaded")
    public void the_login_page_should_be_properly_loaded() {
        logger.info("Verifying login page is properly loaded");
        try {
            loginPage.verifyLoginPageLoaded();
            logger.info("Login page is properly loaded");
        } catch (Exception e) {
            logger.error("Login page load verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "login_page_load_verification_failed");
            throw new RuntimeException("Login page load verification failed", e);
        }
    }
    
    // Security and Functionality Steps
    @When("I enter password {string} in the password field")
    public void i_enter_password_in_the_password_field(String password) {
        logger.info("Entering password in password field");
        try {
            loginPage.enterPassword(password);
            logger.info("Password entered in password field");
        } catch (Exception e) {
            logger.error("Failed to enter password", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "password_entry_failed");
            throw new RuntimeException("Failed to enter password", e);
        }
    }
    
    @Then("the password field should mask the input")
    public void the_password_field_should_mask_the_input() {
        logger.info("Verifying password field masks input");
        try {
            boolean isPasswordMasked = loginPage.isPasswordFieldMasked();
            Assert.assertTrue(isPasswordMasked, "Password field should mask the input");
            logger.info("Password field correctly masks input");
        } catch (Exception e) {
            logger.error("Password masking verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "password_masking_verification_failed");
            throw new RuntimeException("Password masking verification failed", e);
        }
    }
    
    @And("the password should not be visible in plain text")
    public void the_password_should_not_be_visible_in_plain_text() {
        logger.info("Verifying password is not visible in plain text");
        try {
            boolean isPasswordHidden = loginPage.isPasswordHidden();
            Assert.assertTrue(isPasswordHidden, "Password should not be visible in plain text");
            logger.info("Password is properly hidden");
        } catch (Exception e) {
            logger.error("Password visibility verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "password_visibility_verification_failed");
            throw new RuntimeException("Password visibility verification failed", e);
        }
    }
    
    @When("I check the Remember Me checkbox")
    public void i_check_the_remember_me_checkbox() {
        logger.info("Checking Remember Me checkbox");
        try {
            loginPage.checkRememberMe();
            logger.info("Remember Me checkbox checked");
        } catch (Exception e) {
            logger.error("Failed to check Remember Me checkbox", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "remember_me_check_failed");
            throw new RuntimeException("Failed to check Remember Me checkbox", e);
        }
    }
    
    @And("the credentials should be remembered for next session")
    public void the_credentials_should_be_remembered_for_next_session() {
        logger.info("Verifying credentials are remembered");
        try {
            boolean credentialsRemembered = loginPage.areCredentialsRemembered();
            Assert.assertTrue(credentialsRemembered, "Credentials should be remembered for next session");
            logger.info("Credentials are properly remembered");
        } catch (Exception e) {
            logger.error("Credentials remember verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "credentials_remember_verification_failed");
            throw new RuntimeException("Credentials remember verification failed", e);
        }
    }
    
    @When("I click on the Forgot Password link")
    public void i_click_on_the_forgot_password_link() {
        logger.info("Clicking Forgot Password link");
        try {
            loginPage.clickForgotPassword();
            logger.info("Forgot Password link clicked");
        } catch (Exception e) {
            logger.error("Failed to click Forgot Password link", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "forgot_password_click_failed");
            throw new RuntimeException("Failed to click Forgot Password link", e);
        }
    }
    
    @Then("I should be redirected to the password reset page")
    public void i_should_be_redirected_to_the_password_reset_page() {
        logger.info("Verifying redirection to password reset page");
        try {
            boolean onResetPage = loginPage.isOnPasswordResetPage();
            Assert.assertTrue(onResetPage, "Should be redirected to password reset page");
            logger.info("Successfully redirected to password reset page");
        } catch (Exception e) {
            logger.error("Password reset page verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "password_reset_verification_failed");
            throw new RuntimeException("Password reset page verification failed", e);
        }
    }
    
    // Performance and Responsive Steps
    @Then("the login page should load within {int} seconds")
    public void the_login_page_should_load_within_seconds(int seconds) {
        logger.info("Verifying login page loads within {} seconds", seconds);
        try {
            boolean loadedInTime = loginPage.isPageLoadedWithinTime(seconds * 1000);
            Assert.assertTrue(loadedInTime, "Login page should load within " + seconds + " seconds");
            logger.info("Login page loaded within specified time");
        } catch (Exception e) {
            logger.error("Page load time verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "page_load_time_verification_failed");
            throw new RuntimeException("Page load time verification failed", e);
        }
    }
    
    @And("all login elements should be visible within {int} seconds")
    public void all_login_elements_should_be_visible_within_seconds(int seconds) {
        logger.info("Verifying all login elements are visible within {} seconds", seconds);
        try {
            boolean elementsVisibleInTime = loginPage.areAllElementsVisibleWithinTime(seconds * 1000);
            Assert.assertTrue(elementsVisibleInTime, "All login elements should be visible within " + seconds + " seconds");
            logger.info("All login elements are visible within specified time");
        } catch (Exception e) {
            logger.error("Element visibility time verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "element_visibility_time_verification_failed");
            throw new RuntimeException("Element visibility time verification failed", e);
        }
    }
    
    // Logout and Session Steps
    @Given("I am logged in to the portal")
    public void i_am_logged_in_to_the_portal() {
        logger.info("Ensuring user is logged in to the portal");
        try {
            i_navigate_to_the_m2p_fintech_admin_portal_login_page();
            i_enter_valid_username_and_password();
            i_click_the_login_button();
            the_login_should_be_successful();
            logger.info("User is successfully logged in to the portal");
        } catch (Exception e) {
            logger.error("Failed to login to portal", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "portal_login_failed");
            throw new RuntimeException("Failed to login to portal", e);
        }
    }
    
    @When("I click the logout button")
    public void i_click_the_logout_button() {
        logger.info("Clicking logout button");
        try {
            loginPage.logout();
            logger.info("Logout button clicked");
        } catch (Exception e) {
            logger.error("Failed to click logout button", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "logout_click_failed");
            throw new RuntimeException("Failed to click logout button", e);
        }
    }
    
    @And("the session should be terminated")
    public void the_session_should_be_terminated() {
        logger.info("Verifying session is terminated");
        try {
            boolean sessionTerminated = loginPage.isSessionTerminated();
            Assert.assertTrue(sessionTerminated, "Session should be terminated after logout");
            logger.info("Session is properly terminated");
        } catch (Exception e) {
            logger.error("Session termination verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "session_termination_verification_failed");
            throw new RuntimeException("Session termination verification failed", e);
        }
    }
    
    // Additional verification steps for comprehensive testing
    @Then("the login form should be accessible")
    public void the_login_form_should_be_accessible() {
        logger.info("Verifying login form accessibility");
        try {
            boolean isAccessible = loginPage.isFormAccessible();
            Assert.assertTrue(isAccessible, "Login form should be accessible");
            logger.info("Login form is accessible");
        } catch (Exception e) {
            logger.error("Form accessibility verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "form_accessibility_verification_failed");
            throw new RuntimeException("Form accessibility verification failed", e);
        }
    }
    
    @And("all form fields should have proper labels")
    public void all_form_fields_should_have_proper_labels() {
        logger.info("Verifying form fields have proper labels");
        try {
            boolean hasProperLabels = loginPage.hasProperLabels();
            Assert.assertTrue(hasProperLabels, "All form fields should have proper labels");
            logger.info("All form fields have proper labels");
        } catch (Exception e) {
            logger.error("Form labels verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "form_labels_verification_failed");
            throw new RuntimeException("Form labels verification failed", e);
        }
    }
    
    @And("the form should support keyboard navigation")
    public void the_form_should_support_keyboard_navigation() {
        logger.info("Verifying form supports keyboard navigation");
        try {
            boolean supportsKeyboardNav = loginPage.supportsKeyboardNavigation();
            Assert.assertTrue(supportsKeyboardNav, "Form should support keyboard navigation");
            logger.info("Form supports keyboard navigation");
        } catch (Exception e) {
            logger.error("Keyboard navigation verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "keyboard_navigation_verification_failed");
            throw new RuntimeException("Keyboard navigation verification failed", e);
        }
    }
    
    @When("I resize the browser to mobile view")
    public void i_resize_the_browser_to_mobile_view() {
        logger.info("Resizing browser to mobile view");
        try {
            loginPage.resizeToMobileView();
            logger.info("Browser resized to mobile view");
        } catch (Exception e) {
            logger.error("Failed to resize browser to mobile view", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "mobile_resize_failed");
            throw new RuntimeException("Failed to resize browser to mobile view", e);
        }
    }
    
    @Then("the login form should be responsive")
    public void the_login_form_should_be_responsive() {
        logger.info("Verifying login form is responsive");
        try {
            boolean isResponsive = loginPage.isFormResponsive();
            Assert.assertTrue(isResponsive, "Login form should be responsive");
            logger.info("Login form is responsive");
        } catch (Exception e) {
            logger.error("Form responsiveness verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "form_responsiveness_verification_failed");
            throw new RuntimeException("Form responsiveness verification failed", e);
        }
    }
    
    @And("all elements should be properly aligned")
    public void all_elements_should_be_properly_aligned() {
        logger.info("Verifying all elements are properly aligned");
        try {
            boolean properlyAligned = loginPage.areElementsProperlyAligned();
            Assert.assertTrue(properlyAligned, "All elements should be properly aligned");
            logger.info("All elements are properly aligned");
        } catch (Exception e) {
            logger.error("Element alignment verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "element_alignment_verification_failed");
            throw new RuntimeException("Element alignment verification failed", e);
        }
    }
    
    @When("I enter username {string} and password {string}")
    public void i_enter_username_and_password(String username, String password) {
        logger.info("Entering username: {} and password", username);
        try {
            loginPage.enterUsername(username)
                    .enterPassword(password);
            logger.info("Username and password entered successfully");
        } catch (Exception e) {
            logger.error("Failed to enter username and password", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "credential_entry_failed");
            throw new RuntimeException("Failed to enter username and password", e);
        }
    }
    
    @Then("the credentials should be encrypted before transmission")
    public void the_credentials_should_be_encrypted_before_transmission() {
        logger.info("Verifying credentials are encrypted before transmission");
        try {
            boolean credentialsEncrypted = loginPage.areCredentialsEncrypted();
            Assert.assertTrue(credentialsEncrypted, "Credentials should be encrypted before transmission");
            logger.info("Credentials are properly encrypted");
        } catch (Exception e) {
            logger.error("Credentials encryption verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "credentials_encryption_verification_failed");
            throw new RuntimeException("Credentials encryption verification failed", e);
        }
    }
    
    @And("the encrypted data should be logged for security audit")
    public void the_encrypted_data_should_be_logged_for_security_audit() {
        logger.info("Verifying encrypted data is logged for security audit");
        try {
            boolean encryptedDataLogged = loginPage.isEncryptedDataLogged();
            Assert.assertTrue(encryptedDataLogged, "Encrypted data should be logged for security audit");
            logger.info("Encrypted data is properly logged for security audit");
        } catch (Exception e) {
            logger.error("Encrypted data logging verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "encrypted_data_logging_verification_failed");
            throw new RuntimeException("Encrypted data logging verification failed", e);
        }
    }
    
    @When("the session expires")
    public void the_session_expires() {
        logger.info("Simulating session expiration");
        try {
            loginPage.simulateSessionExpiry();
            logger.info("Session expiration simulated");
        } catch (Exception e) {
            logger.error("Failed to simulate session expiration", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "session_expiry_simulation_failed");
            throw new RuntimeException("Failed to simulate session expiration", e);
        }
    }
    
    @And("I should see a session timeout message")
    public void i_should_see_a_session_timeout_message() {
        logger.info("Verifying session timeout message is visible");
        try {
            boolean timeoutMessageVisible = loginPage.isSessionTimeoutMessageVisible();
            Assert.assertTrue(timeoutMessageVisible, "Session timeout message should be visible");
            logger.info("Session timeout message is visible");
        } catch (Exception e) {
            logger.error("Session timeout message verification failed", e);
            screenshotUtils.captureScreenshot(playwrightBase.getPage(), "session_timeout_message_verification_failed");
            throw new RuntimeException("Session timeout message verification failed", e);
        }
    }
}
