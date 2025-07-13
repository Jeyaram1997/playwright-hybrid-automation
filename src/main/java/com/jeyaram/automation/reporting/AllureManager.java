package com.jeyaram.automation.reporting;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Allure Report Manager for enhanced reporting capabilities
 * 
 * @author Jeyaram K
 * @version 1.0.0
 * @since 2025-01-01
 */
public class AllureManager {
    
    private static final Logger logger = LoggerFactory.getLogger(AllureManager.class);
    private static final AllureLifecycle lifecycle = Allure.getLifecycle();
    
    /**
     * Add step to current test
     * 
     * @param stepName Step name
     */
    public static void addStep(String stepName) {
        addStep(stepName, Status.PASSED);
    }
    
    /**
     * Add step with status to current test
     * 
     * @param stepName Step name
     * @param status Step status
     */
    public static void addStep(String stepName, Status status) {
        String uuid = UUID.randomUUID().toString();
        StepResult stepResult = new StepResult()
            .setName(stepName)
            .setStatus(status);
        
        lifecycle.startStep(uuid, stepResult);
        lifecycle.stopStep(uuid);
        
        logger.debug("Added Allure step: {} with status: {}", stepName, status);
    }
    
    /**
     * Add attachment to current test
     * 
     * @param name Attachment name
     * @param content Attachment content
     * @param type MIME type
     */
    public static void addAttachment(String name, String content, String type) {
        Allure.addAttachment(name, type, content);
        logger.debug("Added Allure attachment: {}", name);
    }
    
    /**
     * Add attachment from file
     * 
     * @param name Attachment name
     * @param filePath Path to file
     * @param type MIME type
     */
    public static void addAttachmentFromFile(String name, String filePath, String type) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                byte[] content = Files.readAllBytes(path);
                Allure.addAttachment(name, type, new ByteArrayInputStream(content), "");
                logger.debug("Added Allure attachment from file: {}", filePath);
            } else {
                logger.warn("File not found for attachment: {}", filePath);
            }
        } catch (IOException e) {
            logger.error("Failed to add attachment from file: {}", filePath, e);
        }
    }
    
    /**
     * Add screenshot attachment
     * 
     * @param screenshotPath Path to screenshot
     * @param description Screenshot description
     */
    public static void addScreenshot(String screenshotPath, String description) {
        addAttachmentFromFile(description, screenshotPath, "image/png");
    }
    
    /**
     * Add test description
     * 
     * @param description Test description
     */
    public static void addDescription(String description) {
        Allure.description(description);
        logger.debug("Added Allure description: {}", description);
    }
    
    /**
     * Add test link
     * 
     * @param name Link name
     * @param url Link URL
     */
    public static void addLink(String name, String url) {
        Allure.link(name, url);
        logger.debug("Added Allure link: {} -> {}", name, url);
    }
    
    /**
     * Add issue link
     * 
     * @param issueId Issue ID
     */
    public static void addIssue(String issueId) {
        Allure.issue(issueId, issueId);
        logger.debug("Added Allure issue: {}", issueId);
    }
    
    /**
     * Add test case ID link
     * 
     * @param testCaseId Test case ID
     */
    public static void addTestCaseId(String testCaseId) {
        Allure.tms(testCaseId, testCaseId);
        logger.debug("Added Allure test case ID: {}", testCaseId);
    }
    
    /**
     * Add label to test
     * 
     * @param name Label name
     * @param value Label value
     */
    public static void addLabel(String name, String value) {
        Allure.label(name, value);
        logger.debug("Added Allure label: {} = {}", name, value);
    }
    
    /**
     * Add severity label
     * 
     * @param severity Severity level
     */
    public static void addSeverity(String severity) {
        addLabel("severity", severity);
    }
    
    /**
     * Add epic label
     * 
     * @param epic Epic name
     */
    public static void addEpic(String epic) {
        addLabel("epic", epic);
    }
    
    /**
     * Add feature label
     * 
     * @param feature Feature name
     */
    public static void addFeature(String feature) {
        addLabel("feature", feature);
    }
    
    /**
     * Add story label
     * 
     * @param story Story name
     */
    public static void addStory(String story) {
        addLabel("story", story);
    }
    
    /**
     * Add owner label
     * 
     * @param owner Owner name
     */
    public static void addOwner(String owner) {
        addLabel("owner", owner);
    }
    
    /**
     * Add custom parameter
     * 
     * @param name Parameter name
     * @param value Parameter value
     */
    public static void addParameter(String name, String value) {
        Allure.parameter(name, value);
        logger.debug("Added Allure parameter: {} = {}", name, value);
    }
    
    /**
     * Add environment information
     * 
     * @param name Environment property name
     * @param value Environment property value
     */
    public static void addEnvironmentInfo(String name, String value) {
        // Write to allure-results/environment.properties
        try {
            Path allureResultsDir = Paths.get("allure-results");
            if (!Files.exists(allureResultsDir)) {
                Files.createDirectories(allureResultsDir);
            }
            
            Path envFile = allureResultsDir.resolve("environment.properties");
            String content = name + "=" + value + System.lineSeparator();
            
            if (Files.exists(envFile)) {
                content = Files.readString(envFile) + content;
            }
            
            Files.writeString(envFile, content);
            logger.debug("Added environment info: {} = {}", name, value);
            
        } catch (IOException e) {
            logger.error("Failed to add environment info", e);
        }
    }
}
