@ui @regression
Feature: Sample UI Testing with Playwright
  As a test automation engineer
  I want to verify the UI functionality of web applications
  So that I can ensure the application works correctly for end users

  Background:
    Given I am on the application homepage

  @smoke @critical
  Scenario: Verify homepage loads successfully
    When the page loads completely
    Then I should see the page title contains "Playwright"
    And I should see the main heading
    And I take a screenshot "homepage_loaded"

  @navigation @normal
  Scenario: Navigate to documentation page
    When I click on the "Docs" link
    Then I should be redirected to the documentation page
    And the URL should contain "docs/intro"
    And I should see the installation guide
    And I take a screenshot "docs_page"

  @search @normal
  Scenario: Search for specific content
    Given I am on the documentation page
    When I open the search functionality
    And I search for "locators"
    Then I should see search results
    And I click on the "Locators" result
    Then I should be on the locators documentation page
    And I take a screenshot "search_results"

  @form @critical
  Scenario: Login with valid credentials
    Given I navigate to the login page
    When I enter username "tomsmith"
    And I enter password "SuperSecretPassword!"
    And I click the login button
    Then I should see a success message
    And I should be logged in successfully
    And I take a screenshot "successful_login"

  @form @negative
  Scenario: Login with invalid credentials
    Given I navigate to the login page
    When I enter username "invaliduser"
    And I enter password "wrongpassword"
    And I click the login button
    Then I should see an error message
    And I should remain on the login page
    And I take a screenshot "failed_login"

  @responsive @normal
  Scenario: Verify mobile responsiveness
    Given I set the viewport to mobile size
    When I navigate to the homepage
    Then the page should be displayed correctly on mobile
    And I should see the mobile navigation menu
    When I click the mobile menu toggle
    Then the navigation menu should expand
    And I take a screenshot "mobile_view"

  @accessibility @normal
  Scenario: Verify keyboard navigation
    Given I am on the homepage
    When I use tab key to navigate
    Then I should be able to focus on interactive elements
    And the focus should be visible
    And I take a screenshot "keyboard_navigation"

  @error @minor
  Scenario: Handle invalid URL gracefully
    When I navigate to an invalid URL "/nonexistent-page"
    Then I should see a 404 error page
    Or I should see a "Page not found" message
    And I take a screenshot "error_page"

  @data-driven
  Scenario Outline: Test form with multiple data sets
    Given I navigate to the login page
    When I enter username "<username>"
    And I enter password "<password>"
    And I click the login button
    Then I should see "<expected_result>"
    And I take a screenshot "<screenshot_name>"

    Examples:
      | username    | password              | expected_result | screenshot_name    |
      | tomsmith    | SuperSecretPassword!  | success message | valid_login        |
      | invaliduser | wrongpassword         | error message   | invalid_login      |
      | ""          | SuperSecretPassword!  | error message   | empty_username     |
      | tomsmith    | ""                    | error message   | empty_password     |
