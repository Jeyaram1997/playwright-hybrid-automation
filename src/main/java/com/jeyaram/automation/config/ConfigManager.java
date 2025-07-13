package com.jeyaram.automation.config;

import com.jeyaram.automation.utils.SecurityUtils;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration Manager for handling environment and application configurations
 * Supports multiple data sources: properties files, environment variables, encrypted secrets
 * 
 * @author Jeyaram K
 * @version 1.0.0
 * @since 2025-01-01
 */
public class ConfigManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static ConfigManager instance;
    private final Properties properties;
    private final Dotenv dotenv;
    private final SecurityUtils securityUtils;
    private final EnvironmentConfig envConfig;
    
    private ConfigManager() {
        this.properties = new Properties();
        this.dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.securityUtils = new SecurityUtils();
        this.envConfig = new EnvironmentConfig();
        loadConfigurations();
    }
    
    /**
     * Get singleton instance
     * 
     * @return ConfigManager instance
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    /**
     * Load configurations from multiple sources
     */
    private void loadConfigurations() {
        try {
            // Load from properties files
            loadPropertiesFile("application.properties");
            loadPropertiesFile("test.properties");
            
            // Load environment-specific properties
            String environment = getEnvironment();
            loadPropertiesFile("application-" + environment + ".properties");
            
            logger.info("Configurations loaded successfully for environment: {}", environment);
            
        } catch (Exception e) {
            logger.error("Failed to load configurations", e);
        }
    }
    
    /**
     * Load properties from file
     * 
     * @param fileName Properties file name
     */
    private void loadPropertiesFile(String fileName) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream != null) {
                properties.load(inputStream);
                logger.debug("Loaded properties from: {}", fileName);
            }
        } catch (IOException e) {
            logger.warn("Could not load properties file: {}", fileName);
        }
    }
    
    /**
     * Get property value with fallback mechanism
     * 
     * @param key Property key
     * @param defaultValue Default value if not found
     * @return Property value
     */
    public String getProperty(String key, String defaultValue) {
        // Priority: System properties > Environment variables > .env file > properties files > default
        String value = System.getProperty(key);
        if (value != null) {
            return value;
        }
        
        value = System.getenv(key);
        if (value != null) {
            return value;
        }
        
        value = dotenv.get(key);
        if (value != null) {
            return value;
        }
        
        value = properties.getProperty(key);
        if (value != null) {
            return value;
        }
        
        return defaultValue;
    }
    
    /**
     * Get property value
     * 
     * @param key Property key
     * @return Property value
     */
    public String getProperty(String key) {
        return getProperty(key, null);
    }
    
    /**
     * Get encrypted property value
     * 
     * @param key Property key
     * @param defaultValue Default value if not found
     * @return Decrypted property value
     */
    public String getEncryptedProperty(String key, String defaultValue) {
        String encryptedValue = getProperty(key, defaultValue);
        if (encryptedValue != null && securityUtils.isEncrypted(encryptedValue)) {
            return securityUtils.decrypt(encryptedValue);
        }
        return encryptedValue;
    }
    
    /**
     * Get boolean property
     * 
     * @param key Property key
     * @param defaultValue Default value
     * @return Boolean value
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Get integer property
     * 
     * @param key Property key
     * @param defaultValue Default value
     * @return Integer value
     */
    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for key {}: {}", key, value);
            return defaultValue;
        }
    }
    
    /**
     * Get long property
     * 
     * @param key Property key
     * @param defaultValue Default value
     * @return Long value
     */
    public long getLongProperty(String key, long defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            logger.warn("Invalid long value for key {}: {}", key, value);
            return defaultValue;
        }
    }
    
    /**
     * Get double property
     * 
     * @param key Property key
     * @param defaultValue Default value
     * @return Double value
     */
    public double getDoubleProperty(String key, double defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            logger.warn("Invalid double value for key {}: {}", key, value);
            return defaultValue;
        }
    }
    
    /**
     * Get current environment
     * 
     * @return Environment name
     */
    public String getEnvironment() {
        return getProperty("environment", "dev");
    }
    
    /**
     * Get environment configuration
     * 
     * @return EnvironmentConfig instance
     */
    public EnvironmentConfig getEnvironmentConfig() {
        return envConfig;
    }
    
    /**
     * Check if running in development environment
     * 
     * @return true if development environment
     */
    public boolean isDevelopment() {
        return "dev".equalsIgnoreCase(getEnvironment());
    }
    
    /**
     * Check if running in test environment
     * 
     * @return true if test environment
     */
    public boolean isTest() {
        return "test".equalsIgnoreCase(getEnvironment());
    }
    
    /**
     * Check if running in staging environment
     * 
     * @return true if staging environment
     */
    public boolean isStaging() {
        return "staging".equalsIgnoreCase(getEnvironment());
    }
    
    /**
     * Check if running in production environment
     * 
     * @return true if production environment
     */
    public boolean isProduction() {
        return "prod".equalsIgnoreCase(getEnvironment());
    }
    
    /**
     * Set property value (runtime only)
     * 
     * @param key Property key
     * @param value Property value
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
    
    /**
     * Get all properties
     * 
     * @return Properties object
     */
    public Properties getAllProperties() {
        return new Properties(properties);
    }
}
