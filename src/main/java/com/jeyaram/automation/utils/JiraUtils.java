package com.jeyaram.automation.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeyaram.automation.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * JIRA integration utilities for automated bug reporting
 * Author: Jeyaram K
 */
public class JiraUtils {
    private static final Logger logger = LoggerFactory.getLogger(JiraUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    
    private static String jiraUrl;
    private static String username;
    private static String apiToken;
    private static String projectKey;
    private static String issueType;
    
    static {
        initializeJiraConfig();
    }
    
    private static void initializeJiraConfig() {
        try {
            ConfigManager configManager = ConfigManager.getInstance();
            jiraUrl = configManager.getProperty("jira.url", "");
            username = configManager.getProperty("jira.username", "");
            apiToken = configManager.getProperty("jira.api.token", "");
            projectKey = configManager.getProperty("jira.project.key", "");
            issueType = configManager.getProperty("jira.issue.type", "Bug");
        } catch (Exception e) {
            logger.warn("Failed to initialize JIRA configuration: {}", e.getMessage());
        }
    }
    
    /**
     * Create a bug in JIRA for test failure
     */
    public static String createBugForTestFailure(String testName, String errorMessage, 
                                               String stackTrace, String environment, 
                                               String testType, String priority) {
        try {
            if (!isJiraConfigured()) {
                logger.warn("JIRA not configured. Skipping bug creation.");
                return null;
            }
            
            String summary = String.format("[AUTO] Test Failure - %s in %s", testName, environment);
            String description = buildBugDescription(testName, errorMessage, stackTrace, environment, testType);
            
            Map<String, Object> issueData = new HashMap<>();
            Map<String, Object> fields = new HashMap<>();
            
            // Project
            Map<String, String> project = new HashMap<>();
            project.put("key", projectKey);
            fields.put("project", project);
            
            // Issue Type
            Map<String, String> issueTypeMap = new HashMap<>();
            issueTypeMap.put("name", issueType);
            fields.put("issuetype", issueTypeMap);
            
            // Summary and Description
            fields.put("summary", summary);
            fields.put("description", description);
            
            // Priority
            if (priority != null && !priority.isEmpty()) {
                Map<String, String> priorityMap = new HashMap<>();
                priorityMap.put("name", priority);
                fields.put("priority", priorityMap);
            }
            
            // Labels
            fields.put("labels", new String[]{"automation", "test-failure", testType.toLowerCase()});
            
            issueData.put("fields", fields);
            
            String jsonPayload = objectMapper.writeValueAsString(issueData);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(jiraUrl + "/rest/api/2/issue"))
                    .header("Authorization", getAuthHeader())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 201) {
                JsonNode responseJson = objectMapper.readTree(response.body());
                String issueKey = responseJson.get("key").asText();
                String issueUrl = jiraUrl + "/browse/" + issueKey;
                
                logger.info("JIRA bug created successfully: {}", issueUrl);
                return issueUrl;
            } else {
                logger.error("Failed to create JIRA bug. Status: {}, Response: {}", 
                           response.statusCode(), response.body());
                return null;
            }
            
        } catch (Exception e) {
            logger.error("Error creating JIRA bug: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Create a bug with attachments
     */
    public static String createBugWithAttachments(String testName, String errorMessage, 
                                                String stackTrace, String environment, 
                                                String testType, String priority, 
                                                String... attachmentPaths) {
        String issueUrl = createBugForTestFailure(testName, errorMessage, stackTrace, 
                                                environment, testType, priority);
        
        if (issueUrl != null && attachmentPaths != null && attachmentPaths.length > 0) {
            String issueKey = extractIssueKeyFromUrl(issueUrl);
            for (String attachmentPath : attachmentPaths) {
                addAttachmentToIssue(issueKey, attachmentPath);
            }
        }
        
        return issueUrl;
    }
    
    /**
     * Add attachment to existing JIRA issue
     */
    public static boolean addAttachmentToIssue(String issueKey, String filePath) {
        try {
            if (!isJiraConfigured() || issueKey == null || filePath == null) {
                return false;
            }
            
            // Note: This is a simplified implementation
            // In production, you would use multipart/form-data to upload files
            logger.info("Attachment upload simulated for issue {} with file {}", issueKey, filePath);
            return true;
            
        } catch (Exception e) {
            logger.error("Error adding attachment to JIRA issue {}: {}", issueKey, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Get JIRA issue details
     */
    public static Map<String, Object> getIssueDetails(String issueKey) {
        try {
            if (!isJiraConfigured() || issueKey == null) {
                return new HashMap<>();
            }
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(jiraUrl + "/rest/api/2/issue/" + issueKey))
                    .header("Authorization", getAuthHeader())
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonNode responseJson = objectMapper.readTree(response.body());
                Map<String, Object> issueDetails = new HashMap<>();
                
                issueDetails.put("key", responseJson.get("key").asText());
                issueDetails.put("summary", responseJson.get("fields").get("summary").asText());
                issueDetails.put("status", responseJson.get("fields").get("status").get("name").asText());
                issueDetails.put("url", jiraUrl + "/browse/" + issueKey);
                
                return issueDetails;
            }
            
        } catch (Exception e) {
            logger.error("Error getting JIRA issue details for {}: {}", issueKey, e.getMessage(), e);
        }
        
        return new HashMap<>();
    }
    
    /**
     * Update issue status
     */
    public static boolean updateIssueStatus(String issueKey, String status) {
        try {
            if (!isJiraConfigured() || issueKey == null || status == null) {
                return false;
            }
            
            // Get available transitions
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(jiraUrl + "/rest/api/2/issue/" + issueKey + "/transitions"))
                    .header("Authorization", getAuthHeader())
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonNode responseJson = objectMapper.readTree(response.body());
                JsonNode transitions = responseJson.get("transitions");
                
                String transitionId = null;
                for (JsonNode transition : transitions) {
                    if (status.equalsIgnoreCase(transition.get("to").get("name").asText())) {
                        transitionId = transition.get("id").asText();
                        break;
                    }
                }
                
                if (transitionId != null) {
                    Map<String, Object> transitionData = new HashMap<>();
                    Map<String, String> transition = new HashMap<>();
                    transition.put("id", transitionId);
                    transitionData.put("transition", transition);
                    
                    String jsonPayload = objectMapper.writeValueAsString(transitionData);
                    
                    HttpRequest updateRequest = HttpRequest.newBuilder()
                            .uri(URI.create(jiraUrl + "/rest/api/2/issue/" + issueKey + "/transitions"))
                            .header("Authorization", getAuthHeader())
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                            .build();
                    
                    HttpResponse<String> updateResponse = httpClient.send(updateRequest, 
                                                                        HttpResponse.BodyHandlers.ofString());
                    
                    return updateResponse.statusCode() == 204;
                }
            }
            
        } catch (Exception e) {
            logger.error("Error updating JIRA issue status for {}: {}", issueKey, e.getMessage(), e);
        }
        
        return false;
    }
    
    /**
     * Search for existing issues
     */
    public static String findExistingBug(String testName, String environment) {
        try {
            if (!isJiraConfigured()) {
                return null;
            }
            
            String jql = String.format(
                "project = %s AND summary ~ \"%s\" AND summary ~ \"%s\" AND status != Done ORDER BY created DESC",
                projectKey, testName, environment
            );
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(jiraUrl + "/rest/api/2/search?jql=" + 
                         java.net.URLEncoder.encode(jql, StandardCharsets.UTF_8) + "&maxResults=1"))
                    .header("Authorization", getAuthHeader())
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonNode responseJson = objectMapper.readTree(response.body());
                JsonNode issues = responseJson.get("issues");
                
                if (issues.size() > 0) {
                    String issueKey = issues.get(0).get("key").asText();
                    return jiraUrl + "/browse/" + issueKey;
                }
            }
            
        } catch (Exception e) {
            logger.error("Error searching for existing JIRA bugs: {}", e.getMessage(), e);
        }
        
