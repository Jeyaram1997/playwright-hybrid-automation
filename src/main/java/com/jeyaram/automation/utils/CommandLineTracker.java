package com.jeyaram.automation.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Command line usage tracker for framework methods
 * Provides detailed usage statistics and help information
 * 
 * @author Jeyaram K
 * @version 1.0.0
 * @since 2025-01-01
 */
public class CommandLineTracker {
    
    private static final Logger logger = LoggerFactory.getLogger(CommandLineTracker.class);
    private static final Map<String, MethodUsage> methodUsageMap = new ConcurrentHashMap<>();
    
    /**
     * Track method usage with parameters
     * 
     * @param methodName Method name
     * @param parameters Method parameters
     */
    public void trackMethodUsage(String methodName, Map<String, Object> parameters) {
        try {
            MethodUsage usage = methodUsageMap.computeIfAbsent(methodName, 
                k -> new MethodUsage(methodName));
            usage.incrementUsage(parameters);
            
            logger.debug("Method usage tracked: {} with parameters: {}", methodName, parameters);
        } catch (Exception e) {
            logger.warn("Failed to track method usage for: {}", methodName, e);
        }
    }
    
    /**
     * Get usage statistics for all methods
     * 
     * @return Usage statistics map
     */
    public Map<String, Object> getUsageStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        for (Map.Entry<String, MethodUsage> entry : methodUsageMap.entrySet()) {
            MethodUsage usage = entry.getValue();
            Map<String, Object> methodStats = new HashMap<>();
            methodStats.put("totalCalls", usage.getTotalCalls());
            methodStats.put("firstUsed", usage.getFirstUsed());
            methodStats.put("lastUsed", usage.getLastUsed());
            methodStats.put("averageParameterCount", usage.getAverageParameterCount());
            methodStats.put("commonParameters", usage.getCommonParameters());
            
            stats.put(entry.getKey(), methodStats);
        }
        
