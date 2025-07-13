Feature: M2P Fintech Workflow Admin Portal Login
  As a workflow administrator
  I want to login to the M2P Fintech Admin Portal
  So that I can access the workflow management features

  Background:
    Given I navigate to the M2P Fintech Admin Portal login page

  @login @smoke @positive
  Scenario: Successful login with valid credentials
    When I enter valid username and password
    And I click the login button
    Then I should be redirected to the dashboard
    And I should see the user profile section
    And the login should be successful

  @login @smoke @positive
  Scenario: Login with stored encrypted credentials
    When I login using stored encrypted credentials
    Then I should be redirected to the dashboard
    And the login should be successful

  @login @negative
  Scenario: Failed login with invalid username
    When I enter invalid username "invaliduser@test.com" and valid password
    And I click the login button
    Then I should see an error message
    And I should remain on the login page

  @login @negative
  Scenario: Failed login with invalid password
    When I enter valid username and invalid password "wrongpassword"
    And I click the login button
    Then I should see an error message
    And I should remain on the login page

  @login @negative
  Scenario: Failed login with empty credentials
    When I leave username and password fields empty
    And I click the login button
    Then I should see validation error messages
    And I should remain on the login page

  @login @ui
  Scenario: Verify login page elements are loaded
    Then I should see the username field
    And I should see the password field
    And I should see the login button
    And the login page should be properly loaded

  @login @security
  Scenario: Verify password field is masked
    When I enter password "Test@1234" in the password field
    Then the password field should mask the input
    And the password should not be visible in plain text

  @login @functionality
  Scenario: Remember Me functionality
    When I check the Remember Me checkbox
    And I enter valid username and password
    And I click the login button
    Then I should be redirected to the dashboard
    And the credentials should be remembered for next session

  @login @functionality
  Scenario: Forgot Password link functionality
    When I click on the Forgot Password link
    Then I should be redirected to the password reset page

  @login @accessibility
  Scenario: Login form accessibility
    Then the login form should be accessible
    And all form fields should have proper labels
    And the form should support keyboard navigation

  @login @performance
  Scenario: Login page load performance
    Then the login page should load within 5 seconds
    And all login elements should be visible within 3 seconds

  @login @responsive
  Scenario: Login page responsive design
    When I resize the browser to mobile view
    Then the login form should be responsive
    And all elements should be properly aligned

  @login @security @encryption
  Scenario: Verify credential encryption during login
    When I enter username "jeyaramk" and password "Test@1234"
    Then the credentials should be encrypted before transmission
    And the encrypted data should be logged for security audit

  @login @logout
  Scenario: Successful logout after login
    Given I am logged in to the portal
    When I click the logout button
    Then I should be redirected to the login page
    And the session should be terminated

  @login @session
  Scenario: Session timeout handling
    Given I am logged in to the portal
    When the session expires
    Then I should be redirected to the login page
    And I should see a session timeout message
