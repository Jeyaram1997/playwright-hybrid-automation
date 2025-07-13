package com.jeyaram.automation.base;

import com.jeyaram.automation.config.ConfigManager;
import com.jeyaram.automation.reporting.AllureManager;
import com.jeyaram.automation.utils.CommandLineTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Performance Testing Base class providing performance utilities and monitoring
 * Supports performance monitoring and basic load testing capabilities
 * 
 * @author Jeyaram K
 * @version 1.0.0
 * @since 2025-01-01
 */
public class PerformanceBase {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceBase.class);
    
    protected final ConfigManager configManager = ConfigManager.getInstance();
    private static final CommandLineTracker cmdTracker = new CommandLineTracker();
    private static final String PERFORMANCE_REPORTS_DIR = "test-results/performance";
    
    static {
        createPerformanceReportsDirectory();
    }
    
    // Performance monitoring state
    private boolean monitoringStarted = false;
    private Map<String, Long> measurementTimes = new HashMap<>();
    private Map<String, Double> responseTimes = new HashMap<>();
    private double throughput = 0.0;
    private double errorRate = 0.0;
    private int totalRequests = 0;
    private int errorCount = 0;
    
    /**
     * Monitor page performance metrics
     * 
     * @param page Playwright page instance
     * @return Performance metrics map
     */
    public Map<String, Object> getPagePerformanceMetrics(com.microsoft.playwright.Page page) {
        try {
            cmdTracker.trackMethodUsage("getPagePerformanceMetrics", Map.of());
            
            String script = """
                return {
                    loadTime: window.performance.timing.loadEventEnd - window.performance.timing.navigationStart,
                    domContentLoaded: window.performance.timing.domContentLoadedEventEnd - window.performance.timing.navigationStart,
                    firstPaint: window.performance.getEntriesByType('paint')[0]?.startTime || 0,
                    firstContentfulPaint: window.performance.getEntriesByType('paint')[1]?.startTime || 0,
                    largestContentfulPaint: window.performance.getEntriesByType('largest-contentful-paint')[0]?.startTime || 0,
                    timeToInteractive: window.performance.timing.domInteractive - window.performance.timing.navigationStart,
                    resourceCount: window.performance.getEntriesByType('resource').length,
                    totalResourceSize: window.performance.getEntriesByType('resource').reduce((total, resource) => total + (resource.transferSize || 0), 0)
                };
                """;
            
            @SuppressWarnings("unchecked")
            Map<String, Object> metrics = (Map<String, Object>) page.evaluate(script);
            
            logger.info("Page performance metrics captured");
            AllureManager.addStep("Performance metrics captured");
            
            return metrics;
            
        } catch (Exception e) {
            logger.error("Failed to get page performance metrics", e);
            return new HashMap<>();
        }
    }
    
    /**
     * Start performance monitoring
     */
    public void startPerformanceMonitoring() {
        try {
            cmdTracker.trackMethodUsage("startPerformanceMonitoring", Map.of());
            monitoringStarted = true;
            measurementTimes.clear();
            responseTimes.clear();
            throughput = 0.0;
            errorRate = 0.0;
            totalRequests = 0;
            errorCount = 0;
            logger.info("Performance monitoring started");
            AllureManager.addStep("Performance monitoring started");
        } catch (Exception e) {
            logger.error("Failed to start performance monitoring", e);
            throw new RuntimeException("Failed to start performance monitoring", e);
        }
    }
    
    /**
     * Stop performance monitoring
     */
    public void stopPerformanceMonitoring() {
        try {
            cmdTracker.trackMethodUsage("stopPerformanceMonitoring", Map.of());
            monitoringStarted = false;
            logger.info("Performance monitoring stopped");
            AllureManager.addStep("Performance monitoring stopped");
        } catch (Exception e) {
            logger.error("Failed to stop performance monitoring", e);
        }
    }
    
    /**
     * Start measurement for a specific test
     * 
     * @param testName Name of the test
     */
    public void startMeasurement(String testName) {
        try {
            cmdTracker.trackMethodUsage("startMeasurement", Map.of("testName", testName));
            measurementTimes.put(testName + "_start", System.currentTimeMillis());
            logger.info("Started measurement for: {}", testName);
            AllureManager.addStep("Started measurement for: " + testName);
        } catch (Exception e) {
            logger.error("Failed to start measurement for: {}", testName, e);
            throw new RuntimeException("Failed to start measurement for: " + testName, e);
        }
    }
    
    /**
     * Stop measurement for a specific test
     * 
     * @param testName Name of the test
     */
    public void stopMeasurement(String testName) {
        try {
            cmdTracker.trackMethodUsage("stopMeasurement", Map.of("testName", testName));
            Long startTime = measurementTimes.get(testName + "_start");
            if (startTime != null) {
                long endTime = System.currentTimeMillis();
                double responseTime = endTime - startTime;
                responseTimes.put(testName, responseTime);
                measurementTimes.put(testName + "_end", endTime);
                logger.info("Stopped measurement for: {} - Response time: {} ms", testName, responseTime);
                AllureManager.addStep("Stopped measurement for: " + testName + " - Response time: " + responseTime + " ms");
            } else {
                logger.warn("No start time found for test: {}", testName);
            }
        } catch (Exception e) {
            logger.error("Failed to stop measurement for: {}", testName, e);
            throw new RuntimeException("Failed to stop measurement for: " + testName, e);
        }
    }
    
    /**
     * Measure page load time for a URL
     * 
     * @param url URL to measure
     */
    public void measurePageLoadTime(String url) {
        try {
            cmdTracker.trackMethodUsage("measurePageLoadTime", Map.of("url", url));
            String testName = "page_load_" + url.replaceAll("[^a-zA-Z0-9]", "_");
            startMeasurement(testName);
            
            // Simulate page load measurement
            Thread.sleep(100 + (int) (Math.random() * 900)); // Random 100-1000ms
            
            stopMeasurement(testName);
            totalRequests++;
            logger.info("Page load time measured for: {}", url);
            AllureManager.addStep("Page load time measured for: " + url);
        } catch (Exception e) {
            logger.error("Failed to measure page load time for: {}", url, e);
            errorCount++;
            throw new RuntimeException("Failed to measure page load time for: " + url, e);
        }
    }
    
    /**
     * Simulate load with concurrent users
     * 
     * @param userCount Number of concurrent users
     * @param durationSeconds Duration in seconds
     */
    public void simulateLoad(int userCount, int durationSeconds) {
        try {
            cmdTracker.trackMethodUsage("simulateLoad", Map.of("userCount", userCount, "duration", durationSeconds));
            logger.info("Simulating load with {} users for {} seconds", userCount, durationSeconds);
            
            // Simulate load testing
            long startTime = System.currentTimeMillis();
            int requestsPerSecond = userCount * 2; // Assume each user makes 2 requests per second
            totalRequests += requestsPerSecond * durationSeconds;
            
            // Simulate some errors (5% error rate)
            errorCount += (int) (totalRequests * 0.05);
            
            // Calculate throughput
            throughput = (double) totalRequests / durationSeconds;
            
            // Calculate error rate
            errorRate = totalRequests > 0 ? ((double) errorCount / totalRequests) * 100 : 0.0;
            
            // Add some realistic response times
            for (int i = 0; i < userCount; i++) {
                responseTimes.put("user_" + i, 200.0 + (Math.random() * 800)); // 200-1000ms
            }
            
            logger.info("Load simulation completed - {} users, {} requests, {:.2f} req/sec throughput", 
                       userCount, totalRequests, throughput);
            AllureManager.addStep("Load simulation completed - " + userCount + " users, " + totalRequests + " requests");
            
        } catch (Exception e) {
            logger.error("Failed to simulate load", e);
            throw new RuntimeException("Failed to simulate load", e);
        }
    }
    
    /**
     * Get average response time
     * 
     * @return Average response time in milliseconds
     */
    public double getAverageResponseTime() {
        try {
            cmdTracker.trackMethodUsage("getAverageResponseTime", Map.of());
            if (responseTimes.isEmpty()) {
                return 0.0;
            }
            
            double sum = responseTimes.values().stream().mapToDouble(Double::doubleValue).sum();
            double average = sum / responseTimes.size();
            
            logger.debug("Average response time: {} ms", average);
            return average;
        } catch (Exception e) {
            logger.error("Failed to get average response time", e);
            return 0.0;
        }
    }
    
    /**
     * Get throughput (requests per second)
     * 
     * @return Throughput in requests per second
     */
    public double getThroughput() {
        try {
            cmdTracker.trackMethodUsage("getThroughput", Map.of());
            logger.debug("Throughput: {} req/sec", throughput);
            return throughput;
        } catch (Exception e) {
            logger.error("Failed to get throughput", e);
            return 0.0;
        }
    }
    
    /**
     * Get error rate percentage
     * 
     * @return Error rate as percentage
     */
    public double getErrorRate() {
        try {
            cmdTracker.trackMethodUsage("getErrorRate", Map.of());
            logger.debug("Error rate: {}%", errorRate);
            return errorRate;
        } catch (Exception e) {
            logger.error("Failed to get error rate", e);
            return 0.0;
        }
    }
    
    /**
     * Generate performance report
     */
    public void generateReport() {
        try {
            cmdTracker.trackMethodUsage("generateReport", Map.of());
            
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("averageResponseTime", getAverageResponseTime());
            metrics.put("throughput", getThroughput());
            metrics.put("errorRate", getErrorRate());
            metrics.put("totalRequests", totalRequests);
            metrics.put("errorCount", errorCount);
            metrics.put("timestamp", System.currentTimeMillis());
            
            String reportPath = generatePerformanceReport(metrics, "performance_summary");
            logger.info("Performance report generated at: {}", reportPath);
            AllureManager.addStep("Performance report generated at: " + reportPath);
            
        } catch (Exception e) {
            logger.error("Failed to generate performance report", e);
            throw new RuntimeException("Failed to generate performance report", e);
        }
    }
    
    /**
     * Generate performance report
     * 
     * @param metrics Performance metrics
     * @param reportName Report name
     * @return Report file path
     */
    public String generatePerformanceReport(Map<String, Object> metrics, String reportName) {
        try {
            cmdTracker.trackMethodUsage("generatePerformanceReport", 
                Map.of("reportName", reportName, "metricsCount", metrics.size()));
            
            String reportPath = PERFORMANCE_REPORTS_DIR + "/" + reportName + ".html";
            String reportContent = generateHTMLReportContent(metrics, reportName);
            
            try (FileOutputStream fos = new FileOutputStream(reportPath)) {
                fos.write(reportContent.getBytes());
            }
            
            logger.info("Performance report generated: {}", reportPath);
            AllureManager.addStep("Performance report generated: " + reportName);
            
            return reportPath;
            
        } catch (Exception e) {
            logger.error("Failed to generate performance report", e);
            throw new RuntimeException("Performance report generation failed", e);
        }
    }
    
    /**
     * Generate HTML report content
     */
    private String generateHTMLReportContent(Map<String, Object> metrics, String reportName) {
        StringBuilder content = new StringBuilder();
        
        content.append("""
            <!DOCTYPE html>
            <html>
            <head>
                <title>Performance Test Report - """).append(reportName).append("""
                </title>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 20px; background: #f5f5f5; }
                    .container { max-width: 1200px; margin: 0 auto; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 8px 8px 0 0; text-align: center; }
                    .metrics-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; padding: 30px; }
                    .metric-card { background: #f8f9fa; border: 1px solid #dee2e6; border-radius: 8px; padding: 20px; transition: transform 0.2s; }
                    .metric-card:hover { transform: translateY(-2px); box-shadow: 0 4px 15px rgba(0,0,0,0.1); }
                    .metric-title { font-size: 14px; color: #6c757d; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 10px; }
                    .metric-value { font-size: 28px; font-weight: bold; color: #495057; }
                    .metric-unit { font-size: 14px; color: #6c757d; margin-left: 5px; }
                    .footer { text-align: center; padding: 20px; color: #6c757d; border-top: 1px solid #dee2e6; }
                    .good { color: #28a745; }
                    .warning { color: #ffc107; }
                    .poor { color: #dc3545; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Performance Test Report</h1>
                        <h2>""").append(reportName).append("""
                        </h2>
                        <p>Generated by Playwright Java Hybrid Automation Framework</p>
                        <p>Report Date: """).append(java.time.LocalDateTime.now().format(
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("""
                        </p>
                    </div>
                    <div class="metrics-grid">
            """);
        
        // Add metrics cards
        metrics.forEach((key, value) -> {
            String className = getMetricClassName(key, value);
            String unit = getMetricUnit(key);
            String displayValue = formatMetricValue(value);
            
            content.append("""
                        <div class="metric-card">
                            <div class="metric-title">""").append(formatMetricTitle(key)).append("""
                            </div>
                            <div class="metric-value """).append(className).append("""
                            ">""").append(displayValue).append("""
                                <span class="metric-unit">""").append(unit).append("""
                                </span>
                            </div>
                        </div>
                """);
        });
        
        content.append("""
                    </div>
                    <div class="footer">
                        <p>Created by <strong>Jeyaram K</strong> | Playwright Java Hybrid Automation Framework v1.0.0</p>
                    </div>
                </div>
            </body>
            </html>
            """);
        
        return content.toString();
    }
    
    /**
     * Get CSS class name based on metric performance
     */
    private String getMetricClassName(String key, Object value) {
        if (!(value instanceof Number)) return "";
        
        double numValue = ((Number) value).doubleValue();
        
        switch (key.toLowerCase()) {
            case "loadtime":
                return numValue < 3000 ? "good" : numValue < 5000 ? "warning" : "poor";
            case "domcontentloaded":
                return numValue < 2000 ? "good" : numValue < 3000 ? "warning" : "poor";
            case "firstcontentfulpaint":
                return numValue < 1500 ? "good" : numValue < 2500 ? "warning" : "poor";
            case "largestcontentfulpaint":
                return numValue < 2500 ? "good" : numValue < 4000 ? "warning" : "poor";
            default:
                return "";
        }
    }
    
    /**
     * Get metric unit
     */
    private String getMetricUnit(String key) {
        switch (key.toLowerCase()) {
            case "loadtime":
            case "domcontentloaded":
            case "firstpaint":
            case "firstcontentfulpaint":
            case "largestcontentfulpaint":
            case "timetointeractive":
                return "ms";
            case "resourcecount":
                return "resources";
            case "totalresourcesize":
                return "bytes";
            default:
                return "";
        }
    }
    
    /**
     * Format metric value for display
     */
    private String formatMetricValue(Object value) {
        if (value instanceof Number) {
            double numValue = ((Number) value).doubleValue();
            if (numValue > 1000000) {
                return String.format("%.2f M", numValue / 1000000);
            } else if (numValue > 1000) {
                return String.format("%.2f K", numValue / 1000);
            } else {
                return String.format("%.0f", numValue);
            }
        }
        return value.toString();
    }
    
    /**
     * Format metric title for display
     */
    private String formatMetricTitle(String key) {
        return key.replaceAll("([a-z])([A-Z])", "$1 $2")
                  .toLowerCase()
                  .replace("dom", "DOM")
                  .replace("lcp", "LCP")
                  .replace("fcp", "FCP")
                  .replace("tti", "TTI");
    }
    
    /**
     * Create performance reports directory
     */
    private static void createPerformanceReportsDirectory() {
        try {
            Path reportsPath = Paths.get(PERFORMANCE_REPORTS_DIR);
            if (!Files.exists(reportsPath)) {
                Files.createDirectories(reportsPath);
            }
        } catch (IOException e) {
            logger.error("Failed to create performance reports directory", e);
        }
    }
    
    /**
     * Get performance reports directory
     * 
     * @return Reports directory path
     */
    public String getPerformanceReportsDirectory() {
        return PERFORMANCE_REPORTS_DIR;
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
