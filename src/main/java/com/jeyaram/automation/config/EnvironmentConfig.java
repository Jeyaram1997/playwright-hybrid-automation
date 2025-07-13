package com.jeyaram.automation.config;

/**
 * Environment-specific configuration class
 * 
 * @author Jeyaram K
 * @version 1.0.0
 * @since 2025-01-01
 */
public class EnvironmentConfig {
    
    private final ConfigManager configManager;
    
    public EnvironmentConfig() {
        // Use a simple properties-based approach to avoid circular dependency
        this.configManager = null;
    }
    
    /**
     * Get base URL for the current environment
     */
    public String getBaseUrl() {
        return getProperty("base.url", "http://localhost:3000");
    }
    
    /**
     * Get API base URL
     */
    public String getApiBaseUrl() {
        return getProperty("api.base.url", "http://localhost:3000/api");
    }
    
    /**
     * Get database URL
     */
    public String getDatabaseUrl() {
        return getProperty("database.url", "jdbc:h2:mem:testdb");
    }
    
    /**
     * Get browser type
     */
    public String getBrowser() {
        return getProperty("browser", "chromium");
    }
    
    /**
     * Get headless mode setting
     */
    public boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "false"));
    }
    
    /**
     * Get viewport width
     */
    public int getViewportWidth() {
        return Integer.parseInt(getProperty("viewport.width", "1920"));
    }
    
    /**
     * Get viewport height
     */
    public int getViewportHeight() {
        return Integer.parseInt(getProperty("viewport.height", "1080"));
    }
    
    /**
     * Get locale
     */
    public String getLocale() {
        return getProperty("locale", "en-US");
    }
    
    /**
     * Get timezone
     */
    public String getTimezone() {
        return getProperty("timezone", "America/New_York");
    }
    
    /**
     * Get timeout settings
     */
    public long getDefaultTimeout() {
        return Long.parseLong(getProperty("default.timeout", "30000"));
    }
    
    /**
     * Get page load timeout
     */
    public long getPageLoadTimeout() {
        return Long.parseLong(getProperty("page.load.timeout", "60000"));
    }
    
    /**
     * Get API timeout
     */
    public long getApiTimeout() {
        return Long.parseLong(getProperty("api.timeout", "30000"));
    }
    
    /**
     * Helper method to get property
     */
    private String getProperty(String key, String defaultValue) {
        // Priority: System properties > Environment variables > default
        String value = System.getProperty(key);
        if (value != null) {
            return value;
        }
        
        value = System.getenv(key.replace(".", "_").toUpperCase());
        if (value != null) {
            return value;
        }
        
        return defaultValue;
    }
}
