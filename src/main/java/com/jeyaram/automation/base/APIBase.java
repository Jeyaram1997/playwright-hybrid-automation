package com.jeyaram.automation.base;

import com.jeyaram.automation.config.ConfigManager;
import com.jeyaram.automation.reporting.AllureManager;
import com.jeyaram.automation.utils.JsonUtils;
import com.jeyaram.automation.utils.CommandLineTracker;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * API Testing Base class providing comprehensive REST API testing capabilities
 * Supports various HTTP methods, authentication, and response validation
 * 
 * @author Jeyaram K
 * @version 1.0.0
 * @since 2025-01-01
 */
public class APIBase {
    
    private static final Logger logger = LoggerFactory.getLogger(APIBase.class);
    
    protected static Playwright playwright;
    protected static APIRequestContext apiContext;
    
    protected final ConfigManager configManager = ConfigManager.getInstance();
    protected final JsonUtils jsonUtils = new JsonUtils();
    private static final CommandLineTracker cmdTracker = new CommandLineTracker();
    
    // Default headers
    private final Map<String, String> defaultHeaders = new HashMap<>();
    
    /**
     * Initialize API context with base configuration
     * 
     * @param baseUrl Base URL for API requests
     * @param timeout Request timeout in milliseconds
     */
    public void initializeAPI(String baseUrl, long timeout) {
        try {
            cmdTracker.trackMethodUsage("initializeAPI", 
                Map.of("baseUrl", baseUrl, "timeout", timeout));
            
            playwright = Playwright.create();
            
            APIRequest.NewContextOptions options = new APIRequest.NewContextOptions()
                .setBaseURL(baseUrl)
                .setTimeout(timeout);
            
            // Add default headers
            if (!defaultHeaders.isEmpty()) {
                options.setExtraHTTPHeaders(defaultHeaders);
            }
            
            apiContext = playwright.request().newContext(options);
            
            logger.info("API context initialized with base URL: {}", baseUrl);
            AllureManager.addStep("API context initialized: " + baseUrl);
            
        } catch (Exception e) {
            logger.error("Failed to initialize API context", e);
            throw new RuntimeException("API initialization failed", e);
        }
    }
    
    /**
     * Set default headers for all requests
     * 
     * @param headers Map of headers
     */
    public void setDefaultHeaders(Map<String, String> headers) {
        cmdTracker.trackMethodUsage("setDefaultHeaders", Map.of("headers", headers));
        
        defaultHeaders.clear();
        defaultHeaders.putAll(headers);
        
        logger.info("Default headers set: {}", headers.size());
        AllureManager.addStep("Default headers configured");
    }
    
    /**
     * Set authentication token
     * 
     * @param token Bearer token
     */
    public void setAuthToken(String token) {
        cmdTracker.trackMethodUsage("setAuthToken", Map.of("token", "***"));
        
        defaultHeaders.put("Authorization", "Bearer " + token);
        
        logger.info("Authentication token set");
        AllureManager.addStep("Authentication token configured");
    }
    
    /**
     * Set API key authentication
     * 
     * @param apiKey API key
     * @param headerName Header name for API key (default: X-API-Key)
     */
    public void setApiKey(String apiKey, String headerName) {
        cmdTracker.trackMethodUsage("setApiKey", 
            Map.of("apiKey", "***", "headerName", headerName));
        
        String header = headerName != null ? headerName : "X-API-Key";
        defaultHeaders.put(header, apiKey);
        
        logger.info("API key set with header: {}", header);
        AllureManager.addStep("API key configured");
    }
    