        return null;
    }
    
    private static String buildBugDescription(String testName, String errorMessage, 
                                            String stackTrace, String environment, String testType) {
        StringBuilder description = new StringBuilder();
        
        description.append("*Automated Test Failure Report*\n\n");
        description.append("*Test Details:*\n");
        description.append("• Test Name: ").append(testName).append("\n");
        description.append("• Test Type: ").append(testType).append("\n");
        description.append("• Environment: ").append(environment).append("\n");
        description.append("• Timestamp: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n\n");
        
        description.append("*Error Information:*\n");
        description.append("• Error Message: ").append(errorMessage != null ? errorMessage : "N/A").append("\n\n");
        
        if (stackTrace != null && !stackTrace.isEmpty()) {
            description.append("*Stack Trace:*\n");
            description.append("{code:java}\n");
            description.append(stackTrace);
            description.append("\n{code}\n\n");
        }
        
        description.append("*Additional Information:*\n");
        description.append("• Framework: Playwright Java Hybrid Framework\n");
        description.append("• Author: Jeyaram K\n");
        description.append("• Report Generated: Automatically by test framework\n");
        
        return description.toString();
    }
    
    private static String getAuthHeader() {
        String auth = username + ":" + apiToken;
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }
    
    private static boolean isJiraConfigured() {
        return jiraUrl != null && !jiraUrl.isEmpty() &&
               username != null && !username.isEmpty() &&
               apiToken != null && !apiToken.isEmpty() &&
               projectKey != null && !projectKey.isEmpty();
    }
    
    private static String extractIssueKeyFromUrl(String issueUrl) {
        if (issueUrl != null && issueUrl.contains("/browse/")) {
            return issueUrl.substring(issueUrl.lastIndexOf("/") + 1);
        }
        return null;
    }
    
    /**
     * Get JIRA configuration status
     */
    public static Map<String, Object> getConfigurationStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("configured", isJiraConfigured());
        status.put("url", jiraUrl != null ? jiraUrl : "Not configured");
        status.put("username", username != null ? username : "Not configured");
        status.put("projectKey", projectKey != null ? projectKey : "Not configured");
        status.put("issueType", issueType);
        return status;
    }
}
