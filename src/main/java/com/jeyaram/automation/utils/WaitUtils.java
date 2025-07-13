package com.jeyaram.automation.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * Wait utilities for handling various wait scenarios
 * 
 * @author Jeyaram K
 * @version 1.0.0
 * @since 2025-01-01
 */
public class WaitUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(WaitUtils.class);
    
    /**
     * Wait for condition with polling
     * 
     * @param condition Condition to wait for
     * @param timeoutSeconds Timeout in seconds
     * @param pollIntervalMillis Poll interval in milliseconds
     * @return true if condition met, false if timeout
     */
    public boolean waitForCondition(Supplier<Boolean> condition, long timeoutSeconds, long pollIntervalMillis) {
        long startTime = System.currentTimeMillis();
        long timeoutMillis = timeoutSeconds * 1000;
        
        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            try {
                if (condition.get()) {
                    return true;
                }
                Thread.sleep(pollIntervalMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Wait interrupted", e);
                return false;
            } catch (Exception e) {
                logger.debug("Condition check failed, continuing to poll", e);
                try {
                    Thread.sleep(pollIntervalMillis);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Simple sleep with exception handling
     * 
     * @param milliseconds Milliseconds to sleep
     */
    public void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrupted", e);
        }
    }
    
    /**
     * Wait with exponential backoff
     * 
     * @param condition Condition to wait for
     * @param maxAttempts Maximum number of attempts
     * @param initialDelayMs Initial delay in milliseconds
     * @param maxDelayMs Maximum delay in milliseconds
     * @return true if condition met, false if max attempts reached
     */
    public boolean waitWithExponentialBackoff(Supplier<Boolean> condition, int maxAttempts, 
                                            long initialDelayMs, long maxDelayMs) {
        long delay = initialDelayMs;
        
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                if (condition.get()) {
                    return true;
                }
                
                if (attempt < maxAttempts) {
                    logger.debug("Attempt {} failed, waiting {} ms before retry", attempt, delay);
                    Thread.sleep(delay);
                    delay = Math.min(delay * 2, maxDelayMs);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Wait with exponential backoff interrupted", e);
                return false;
            } catch (Exception e) {
                logger.debug("Condition check failed on attempt {}", attempt, e);
            }
        }
        
        return false;
    }
}
