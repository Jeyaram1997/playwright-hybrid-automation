@mobile @regression
Feature: Sample Mobile Application Testing
  As a mobile test automation engineer
  I want to verify the mobile application functionality
  So that I can ensure the app works correctly on mobile devices

  Background:
    Given I launch the mobile application

  @smoke @critical
  Scenario: Launch mobile application successfully
    Then the application should start without crashes
    And I should see the main screen
    And the app logo should be visible
    And I take a mobile screenshot "app_launch"

  @login @critical
  Scenario: Login with valid credentials on mobile
    Given I am on the login screen
    When I enter username "testuser@example.com"
    And I enter password "password123"
    And I tap the login button
    Then I should see the dashboard screen
    And I should see the welcome message
    And I take a mobile screenshot "successful_mobile_login"

  @login @negative
  Scenario: Login with invalid credentials on mobile
    Given I am on the login screen
    When I enter username "invalid@example.com"
    And I enter password "wrongpassword"
    And I tap the login button
    Then I should see an error message
    And I should remain on the login screen
    And I take a mobile screenshot "failed_mobile_login"

  @gestures @normal
  Scenario: Test mobile gestures
    Given I am on the main screen
    When I swipe down to refresh
    Then the content should be refreshed
    When I swipe left to navigate
    Then I should see the next screen
    When I pinch to zoom
    Then the content should be zoomed
    And I take a mobile screenshot "gesture_interactions"

  @orientation @normal
  Scenario: Test device orientation changes
    Given I am on the main screen in portrait mode
    When I rotate the device to landscape
    Then the layout should adapt to landscape mode
    And all elements should be visible
    When I rotate back to portrait
    Then the layout should return to portrait mode
    And I take a mobile screenshot "orientation_change"

  @navigation @normal
  Scenario: Navigate through app screens
    Given I am logged into the application
    When I tap on the "Profile" tab
    Then I should see the profile screen
    When I tap on the "Settings" option
    Then I should see the settings screen
    When I tap the back button
    Then I should return to the profile screen
    And I take a mobile screenshot "app_navigation"

  @forms @normal
  Scenario: Fill and submit a mobile form
    Given I am on the contact form screen
    When I enter name "John Doe"
    And I enter email "john.doe@example.com"
    And I enter message "This is a test message from automation"
    And I tap the submit button
    Then I should see a success confirmation
    And the form should be submitted successfully
    And I take a mobile screenshot "form_submission"

  @notifications @normal
  Scenario: Handle push notifications
    Given the application is running
    When I receive a push notification
    Then the notification should be displayed
    When I tap on the notification
    Then I should be taken to the relevant screen
    And I take a mobile screenshot "notification_handling"

  @search @normal
  Scenario: Search functionality on mobile
    Given I am on the search screen
    When I enter search term "automation"
    And I tap the search button
    Then I should see search results
    And the results should contain "automation"
    When I tap on a search result
    Then I should see the detailed view
    And I take a mobile screenshot "mobile_search"

  @offline @normal
  Scenario: Test offline functionality
    Given I am connected to the internet
    And I have some cached data
    When I disconnect from the internet
    Then the app should still function with cached data
    And I should see an offline indicator
    When I reconnect to the internet
    Then the app should sync the data
    And I take a mobile screenshot "offline_mode"

  @permissions @normal
  Scenario: Test app permissions
    Given I launch the app for the first time
    When the app requests camera permission
    Then I should see a permission dialog
    When I grant the camera permission
    Then the app should be able to access the camera
    And I take a mobile screenshot "permissions_granted"

  @biometric @normal
  Scenario: Test biometric authentication
    Given biometric authentication is enabled
    When I attempt to login using biometrics
    Then the biometric prompt should appear
    When I authenticate using fingerprint
    Then I should be logged in successfully
    And I take a mobile screenshot "biometric_auth"

  @deeplink @normal
  Scenario: Test deep linking
    Given the application is installed
    When I open a deep link "myapp://profile/123"
    Then the app should open to the specific profile page
    And the correct profile should be displayed
    And I take a mobile screenshot "deep_link"

  @performance @normal
  Scenario: Monitor mobile app performance
    Given I start performance monitoring
    When I navigate through different screens
    And I perform various actions
    Then the app should respond within acceptable time limits
    And memory usage should be within normal range
    And CPU usage should not spike excessively
    And I capture performance metrics

  @accessibility @normal
  Scenario: Test mobile accessibility features
    Given accessibility features are enabled
    When I navigate using voice commands
    Then the app should respond to voice navigation
    When I use screen reader functionality
    Then all elements should be properly labeled
    And the app should be accessible to users with disabilities
    And I take a mobile screenshot "accessibility_features"

  @data-driven
  Scenario Outline: Test with different device configurations
    Given I am testing on "<device_type>" with "<os_version>"
    When I perform basic app operations
    Then the app should function correctly
    And performance should meet expectations for "<device_type>"
    And I take a mobile screenshot "<device_type>_test"

    Examples:
      | device_type | os_version |
      | phone       | Android 11 |
      | phone       | Android 12 |
      | tablet      | Android 11 |
      | phone       | iOS 15     |
      | tablet      | iOS 15     |
