@performance @regression
Feature: Performance Testing Scenarios
  As a performance test engineer
  I want to verify the application performance under various conditions
  So that I can ensure the application meets performance requirements

  Background:
    Given I initialize performance monitoring

  @load @critical
  Scenario: Page load performance test
    Given I start measuring page load time
    When I navigate to the homepage
    Then the page should load within 3 seconds
    And the DOM content should be loaded within 2 seconds
    And all resources should be loaded within 5 seconds
    And I generate a performance report "page_load_test"

  @api @critical
  Scenario: API response time performance
    Given I start measuring API response time
    When I send a GET request to "/posts"
    Then the API should respond within 2 seconds
    And the response size should be reasonable
    When I send 10 concurrent requests to "/posts"
    Then all requests should complete within 5 seconds
    And I generate a performance report "api_performance_test"

  @stress @normal
  Scenario: Stress test with multiple concurrent users
    Given I simulate 50 concurrent users
    When each user navigates to different pages
    And each user performs various actions
    Then the server should handle the load gracefully
    And response times should remain acceptable
    And no errors should occur
    And I generate a performance report "stress_test"

  @memory @normal
  Scenario: Memory usage monitoring
    Given I start memory monitoring
    When I perform memory-intensive operations
    And I navigate through multiple pages
    Then memory usage should not exceed 512MB
    And there should be no memory leaks
    And garbage collection should be efficient
    And I generate a performance report "memory_test"

  @network @normal
  Scenario: Network performance testing
    Given I simulate different network conditions
    When I test on "3G" network speed
    Then the application should still be usable
    And critical features should work
    When I test on "slow 3G" network speed
    Then the application should show loading indicators
    And provide graceful degradation
    And I generate a performance report "network_test"

  @database @normal
  Scenario: Database performance testing
    Given I start database performance monitoring
    When I perform 100 database queries
    Then each query should complete within 100ms
    And the database connection pool should be efficient
    When I perform complex queries
    Then optimization should be in place
    And I generate a performance report "database_test"

  @frontend @normal
  Scenario: Frontend performance metrics
    Given I enable performance monitoring
    When I navigate to the application
    Then the First Contentful Paint should be within 1.5 seconds
    And the Largest Contentful Paint should be within 2.5 seconds
    And the Cumulative Layout Shift should be less than 0.1
    And the First Input Delay should be within 100ms
    And I generate a performance report "frontend_metrics"

  @mobile @normal
  Scenario: Mobile performance testing
    Given I set mobile device conditions
    When I test on mobile devices
    Then the application should perform well on mobile
    And battery usage should be optimized
    And data usage should be minimized
    And I generate a performance report "mobile_performance"

  @cache @normal
  Scenario: Caching performance test
    Given I clear all caches
    When I load the page for the first time
    Then I measure the initial load time
    When I reload the same page
    Then the cached load time should be significantly faster
    And static resources should be cached properly
    And I generate a performance report "cache_test"

  @cdn @normal
  Scenario: CDN performance testing
    Given I test from different geographical locations
    When I access the application from "US East"
    Then response times should be optimal
    When I access from "Europe"
    Then CDN should serve content efficiently
    When I access from "Asia"
    Then performance should meet regional requirements
    And I generate a performance report "cdn_test"

  @scalability @normal
  Scenario: Scalability testing
    Given I start with 10 concurrent users
    When I gradually increase to 100 users
    Then the system should scale gracefully
    And response times should degrade linearly
    When I reach 500 users
    Then the system should still be responsive
    Or should fail gracefully with proper error handling
    And I generate a performance report "scalability_test"

  @endurance @normal
  Scenario: Endurance testing
    Given I start long-running performance test
    When I run the test for 2 hours
    And I maintain constant load
    Then performance should remain stable
    And no performance degradation should occur
    And memory usage should be stable
    And I generate a performance report "endurance_test"

  @baseline @critical
  Scenario: Performance baseline establishment
    Given I have clean test environment
    When I run standard performance test suite
    Then I establish performance baselines
    And I record key performance indicators
    And I save baseline metrics for comparison
    And I generate a performance report "baseline_test"

  @regression @normal
  Scenario: Performance regression testing
    Given I have established performance baselines
    When I run current performance tests
    Then current performance should match baselines
    And no regression should be detected
    If regression is found
    Then I should alert the team
    And I generate a performance report "regression_test"

  @monitoring @normal
  Scenario: Real-time performance monitoring
    Given I enable real-time monitoring
    When I perform various user actions
    Then performance metrics should be collected
    And alerts should trigger for threshold breaches
    And dashboards should show real-time data
    And I generate a performance report "monitoring_test"

  @data-driven
  Scenario Outline: Performance test with different loads
    Given I configure "<user_count>" concurrent users
    When I run performance test for "<duration>" minutes
    Then average response time should be less than "<response_time>"ms
    And error rate should be less than "<error_rate>"%
    And I generate a performance report "<test_name>"

    Examples:
      | user_count | duration | response_time | error_rate | test_name           |
      | 10         | 5        | 1000          | 1          | light_load_test     |
      | 50         | 10       | 2000          | 2          | medium_load_test    |
      | 100        | 15       | 3000          | 5          | heavy_load_test     |
      | 200        | 20       | 5000          | 10         | peak_load_test      |
