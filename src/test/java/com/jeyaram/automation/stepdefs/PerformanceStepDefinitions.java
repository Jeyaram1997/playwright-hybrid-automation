package com.jeyaram.automation.stepdefs;

import com.jeyaram.automation.base.PerformanceBase;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

/**
 * Step definitions for Performance testing scenarios
 * 
 * @author Jeyaram K
 * @version 1.0.0
 * @since 2025-01-01
 */
public class PerformanceStepDefinitions {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceStepDefinitions.class);
    private PerformanceBase performanceBase;
    
    public PerformanceStepDefinitions() {
        this.performanceBase = new PerformanceBase();
    }
    
    @Given("I initialize performance monitoring")
    public void i_initialize_performance_monitoring() {
        logger.info("Initializing performance monitoring");
        try {
            performanceBase.startPerformanceMonitoring();
            logger.info("Performance monitoring initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize performance monitoring", e);
            throw new RuntimeException("Failed to initialize performance monitoring", e);
        }
    }
    
    @When("I start measuring performance for {string}")
    public void i_start_measuring_performance_for(String testName) {
        logger.info("Starting performance measurement for: {}", testName);
        try {
            performanceBase.startMeasurement(testName);
            logger.info("Performance measurement started for: {}", testName);
        } catch (Exception e) {
            logger.error("Failed to start performance measurement for: {}", testName, e);
            throw new RuntimeException("Failed to start performance measurement for: " + testName, e);
        }
    }
    
    @When("I stop measuring performance for {string}")
    public void i_stop_measuring_performance_for(String testName) {
        logger.info("Stopping performance measurement for: {}", testName);
        try {
            performanceBase.stopMeasurement(testName);
            logger.info("Performance measurement stopped for: {}", testName);
        } catch (Exception e) {
            logger.error("Failed to stop performance measurement for: {}", testName, e);
            throw new RuntimeException("Failed to stop performance measurement for: " + testName, e);
        }
    }
    
    @When("I measure page load performance for URL {string}")
    public void i_measure_page_load_performance_for_url(String url) {
        logger.info("Measuring page load performance for URL: {}", url);
        try {
            performanceBase.measurePageLoadTime(url);
            logger.info("Page load performance measurement completed for: {}", url);
        } catch (Exception e) {
            logger.error("Failed to measure page load performance for: {}", url, e);
            throw new RuntimeException("Failed to measure page load performance for: " + url, e);
        }
    }
    
    @When("I simulate {int} concurrent users")
    public void i_simulate_concurrent_users(int userCount) {
        logger.info("Simulating {} concurrent users", userCount);
        try {
            performanceBase.simulateLoad(userCount, 60); // 60 seconds duration
            logger.info("Load simulation completed for {} users", userCount);
        } catch (Exception e) {
            logger.error("Failed to simulate {} concurrent users", userCount, e);
            throw new RuntimeException("Failed to simulate " + userCount + " concurrent users", e);
        }
    }
    
    @When("I wait for {int} seconds during performance test")
    public void i_wait_for_seconds_during_performance_test(int seconds) {
        logger.info("Waiting for {} seconds during performance test", seconds);
        try {
            Thread.sleep(seconds * 1000L);
            logger.info("Performance test wait completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Performance test wait was interrupted", e);
        }
    }
    
    @Then("the response time should be less than {int} milliseconds")
    public void the_response_time_should_be_less_than_milliseconds(int maxResponseTime) {
        logger.info("Verifying response time is less than {} ms", maxResponseTime);
        try {
            double averageResponseTime = performanceBase.getAverageResponseTime();
            Assert.assertTrue(averageResponseTime < maxResponseTime, 
                "Response time should be less than " + maxResponseTime + "ms, but was " + averageResponseTime + "ms");
            logger.info("Response time verification passed: {} ms", averageResponseTime);
        } catch (Exception e) {
            logger.error("Response time verification failed", e);
            throw new RuntimeException("Response time verification failed", e);
        }
    }
    
    @Then("the throughput should be at least {double} requests per second")
    public void the_throughput_should_be_at_least_requests_per_second(double minThroughput) {
        logger.info("Verifying throughput is at least {} requests/sec", minThroughput);
        try {
            double actualThroughput = performanceBase.getThroughput();
            Assert.assertTrue(actualThroughput >= minThroughput, 
                "Throughput should be at least " + minThroughput + " req/sec, but was " + actualThroughput + " req/sec");
            logger.info("Throughput verification passed: {} req/sec", actualThroughput);
        } catch (Exception e) {
            logger.error("Throughput verification failed", e);
            throw new RuntimeException("Throughput verification failed", e);
        }
    }
    
    @Then("the error rate should be less than {double} percent")
    public void the_error_rate_should_be_less_than_percent(double maxErrorRate) {
        logger.info("Verifying error rate is less than {}%", maxErrorRate);
        try {
            double actualErrorRate = performanceBase.getErrorRate();
            Assert.assertTrue(actualErrorRate < maxErrorRate, 
                "Error rate should be less than " + maxErrorRate + "%, but was " + actualErrorRate + "%");
            logger.info("Error rate verification passed: {}%", actualErrorRate);
        } catch (Exception e) {
            logger.error("Error rate verification failed", e);
            throw new RuntimeException("Error rate verification failed", e);
        }
    }
    
    @Then("I generate performance report")
    public void i_generate_performance_report() {
        logger.info("Generating performance report");
        try {
            performanceBase.generateReport();
            logger.info("Performance report generated successfully");
        } catch (Exception e) {
            logger.error("Failed to generate performance report", e);
            throw new RuntimeException("Failed to generate performance report", e);
        }
    }
    
    @Then("I should see performance metrics")
    public void i_should_see_performance_metrics() {
        logger.info("Displaying performance metrics");
        try {
            double avgResponseTime = performanceBase.getAverageResponseTime();
            double throughput = performanceBase.getThroughput();
            double errorRate = performanceBase.getErrorRate();
            
            logger.info("Performance Metrics:");
            logger.info("Average Response Time: {} ms", avgResponseTime);
            logger.info("Throughput: {} req/sec", throughput);
            logger.info("Error Rate: {}%", errorRate);
            
            // Verify that we have meaningful metrics
            Assert.assertTrue(avgResponseTime >= 0, "Average response time should be non-negative");
            Assert.assertTrue(throughput >= 0, "Throughput should be non-negative");
            Assert.assertTrue(errorRate >= 0 && errorRate <= 100, "Error rate should be between 0 and 100%");
            
            logger.info("Performance metrics validation passed");
        } catch (Exception e) {
            logger.error("Failed to retrieve performance metrics", e);
            throw new RuntimeException("Failed to retrieve performance metrics", e);
        }
    }
    
    @Then("I clean up performance monitoring")
    public void i_clean_up_performance_monitoring() {
        logger.info("Cleaning up performance monitoring");
        try {
            performanceBase.stopPerformanceMonitoring();
            logger.info("Performance monitoring cleanup completed");
        } catch (Exception e) {
            logger.error("Failed to clean up performance monitoring", e);
        }
    }
}
