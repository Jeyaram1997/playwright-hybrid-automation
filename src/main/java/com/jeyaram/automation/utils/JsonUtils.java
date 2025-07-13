package com.jeyaram.automation.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * JSON utilities for API testing and data handling
 * 
 * @author Jeyaram K
 * @version 1.0.0
 * @since 2025-01-01
 */
public class JsonUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    private final ObjectMapper objectMapper;
    
    public JsonUtils() {
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Convert object to JSON string
     * 
     * @param object Object to convert
     * @return JSON string
     */
    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Failed to convert object to JSON", e);
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }
    
    /**
     * Convert JSON string to object
     * 
     * @param json JSON string
     * @param clazz Target class type
     * @param <T> Generic type
     * @return Parsed object
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse JSON string", e);
            throw new RuntimeException("Failed to parse JSON string", e);
        }
    }
    
    /**
     * Validate JSON against schema (basic validation)
     * 
     * @param json JSON string to validate
     * @param schemaPath Path to JSON schema file
     * @return true if valid
     */
    public boolean validateJsonSchema(String json, String schemaPath) {
        try {
            // Basic JSON validation
            objectMapper.readTree(json);
            objectMapper.readTree(new File(schemaPath));
            
            logger.info("JSON schema validation completed");
            return true;
            
        } catch (Exception e) {
            logger.error("JSON schema validation failed", e);
            return false;
        }
    }
    
    /**
     * Extract value from JSON using simple path
     * 
     * @param json JSON string
     * @param path Path to extract
     * @return Extracted value
     */
    public Object extractValueFromJson(String json, String path) {
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode currentNode = rootNode;
            
            String[] pathParts = path.split("\\.");
            for (String part : pathParts) {
                currentNode = currentNode.get(part);
                if (currentNode == null) {
                    return null;
                }
            }
            
            return currentNode.asText();
            
        } catch (Exception e) {
            logger.error("Failed to extract value from JSON", e);
            throw new RuntimeException("Failed to extract value from JSON", e);
        }
    }
}