        return stats;
    }
    
    /**
     * Print help information for all tracked methods
     */
    public void printHelp() {
        System.out.println("\\n" + "=".repeat(80));
        System.out.println("PLAYWRIGHT JAVA HYBRID AUTOMATION FRAMEWORK - METHOD USAGE GUIDE");
        System.out.println("Author: Jeyaram K");
        System.out.println("=".repeat(80));
        
        if (methodUsageMap.isEmpty()) {
            System.out.println("No methods have been used yet. Start using the framework to see usage statistics.");
            return;
        }
        
        System.out.println("\\nMETHOD USAGE STATISTICS:");
        System.out.println("-".repeat(50));
        
        methodUsageMap.entrySet().stream()
            .sorted((e1, e2) -> Long.compare(e2.getValue().getTotalCalls(), e1.getValue().getTotalCalls()))
            .forEach(entry -> {
                String methodName = entry.getKey();
                MethodUsage usage = entry.getValue();
                
                System.out.printf("\\n📍 %s\\n", methodName);
                System.out.printf("   📊 Total Calls: %d\\n", usage.getTotalCalls());
                System.out.printf("   🕐 First Used: %s\\n", usage.getFirstUsed());
                System.out.printf("   🕐 Last Used: %s\\n", usage.getLastUsed());
                System.out.printf("   📈 Avg Parameters: %.1f\\n", usage.getAverageParameterCount());
                
                if (!usage.getCommonParameters().isEmpty()) {
                    System.out.println("   🔧 Common Parameters:");
                    usage.getCommonParameters().forEach((param, count) -> 
                        System.out.printf("      - %s: %d times\\n", param, count));
                }
            });
        
        System.out.println("\\n" + "=".repeat(80));
        printMethodHelp();
        System.out.println("=".repeat(80));
    }
    
    /**
     * Print detailed method help
     */
    private void printMethodHelp() {
        System.out.println("\\nAVAILABLE FRAMEWORK METHODS:");
        System.out.println("-".repeat(50));
        
        // UI Testing Methods
        System.out.println("\\n🌐 UI TESTING METHODS:");
        printMethodHelp("initializeBrowser", "Initialize browser instance", 
            "browserType (string): chromium, firefox, webkit",
            "headless (boolean): true/false",
            "slowMo (int): delay in milliseconds");
        
        printMethodHelp("navigateTo", "Navigate to URL", 
            "url (string): target URL",
            "waitForLoad (boolean): wait for page load");
        
        printMethodHelp("click", "Click on element", 
            "selector (string): CSS/XPath selector",
            "timeout (long): timeout in milliseconds",
            "force (boolean): force click if needed");
        
        printMethodHelp("type", "Type text in element", 
            "selector (string): CSS/XPath selector",
            "text (string): text to type",
            "clearFirst (boolean): clear field first",
            "validate (boolean): validate input");
        
        // API Testing Methods
        System.out.println("\\n🔌 API TESTING METHODS:");
        printMethodHelp("initializeAPI", "Initialize API context", 
            "baseUrl (string): API base URL",
            "timeout (long): request timeout");
        
        printMethodHelp("get", "Perform GET request", 
            "endpoint (string): API endpoint",
            "queryParams (Map): query parameters",
            "headers (Map): additional headers");
        
        printMethodHelp("post", "Perform POST request", 
            "endpoint (string): API endpoint",
            "body (Object): request body",
            "headers (Map): additional headers");
        
        // Mobile Testing Methods
        System.out.println("\\n📱 MOBILE TESTING METHODS:");
        printMethodHelp("initializeAndroidDriver", "Initialize Android driver", 
            "appPath (string): path to APK",
            "deviceName (string): device name",
            "platformVersion (string): Android version",
            "appPackage (string): app package",
            "appActivity (string): app activity");
        
        printMethodHelp("swipe", "Perform swipe gesture", 
            "direction (SwipeDirection): UP, DOWN, LEFT, RIGHT",
            "distance (double): swipe distance 0.1-0.9",
            "duration (int): duration in milliseconds");
        
        // Performance Methods
        System.out.println("\\n⚡ PERFORMANCE TESTING METHODS:");
        printMethodHelp("getPerformanceMetrics", "Get page performance metrics", 
            "Returns: Map with loadTime, domContentLoaded, etc.");
        
        // Utility Methods
        System.out.println("\\n🛠️ UTILITY METHODS:");
        printMethodHelp("takeScreenshot", "Capture screenshot", 
            "description (string): screenshot description");
        
        printMethodHelp("waitForElement", "Wait for element", 
            "selector (string): element selector",
            "state (string): visible, hidden, attached, detached",
            "timeout (long): timeout in milliseconds");
    }
    
    /**
     * Print help for a specific method
     */
    private void printMethodHelp(String methodName, String description, String... parameters) {
        System.out.printf("\\n   📝 %s\\n", methodName);
        System.out.printf("      %s\\n", description);
        if (parameters.length > 0) {
            System.out.println("      Parameters:");
            for (String param : parameters) {
                System.out.printf("        • %s\\n", param);
            }
        }
    }
    
    /**
     * Get usage statistics for a specific method
     * 
     * @param methodName Method name
     * @return Method usage statistics
     */
    public Map<String, Object> getMethodStatistics(String methodName) {
        MethodUsage usage = methodUsageMap.get(methodName);
        if (usage == null) {
            return new HashMap<>();
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCalls", usage.getTotalCalls());
        stats.put("firstUsed", usage.getFirstUsed());
        stats.put("lastUsed", usage.getLastUsed());
        stats.put("averageParameterCount", usage.getAverageParameterCount());
        stats.put("commonParameters", usage.getCommonParameters());
        
        return stats;
    }
    
    /**
     * Reset usage statistics
     */
    public void resetStatistics() {
        methodUsageMap.clear();
        logger.info("Usage statistics reset");
    }
    
    /**
     * Inner class to track method usage details
     */
    private static class MethodUsage {
        private final String methodName;
        private final AtomicLong totalCalls;
        private final String firstUsed;
        private String lastUsed;
        private final Map<String, AtomicLong> parameterUsage;
        private long totalParameterCount;
        
        public MethodUsage(String methodName) {
            this.methodName = methodName;
            this.totalCalls = new AtomicLong(0);
            this.firstUsed = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            this.lastUsed = this.firstUsed;
            this.parameterUsage = new ConcurrentHashMap<>();
            this.totalParameterCount = 0;
        }
        
        public void incrementUsage(Map<String, Object> parameters) {
            totalCalls.incrementAndGet();
            lastUsed = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            if (parameters != null) {
                totalParameterCount += parameters.size();
                parameters.keySet().forEach(param -> 
                    parameterUsage.computeIfAbsent(param, k -> new AtomicLong(0)).incrementAndGet());
            }
        }
        
        public long getTotalCalls() {
            return totalCalls.get();
        }
        
        public String getFirstUsed() {
            return firstUsed;
        }
        
        public String getLastUsed() {
            return lastUsed;
        }
        
        public double getAverageParameterCount() {
            long calls = totalCalls.get();
            return calls > 0 ? (double) totalParameterCount / calls : 0;
        }
        
        public Map<String, Long> getCommonParameters() {
            Map<String, Long> commonParams = new HashMap<>();
            parameterUsage.forEach((param, count) -> commonParams.put(param, count.get()));
            return commonParams;
        }
    }
}
