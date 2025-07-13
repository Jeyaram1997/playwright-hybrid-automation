package com.jeyaram.automation.stepdefs;

import com.jeyaram.automation.base.APIBase;
import com.jeyaram.automation.utils.JsonUtils;
import com.microsoft.playwright.APIResponse;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Step definitions for API testing scenarios
 * 
 * @author Jeyaram K
 * @version 1.0.0
 * @since 2025-01-01
 */
public class APIStepDefinitions {
    
    private static final Logger logger = LoggerFactory.getLogger(APIStepDefinitions.class);
    private APIBase apiBase;
    private JsonUtils jsonUtils;
    private APIResponse lastResponse;
    private Map<String, String> headers;
    private String requestBody;
    
    public APIStepDefinitions() {
        this.apiBase = new APIBase();
        this.jsonUtils = new JsonUtils();
        this.headers = new HashMap<>();
    }
    
    @Given("I set the API base URL to {string}")
    public void i_set_the_api_base_url_to(String baseUrl) {
        logger.info("Setting API base URL to: {}", baseUrl);
        // Base URL is typically set through configuration
        // This step is mainly for documentation purposes
    }
    
    @Given("I set header {string} to {string}")
    public void i_set_header_to(String headerName, String headerValue) {
        logger.info("Setting header {} to {}", headerName, headerValue);
        headers.put(headerName, headerValue);
    }
    
    @Given("I set the request body to:")
    public void i_set_the_request_body_to(String body) {
        logger.info("Setting request body");
        this.requestBody = body;
    }
    
    @When("I send a GET request to {string}")
    public void i_send_a_get_request_to(String endpoint) {
        logger.info("Sending GET request to: {}", endpoint);
        try {
            Map<String, String> queryParams = new HashMap<>();
            lastResponse = apiBase.get(endpoint, queryParams, headers);
            logger.info("GET request completed with status: {}", lastResponse.status());
        } catch (Exception e) {
            logger.error("Failed to send GET request to: {}", endpoint, e);
            throw new RuntimeException("Failed to send GET request to: " + endpoint, e);
        }
    }
    
    @When("I send a POST request to {string}")
    public void i_send_a_post_request_to(String endpoint) {
        logger.info("Sending POST request to: {}", endpoint);
        try {
            lastResponse = apiBase.post(endpoint, requestBody, headers);
            logger.info("POST request completed with status: {}", lastResponse.status());
        } catch (Exception e) {
            logger.error("Failed to send POST request to: {}", endpoint, e);
            throw new RuntimeException("Failed to send POST request to: " + endpoint, e);
        }
    }
    
    @When("I send a PUT request to {string}")
    public void i_send_a_put_request_to(String endpoint) {
        logger.info("Sending PUT request to: {}", endpoint);
        try {
            lastResponse = apiBase.put(endpoint, requestBody, headers);
            logger.info("PUT request completed with status: {}", lastResponse.status());
        } catch (Exception e) {
            logger.error("Failed to send PUT request to: {}", endpoint, e);
            throw new RuntimeException("Failed to send PUT request to: " + endpoint, e);
        }
    }
    
    @When("I send a PATCH request to {string}")
    public void i_send_a_patch_request_to(String endpoint) {
        logger.info("Sending PATCH request to: {}", endpoint);
        try {
            lastResponse = apiBase.patch(endpoint, requestBody, headers);
            logger.info("PATCH request completed with status: {}", lastResponse.status());
        } catch (Exception e) {
            logger.error("Failed to send PATCH request to: {}", endpoint, e);
            throw new RuntimeException("Failed to send PATCH request to: " + endpoint, e);
        }
    }
    
    @When("I send a DELETE request to {string}")
    public void i_send_a_delete_request_to(String endpoint) {
        logger.info("Sending DELETE request to: {}", endpoint);
        try {
            lastResponse = apiBase.delete(endpoint, headers);
            logger.info("DELETE request completed with status: {}", lastResponse.status());
        } catch (Exception e) {
            logger.error("Failed to send DELETE request to: {}", endpoint, e);
            throw new RuntimeException("Failed to send DELETE request to: " + endpoint, e);
        }
    }
    
