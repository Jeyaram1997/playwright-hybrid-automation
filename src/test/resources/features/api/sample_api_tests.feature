@api @regression
Feature: Sample API Testing with REST endpoints
  As a test automation engineer
  I want to verify the API functionality of REST services
  So that I can ensure the backend services work correctly

  Background:
    Given I initialize the API client with base URL

  @smoke @critical
  Scenario: Get all posts successfully
    When I send a GET request to "/posts"
    Then the response status code should be 200
    And the response should contain a list of posts
    And each post should have required fields
    And the response time should be less than 2000 milliseconds

  @crud @critical
  Scenario: Create a new post
    Given I have the following post data:
      | title  | Test Post Title                              |
      | body   | Test post body content for automation testing |
      | userId | 1                                            |
    When I send a POST request to "/posts" with the post data
    Then the response status code should be 201
    And the response should contain the created post data
    And the post should have an assigned ID

  @crud @normal
  Scenario: Retrieve a specific post by ID
    Given I know the post ID is 1
    When I send a GET request to "/posts/1"
    Then the response status code should be 200
    And the response should contain the post with ID 1
    And the post should have all required fields
    And the post title should not be empty

  @crud @normal
  Scenario: Update an existing post
    Given I have the following updated post data:
      | id     | 1                        |
      | title  | Updated Test Post Title  |
      | body   | Updated post body content |
      | userId | 1                        |
    When I send a PUT request to "/posts/1" with the updated data
    Then the response status code should be 200
    And the response should contain the updated post data
    And the post title should be "Updated Test Post Title"

  @crud @normal
  Scenario: Partially update a post
    Given I have the following patch data:
      | title | Patched Title Only |
    When I send a PATCH request to "/posts/1" with the patch data
    Then the response status code should be 200
    And the response should contain the updated title
    And other fields should remain unchanged

  @crud @normal
  Scenario: Delete a post
    Given I know the post ID is 1
    When I send a DELETE request to "/posts/1"
    Then the response status code should be 200 or 204
    And the post should be deleted successfully

  @error @minor
  Scenario: Handle invalid endpoint
    When I send a GET request to "/invalid-endpoint"
    Then the response status code should be 404
    And the response should indicate the resource was not found

  @authentication @critical
  Scenario: API request with authentication headers
    Given I set the authentication headers:
      | Authorization | Bearer test-token |
      | X-API-Key     | test-api-key      |
    When I send a GET request to "/posts/1" with authentication
    Then the response status code should be 200
    And the request should be authenticated successfully

  @performance @normal
  Scenario: Verify API response time performance
    When I send a GET request to "/posts"
    Then the response status code should be 200
    And the response time should be less than 2000 milliseconds
    And I record the performance metrics

  @queryparams @normal
  Scenario: API request with query parameters
    Given I set the following query parameters:
      | userId | 1 |
    When I send a GET request to "/posts" with query parameters
    Then the response status code should be 200
    And the response should contain posts
    And the query parameters should be processed correctly

  @schema @normal
  Scenario: Validate JSON response schema
    When I send a GET request to "/posts/1"
    Then the response status code should be 200
    And the response should match the post schema
    And all required fields should be present with correct data types

  @contenttype @minor
  Scenario: Verify content type header
    When I send a GET request to "/posts/1"
    Then the response status code should be 200
    And the content type should be "application/json"
    And the response should be valid JSON

  @data-driven
  Scenario Outline: Test API with multiple endpoints
    When I send a GET request to "<endpoint>"
    Then the response status code should be <status_code>
    And the response should contain "<expected_content>"

    Examples:
      | endpoint | status_code | expected_content |
      | /posts   | 200         | posts list       |
      | /posts/1 | 200         | single post      |
      | /users   | 200         | users list       |
      | /users/1 | 200         | single user      |

  @negative
  Scenario Outline: Test API error scenarios
    When I send a GET request to "<endpoint>"
    Then the response status code should be <status_code>
    And the response should indicate an error

    Examples:
      | endpoint      | status_code |
      | /posts/999999 | 404         |
      | /invalid-path | 404         |