    /**
     * Perform GET request
     * 
     * @param endpoint API endpoint
     * @param queryParams Query parameters
     * @param headers Additional headers
     * @return API response
     */
    public APIResponse get(String endpoint, Map<String, String> queryParams, Map<String, String> headers) {
        try {
            cmdTracker.trackMethodUsage("get", 
                Map.of("endpoint", endpoint, "queryParams", queryParams, "headers", headers));
            
            RequestOptions options = RequestOptions.create();
            
            if (queryParams != null && !queryParams.isEmpty()) {
                for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                    options.setQueryParam(entry.getKey(), entry.getValue());
                }
            }
            
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    options.setHeader(entry.getKey(), entry.getValue());
                }
            }
            
            APIResponse response = apiContext.get(endpoint, options);
            
            logAPIRequest("GET", endpoint, null, headers, queryParams);
            logAPIResponse(response);
            
            return response;
            
        } catch (Exception e) {
            logger.error("GET request failed for endpoint: {}", endpoint, e);
            AllureManager.addStep("GET request failed: " + endpoint, Status.FAILED);
            throw new RuntimeException("GET request failed: " + endpoint, e);
        }
    }
    
    /**
     * Perform POST request
     * 
     * @param endpoint API endpoint
     * @param body Request body
     * @param headers Additional headers
     * @return API response
     */
    public APIResponse post(String endpoint, Object body, Map<String, String> headers) {
        try {
            cmdTracker.trackMethodUsage("post", 
                Map.of("endpoint", endpoint, "body", body, "headers", headers));
            
            RequestOptions options = RequestOptions.create();
            
            if (body != null) {
                if (body instanceof String) {
                    options.setData((String) body);
                } else {
                    options.setData(jsonUtils.toJson(body));
                }
            }
            
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    options.setHeader(entry.getKey(), entry.getValue());
                }
            }
            
            APIResponse response = apiContext.post(endpoint, options);
            
            logAPIRequest("POST", endpoint, body, headers, null);
            logAPIResponse(response);
            
            return response;
            
        } catch (Exception e) {
            logger.error("POST request failed for endpoint: {}", endpoint, e);
            AllureManager.addStep("POST request failed: " + endpoint, Status.FAILED);
            throw new RuntimeException("POST request failed: " + endpoint, e);
        }
    }
    
    /**
     * Perform PUT request
     * 
     * @param endpoint API endpoint
     * @param body Request body
     * @param headers Additional headers
     * @return API response
     */
    public APIResponse put(String endpoint, Object body, Map<String, String> headers) {
        try {
            cmdTracker.trackMethodUsage("put", 
                Map.of("endpoint", endpoint, "body", body, "headers", headers));
            
            RequestOptions options = RequestOptions.create();
            
            if (body != null) {
                if (body instanceof String) {
                    options.setData((String) body);
                } else {
                    options.setData(jsonUtils.toJson(body));
                }
            }
            
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    options.setHeader(entry.getKey(), entry.getValue());
                }
            }
            
            APIResponse response = apiContext.put(endpoint, options);
            
            logAPIRequest("PUT", endpoint, body, headers, null);
            logAPIResponse(response);
            
            return response;
            
        } catch (Exception e) {
            logger.error("PUT request failed for endpoint: {}", endpoint, e);
            AllureManager.addStep("PUT request failed: " + endpoint, Status.FAILED);
            throw new RuntimeException("PUT request failed: " + endpoint, e);
        }
    }
    
    /**
     * Perform PATCH request
     * 
     * @param endpoint API endpoint
     * @param body Request body
     * @param headers Additional headers
     * @return API response
     */
    public APIResponse patch(String endpoint, Object body, Map<String, String> headers) {
        try {
            cmdTracker.trackMethodUsage("patch", 
                Map.of("endpoint", endpoint, "body", body, "headers", headers));
            
            RequestOptions options = RequestOptions.create();
            
            if (body != null) {
                if (body instanceof String) {
                    options.setData((String) body);
                } else {
                    options.setData(jsonUtils.toJson(body));
                }
            }
            
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    options.setHeader(entry.getKey(), entry.getValue());
                }
            }
            
            APIResponse response = apiContext.patch(endpoint, options);
            
            logAPIRequest("PATCH", endpoint, body, headers, null);
            logAPIResponse(response);
            
            return response;
            
        } catch (Exception e) {
            logger.error("PATCH request failed for endpoint: {}", endpoint, e);
            AllureManager.addStep("PATCH request failed: " + endpoint, Status.FAILED);
            throw new RuntimeException("PATCH request failed: " + endpoint, e);
        }
    }
    
    /**
     * Perform DELETE request
     * 
     * @param endpoint API endpoint
     * @param headers Additional headers
     * @return API response
     */
    public APIResponse delete(String endpoint, Map<String, String> headers) {
        try {
            cmdTracker.trackMethodUsage("delete", 
                Map.of("endpoint", endpoint, "headers", headers));
            
            RequestOptions options = RequestOptions.create();
            
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    options.setHeader(entry.getKey(), entry.getValue());
                }
            }
            
            APIResponse response = apiContext.delete(endpoint, options);
            
            logAPIRequest("DELETE", endpoint, null, headers, null);
            logAPIResponse(response);
            
            return response;
            
        } catch (Exception e) {
            logger.error("DELETE request failed for endpoint: {}", endpoint, e);
            AllureManager.addStep("DELETE request failed: " + endpoint, Status.FAILED);
            throw new RuntimeException("DELETE request failed: " + endpoint, e);
        }
    }
    
    /**
     * Validate response status code
     * 
     * @param response API response
     * @param expectedStatus Expected status code
     */
    public void validateStatusCode(APIResponse response, int expectedStatus) {
        cmdTracker.trackMethodUsage("validateStatusCode", 
            Map.of("expectedStatus", expectedStatus, "actualStatus", response.status()));
        
        int actualStatus = response.status();
        
        if (actualStatus != expectedStatus) {
            String error = String.format("Status code validation failed. Expected: %d, Actual: %d", 
                expectedStatus, actualStatus);
            logger.error(error);
            AllureManager.addStep(error, Status.FAILED);
            throw new AssertionError(error);
        }
        
        logger.info("Status code validation passed: {}", expectedStatus);
        AllureManager.addStep("Status code validation passed: " + expectedStatus);
    }
    
    /**
     * Validate response contains expected text
     * 
     * @param response API response
     * @param expectedText Expected text
     */
    public void validateResponseContains(APIResponse response, String expectedText) {
        try {
            cmdTracker.trackMethodUsage("validateResponseContains", 
                Map.of("expectedText", expectedText));
            
            String responseText = response.text();
            
            if (!responseText.contains(expectedText)) {
                String error = "Response does not contain expected text: " + expectedText;
                logger.error(error);
                AllureManager.addStep(error, Status.FAILED);
                throw new AssertionError(error);
            }
            
            logger.info("Response contains validation passed");
            AllureManager.addStep("Response contains validation passed");
            
        } catch (Exception e) {
            logger.error("Response validation failed", e);
            throw new RuntimeException("Response validation failed", e);
        }
    }
    
    /**
     * Validate JSON response schema
     * 
     * @param response API response
     * @param schemaPath Path to JSON schema file
     */
    public void validateJsonSchema(APIResponse response, String schemaPath) {
        try {
            cmdTracker.trackMethodUsage("validateJsonSchema", 
                Map.of("schemaPath", schemaPath));
            
            String responseJson = response.text();
            boolean isValid = jsonUtils.validateJsonSchema(responseJson, schemaPath);
            
            if (!isValid) {
                String error = "JSON schema validation failed";
                logger.error(error);
                AllureManager.addStep(error, Status.FAILED);
                throw new AssertionError(error);
            }
            
            logger.info("JSON schema validation passed");
            AllureManager.addStep("JSON schema validation passed");
            
        } catch (Exception e) {
            logger.error("JSON schema validation failed", e);
            throw new RuntimeException("JSON schema validation failed", e);
        }
    }
    
    /**
     * Extract value from JSON response using JSONPath
     * 
     * @param response API response
     * @param jsonPath JSONPath expression
     * @return Extracted value
     */
    public Object extractFromResponse(APIResponse response, String jsonPath) {
        try {
            cmdTracker.trackMethodUsage("extractFromResponse", 
                Map.of("jsonPath", jsonPath));
            
            String responseJson = response.text();
            Object extractedValue = jsonUtils.extractValueFromJson(responseJson, jsonPath);
            
            logger.info("Value extracted from response using JSONPath: {}", jsonPath);
            AllureManager.addStep("Value extracted using JSONPath: " + jsonPath);
            
            return extractedValue;
            
        } catch (Exception e) {
            logger.error("Failed to extract value from response", e);
            throw new RuntimeException("Failed to extract value from response", e);
        }
    }
    
    /**
     * Get response as JSON object
     * 
     * @param response API response
     * @param clazz Target class type
     * @param <T> Generic type
     * @return Parsed JSON object
     */
    public <T> T getResponseAsObject(APIResponse response, Class<T> clazz) {
        try {
            cmdTracker.trackMethodUsage("getResponseAsObject", 
                Map.of("clazz", clazz.getSimpleName()));
            
            String responseJson = response.text();
            T object = jsonUtils.fromJson(responseJson, clazz);
            
            logger.info("Response parsed to object: {}", clazz.getSimpleName());
            AllureManager.addStep("Response parsed to: " + clazz.getSimpleName());
            
            return object;
            
        } catch (Exception e) {
            logger.error("Failed to parse response to object", e);
            throw new RuntimeException("Failed to parse response to object", e);
        }
    }
    
    /**
     * Log API request details
     */
    private void logAPIRequest(String method, String endpoint, Object body, 
                              Map<String, String> headers, Map<String, String> queryParams) {
        try {
            StringBuilder requestLog = new StringBuilder();
            requestLog.append("API Request:\n");
            requestLog.append("Method: ").append(method).append("\n");
            requestLog.append("Endpoint: ").append(endpoint).append("\n");
            
            if (headers != null && !headers.isEmpty()) {
                requestLog.append("Headers: ").append(headers).append("\n");
            }
            
            if (queryParams != null && !queryParams.isEmpty()) {
                requestLog.append("Query Params: ").append(queryParams).append("\n");
            }
            
            if (body != null) {
                requestLog.append("Body: ").append(body).append("\n");
            }
            
            logger.info(requestLog.toString());
            Allure.addAttachment("API Request", "text/plain", requestLog.toString());
            
        } catch (Exception e) {
            logger.warn("Failed to log API request", e);
        }
    }
    
    /**
     * Log API response details
     */
    private void logAPIResponse(APIResponse response) {
        try {
            StringBuilder responseLog = new StringBuilder();
            responseLog.append("API Response:\n");
            responseLog.append("Status: ").append(response.status()).append("\n");
            responseLog.append("Status Text: ").append(response.statusText()).append("\n");
            responseLog.append("Headers: ").append(response.headers()).append("\n");
            responseLog.append("Body: ").append(response.text()).append("\n");
            
            logger.info(responseLog.toString());
            Allure.addAttachment("API Response", "application/json", response.text());
            
        } catch (Exception e) {
            logger.warn("Failed to log API response", e);
        }
    }
    
    /**
     * Cleanup API resources
     */
    public void cleanup() {
        try {
            if (apiContext != null) {
                apiContext.dispose();
            }
            if (playwright != null) {
                playwright.close();
            }
            
            logger.info("API cleanup completed");
            
        } catch (Exception e) {
            logger.error("API cleanup failed", e);
        }
    }
    
    /**
     * Get API context
     * 
     * @return API request context
     */
    public APIRequestContext getApiContext() {
        return apiContext;
    }
    
    /**
     * Get command line usage statistics
     * 
     * @return Usage statistics
     */
    public static Map<String, Object> getUsageStatistics() {
        return cmdTracker.getUsageStatistics();
    }
}