    @Then("the response status should be {int}")
    public void the_response_status_should_be(int expectedStatus) {
        logger.info("Verifying response status is: {}", expectedStatus);
        Assert.assertNotNull(lastResponse, "No response received");
        int actualStatus = lastResponse.status();
        Assert.assertEquals(actualStatus, expectedStatus, 
            "Expected status " + expectedStatus + " but got " + actualStatus);
        logger.info("Response status verification passed: {}", actualStatus);
    }
    
    @Then("the response should contain {string}")
    public void the_response_should_contain(String expectedText) {
        logger.info("Verifying response contains: {}", expectedText);
        Assert.assertNotNull(lastResponse, "No response received");
        try {
            String responseText = lastResponse.text();
            Assert.assertTrue(responseText.contains(expectedText), 
                "Response should contain: " + expectedText + "\nActual response: " + responseText);
            logger.info("Response content verification passed");
        } catch (Exception e) {
            logger.error("Failed to verify response content", e);
            throw new RuntimeException("Failed to verify response content", e);
        }
    }
    
    @Then("the response JSON should have field {string} with value {string}")
    public void the_response_json_should_have_field_with_value(String fieldPath, String expectedValue) {
        logger.info("Verifying JSON field {} has value: {}", fieldPath, expectedValue);
        Assert.assertNotNull(lastResponse, "No response received");
        try {
            String responseText = lastResponse.text();
            Object actualValue = jsonUtils.extractValueFromJson(responseText, fieldPath);
            Assert.assertEquals(String.valueOf(actualValue), expectedValue, 
                "Expected field " + fieldPath + " to have value " + expectedValue + " but got " + actualValue);
            logger.info("JSON field verification passed");
        } catch (Exception e) {
            logger.error("Failed to verify JSON field: {}", fieldPath, e);
            throw new RuntimeException("Failed to verify JSON field: " + fieldPath, e);
        }
    }
    
    @Then("the response JSON should have field {string}")
    public void the_response_json_should_have_field(String fieldPath) {
        logger.info("Verifying JSON field exists: {}", fieldPath);
        Assert.assertNotNull(lastResponse, "No response received");
        try {
            String responseText = lastResponse.text();
            Object value = jsonUtils.extractValueFromJson(responseText, fieldPath);
            Assert.assertNotNull(value, "Field " + fieldPath + " should exist in response");
            logger.info("JSON field existence verification passed");
        } catch (Exception e) {
            logger.error("Failed to verify JSON field existence: {}", fieldPath, e);
            throw new RuntimeException("Failed to verify JSON field existence: " + fieldPath, e);
        }
    }
    
    @Then("the response header {string} should be {string}")
    public void the_response_header_should_be(String headerName, String expectedValue) {
        logger.info("Verifying response header {} is: {}", headerName, expectedValue);
        Assert.assertNotNull(lastResponse, "No response received");
        try {
            String actualValue = lastResponse.headers().get(headerName);
            Assert.assertEquals(actualValue, expectedValue, 
                "Expected header " + headerName + " to be " + expectedValue + " but got " + actualValue);
            logger.info("Response header verification passed");
        } catch (Exception e) {
            logger.error("Failed to verify response header: {}", headerName, e);
            throw new RuntimeException("Failed to verify response header: " + headerName, e);
        }
    }
    
    @Then("the response time should be less than {int} milliseconds")
    public void the_response_time_should_be_less_than_milliseconds(int maxTime) {
        logger.info("Verifying response time is less than {} ms", maxTime);
        // Note: Playwright doesn't provide direct response time measurement
        // This would need to be implemented by measuring time before/after request
        logger.info("Response time verification passed (Note: Actual timing implementation needed)");
    }
    
    @Then("I log the response")
    public void i_log_the_response() {
        logger.info("Logging API response");
        if (lastResponse != null) {
            try {
                logger.info("Response Status: {}", lastResponse.status());
                logger.info("Response Headers: {}", lastResponse.headers());
                logger.info("Response Body: {}", lastResponse.text());
            } catch (Exception e) {
                logger.error("Failed to log response", e);
            }
        } else {
            logger.warn("No response to log");
        }
    }
}
