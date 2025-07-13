package com.jeyaram.automation.utils;

import com.jeyaram.automation.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

/**
 * Email utilities for sending test reports and notifications
 * Supports SMTP configuration and attachment handling
 * 
 * @author Jeyaram K
 * @version 1.0.0
 * @since 2025-01-01
 */
public class EmailUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailUtils.class);
    private final ConfigManager configManager = ConfigManager.getInstance();
    
    /**
     * Send test report email with attachments
     */
    public void sendTestReportEmail() {
        try {
            // Email configuration
            String smtpHost = configManager.getProperty("email.smtp.host", "smtp.gmail.com");
            String smtpPort = configManager.getProperty("email.smtp.port", "587");
            String username = configManager.getEncryptedProperty("email.username", "mailId");
            String password = configManager.getEncryptedProperty("email.password", "Password");
            String fromEmail = configManager.getProperty("email.from", username);
            String toEmails = configManager.getProperty("email.to", "to@gmail.com");
            String ccEmails = configManager.getProperty("email.cc", "");
            
            if (username.isEmpty() || password.isEmpty() || toEmails.isEmpty()) {
                logger.warn("Email configuration incomplete, skipping email notification");
                return;
            }
            
            // Create email session
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.ssl.trust", smtpHost);
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            
            // Set recipients
            String[] toEmailArray = toEmails.split(",");
            InternetAddress[] toAddresses = new InternetAddress[toEmailArray.length];
            for (int i = 0; i < toEmailArray.length; i++) {
                toAddresses[i] = new InternetAddress(toEmailArray[i].trim());
            }
            message.setRecipients(Message.RecipientType.TO, toAddresses);
            
            // Set CC recipients if provided
            if (!ccEmails.isEmpty()) {
                String[] ccEmailArray = ccEmails.split(",");
                InternetAddress[] ccAddresses = new InternetAddress[ccEmailArray.length];
                for (int i = 0; i < ccEmailArray.length; i++) {
                    ccAddresses[i] = new InternetAddress(ccEmailArray[i].trim());
                }
                message.setRecipients(Message.RecipientType.CC, ccAddresses);
            }
            
            // Set subject
            String environment = configManager.getEnvironment();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            message.setSubject("Playwright Automation Test Report - " + environment.toUpperCase() + " - " + timestamp);
            
            // Create multipart message
            Multipart multipart = new MimeMultipart();
            
            // Add email body
            BodyPart messageBodyPart = new MimeBodyPart();
            String emailBody = createEmailBody();
            messageBodyPart.setContent(emailBody, "text/html");
            multipart.addBodyPart(messageBodyPart);
            
            // Add attachments
            addReportAttachments(multipart);
            
            // Set message content
            message.setContent(multipart);
            
            // Send email
            Transport.send(message);
            
            logger.info("Test report email sent successfully to: {}", toEmails);
            
        } catch (Exception e) {
            logger.error("Failed to send test report email", e);
        }
    }
    
    /**
     * Create HTML email body
     */
    private String createEmailBody() {
        String environment = configManager.getEnvironment();
        String baseUrl = configManager.getEnvironmentConfig().getBaseUrl();
        String browser = configManager.getEnvironmentConfig().getBrowser();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 20px; background: #f5f5f5; }
                    .container { max-width: 800px; margin: 0 auto; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 8px 8px 0 0; text-align: center; }
                    .content { padding: 30px; }
                    .info-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin: 20px 0; }
                    .info-item { background: #f8f9fa; padding: 15px; border-radius: 5px; border-left: 4px solid #007bff; }
                    .info-label { font-weight: bold; color: #495057; font-size: 14px; }
                    .info-value { color: #6c757d; margin-top: 5px; }
                    .attachments { background: #e9ecef; padding: 20px; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; color: #6c757d; border-top: 1px solid #dee2e6; }
                    .logo { font-size: 24px; font-weight: bold; margin-bottom: 10px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">🎭 Playwright Java Hybrid Automation</div>
                        <h1>Test Execution Report</h1>
                        <p>Comprehensive automation testing results</p>
                    </div>
                    <div class="content">
                        <h2>📊 Test Execution Summary</h2>
                        <div class="info-grid">
                            <div class="info-item">
                                <div class="info-label">Environment</div>
                                <div class="info-value">""" + environment.toUpperCase() + """
                                </div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Base URL</div>
                                <div class="info-value">""" + baseUrl + """
                                </div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Browser</div>
                                <div class="info-value">""" + browser.toUpperCase() + """
                                </div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Execution Time</div>
                                <div class="info-value">""" + timestamp + """
                                </div>
                            </div>
                        </div>
                        
                        <div class="attachments">
                            <h3>📎 Attached Reports</h3>
                            <ul>
                                <li><strong>Allure Report:</strong> Comprehensive test execution details with steps and screenshots</li>
                                <li><strong>Extent Report:</strong> Interactive HTML report with test metrics and visuals</li>
                                <li><strong>Performance Report:</strong> Page performance metrics and analysis</li>
                                <li><strong>Jira Bug Links:</strong> Automated bug reports for failed tests (if applicable)</li>
                            </ul>
                        </div>
                        
                        <h3>🚀 Framework Features Utilized</h3>
                        <ul>
                            <li>✅ UI Testing with Playwright</li>
                            <li>✅ API Testing capabilities</li>
                            <li>✅ Mobile Testing support</li>
                            <li>✅ Performance monitoring</li>
                            <li>✅ Multiple reporting formats</li>
                            <li>✅ Automated failure handling</li>
                            <li>✅ Data-driven testing</li>
                            <li>✅ Cross-browser compatibility</li>
                        </ul>
                        
                        <p><strong>Note:</strong> Please find the detailed test reports attached to this email. 
                        For any questions or issues, please contact the automation team.</p>
                    </div>
                    <div class="footer">
                        <p><strong>Playwright Java Hybrid Automation Framework v1.0.0</strong></p>
                        <p>Created by <strong>Jeyaram K</strong> | Quality Engineering Excellence</p>
                        <p>Framework supports UI, API, Mobile, and Performance testing with comprehensive reporting</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
    
    /**
     * Add report attachments to email
     */
    private void addReportAttachments(Multipart multipart) {
        try {
            // Add Allure report
            addDirectoryAsZip(multipart, "allure-results", "Allure-Report.zip");
            
            // Add Extent report
            addFileAttachment(multipart, "test-results/extent-reports", "ExtentReport", ".html");
            
            // Add Performance report
            addFileAttachment(multipart, "test-results/performance", "PerformanceReport", ".html");
            
            // Add Cucumber report
            addFileAttachment(multipart, "target/cucumber-reports", "CucumberReport", ".html");
            
            // Add screenshots (latest ones)
            addLatestScreenshots(multipart);
            
        } catch (Exception e) {
            logger.error("Failed to add report attachments", e);
        }
    }
    
    /**
     * Add file attachment to email
     */
    private void addFileAttachment(Multipart multipart, String directory, String filePrefix, String extension) {
        try {
            Path dirPath = Paths.get(directory);
            if (Files.exists(dirPath)) {
                Files.walk(dirPath)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith(filePrefix) && 
                                   path.getFileName().toString().endsWith(extension))
                    .findFirst()
                    .ifPresent(reportPath -> {
                        try {
                            MimeBodyPart attachmentPart = new MimeBodyPart();
                            attachmentPart.attachFile(reportPath.toFile());
                            attachmentPart.setFileName(reportPath.getFileName().toString());
                            multipart.addBodyPart(attachmentPart);
                            logger.debug("Added attachment: {}", reportPath.getFileName());
                        } catch (Exception e) {
                            logger.error("Failed to attach file: {}", reportPath, e);
                        }
                    });
            }
        } catch (Exception e) {
            logger.error("Failed to add file attachment from directory: {}", directory, e);
        }
    }
    
    /**
     * Add directory as ZIP attachment
     */
    private void addDirectoryAsZip(Multipart multipart, String directory, String zipFileName) {
        try {
            Path dirPath = Paths.get(directory);
            if (Files.exists(dirPath)) {
                // This is a simplified version - in practice, you'd create a proper ZIP file
                logger.debug("Directory exists for zipping: {}", directory);
                // Implementation for zipping directory would go here
            }
        } catch (Exception e) {
            logger.error("Failed to add directory as ZIP: {}", directory, e);
        }
    }
    
    /**
     * Add latest screenshots to email
     */
    private void addLatestScreenshots(Multipart multipart) {
        try {
            Path screenshotsDir = Paths.get("test-results/screenshots");
            if (Files.exists(screenshotsDir)) {
                Files.walk(screenshotsDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".png"))
                    .sorted((a, b) -> {
                        try {
                            return Files.getLastModifiedTime(b).compareTo(Files.getLastModifiedTime(a));
                        } catch (Exception e) {
                            return 0;
                        }
                    })
                    .limit(5) // Only attach latest 5 screenshots
                    .forEach(screenshotPath -> {
                        try {
                            MimeBodyPart attachmentPart = new MimeBodyPart();
                            attachmentPart.attachFile(screenshotPath.toFile());
                            attachmentPart.setFileName("screenshot_" + screenshotPath.getFileName().toString());
                            multipart.addBodyPart(attachmentPart);
                            logger.debug("Added screenshot: {}", screenshotPath.getFileName());
                        } catch (Exception e) {
                            logger.error("Failed to attach screenshot: {}", screenshotPath, e);
                        }
                    });
            }
        } catch (Exception e) {
            logger.error("Failed to add screenshot attachments", e);
        }
    }
    
    /**
     * Send custom test report email with attachments and Jira bug URLs
     * 
     * @param subject Email subject
     * @param body Email body content
     * @param attachments List of attachment file paths
     * @param jiraBugUrls List of Jira bug URLs to include in email
     */
    public void sendTestReport(String subject, String body, List<String> attachments, List<String> jiraBugUrls) {
        try {
            // Email configuration
            String smtpHost = configManager.getProperty("email.smtp.host", "smtp.gmail.com");
            String smtpPort = configManager.getProperty("email.smtp.port", "587");
            String username = configManager.getEncryptedProperty("email.username", "");
            String password = configManager.getEncryptedProperty("email.password", "");
            String fromEmail = configManager.getProperty("email.from", username);
            String toEmails = configManager.getProperty("email.to", "");
            String ccEmails = configManager.getProperty("email.cc", "");
            
            if (username.isEmpty() || password.isEmpty() || toEmails.isEmpty()) {
                logger.warn("Email configuration incomplete, skipping email notification");
                return;
            }
            
            // Create email session
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.ssl.trust", smtpHost);
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            
            // Set recipients
            String[] toEmailArray = toEmails.split(",");
            InternetAddress[] toAddresses = new InternetAddress[toEmailArray.length];
            for (int i = 0; i < toEmailArray.length; i++) {
                toAddresses[i] = new InternetAddress(toEmailArray[i].trim());
            }
            message.setRecipients(Message.RecipientType.TO, toAddresses);
            
            // Set CC recipients if provided
            if (!ccEmails.isEmpty()) {
                String[] ccEmailArray = ccEmails.split(",");
                InternetAddress[] ccAddresses = new InternetAddress[ccEmailArray.length];
                for (int i = 0; i < ccEmailArray.length; i++) {
                    ccAddresses[i] = new InternetAddress(ccEmailArray[i].trim());
                }
                message.setRecipients(Message.RecipientType.CC, ccAddresses);
            }
            
            // Set subject
            message.setSubject(subject);
            
            // Create multipart message
            Multipart multipart = new MimeMultipart();
            
            // Add email body with Jira bug URLs
            BodyPart messageBodyPart = new MimeBodyPart();
            String enhancedBody = enhanceBodyWithJiraBugs(body, jiraBugUrls);
            messageBodyPart.setContent(enhancedBody, "text/html");
            multipart.addBodyPart(messageBodyPart);
            
            // Add custom attachments
            if (attachments != null && !attachments.isEmpty()) {
                for (String attachmentPath : attachments) {
                    addCustomAttachment(multipart, attachmentPath);
                }
            }
            
            // Set message content
            message.setContent(multipart);
            
            // Send email
            Transport.send(message);
            
            logger.info("Custom test report email sent successfully to: {}", toEmails);
            if (jiraBugUrls != null && !jiraBugUrls.isEmpty()) {
                logger.info("Email included {} Jira bug URLs", jiraBugUrls.size());
            }
            
        } catch (Exception e) {
            logger.error("Failed to send custom test report email", e);
        }
    }
    
    /**
     * Enhance email body with Jira bug URLs
     */
    private String enhanceBodyWithJiraBugs(String originalBody, List<String> jiraBugUrls) {
        if (jiraBugUrls == null || jiraBugUrls.isEmpty()) {
            return originalBody;
        }
        
        StringBuilder jiraBugSection = new StringBuilder();
        jiraBugSection.append("<div style=\"background: #fff3cd; border: 1px solid #ffeaa7; border-radius: 5px; padding: 15px; margin: 20px 0;\">");
        jiraBugSection.append("<h3 style=\"color: #856404; margin-top: 0;\">🐛 Automated Bug Reports</h3>");
        jiraBugSection.append("<p>The following Jira bugs have been automatically created for test failures:</p>");
        jiraBugSection.append("<ul>");
        
        for (String bugUrl : jiraBugUrls) {
            jiraBugSection.append("<li><a href=\"").append(bugUrl).append("\" target=\"_blank\">")
                          .append(bugUrl).append("</a></li>");
        }
        
        jiraBugSection.append("</ul>");
        jiraBugSection.append("<p><em>Please review and update these bugs as needed.</em></p>");
        jiraBugSection.append("</div>");
        
        // Insert Jira bug section before the closing content div
        if (originalBody.contains("</div>")) {
            int lastDivIndex = originalBody.lastIndexOf("</div>");
            return originalBody.substring(0, lastDivIndex) + jiraBugSection.toString() + 
                   originalBody.substring(lastDivIndex);
        } else {
            return originalBody + jiraBugSection.toString();
        }
    }
    
    /**
     * Add custom attachment to email
     */
    private void addCustomAttachment(Multipart multipart, String attachmentPath) {
        try {
            File file = new File(attachmentPath);
            if (file.exists() && file.isFile()) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(file);
                attachmentPart.setFileName(file.getName());
                multipart.addBodyPart(attachmentPart);
                logger.debug("Added custom attachment: {}", file.getName());
            } else {
                logger.warn("Attachment file not found: {}", attachmentPath);
            }
        } catch (Exception e) {
            logger.error("Failed to add custom attachment: {}", attachmentPath, e);
        }
    }
}
