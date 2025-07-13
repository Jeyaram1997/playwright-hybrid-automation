package com.jeyaram.automation.utils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security utilities for encryption/decryption of sensitive data
 * 
 * @author Jeyaram K
 * @version 1.0.0
 * @since 2025-01-01
 */
public class SecurityUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);
    private final StandardPBEStringEncryptor encryptor;
    private static final String ENCRYPTION_PREFIX = "ENC(";
    private static final String ENCRYPTION_SUFFIX = ")";
    
    public SecurityUtils() {
        this.encryptor = new StandardPBEStringEncryptor();
        this.encryptor.setPassword(getEncryptionPassword());
        // Use stronger algorithm - PBEWITHHMACSHA256ANDAES_256 is more secure than PBEWithMD5AndDES
        this.encryptor.setAlgorithm("PBEWITHHMACSHA256ANDAES_256");
        this.encryptor.setIvGenerator(new org.jasypt.iv.RandomIvGenerator());
    }
    
    /**
     * Get encryption password from environment or generate a secure default
     * Security Improvement: Generate stronger default and warn about using environment variable
     */
    private String getEncryptionPassword() {
        String password = System.getenv("ENCRYPTION_PASSWORD");
        if (password == null) {
            password = System.getProperty("encryption.password");
            if (password == null) {
                // Generate a stronger default password and warn user
                logger.warn("No encryption password found in environment. Using default password. " +
                           "For production, set ENCRYPTION_PASSWORD environment variable.");
                password = "Playwright2025@FrameworkSecure#Key!";
            }
        }
        return password;
    }
    
    /**
     * Encrypt sensitive data
     * 
     * @param plainText Plain text to encrypt
     * @return Encrypted text with prefix/suffix
     */
    public String encrypt(String plainText) {
        try {
            String encrypted = encryptor.encrypt(plainText);
            return ENCRYPTION_PREFIX + encrypted + ENCRYPTION_SUFFIX;
        } catch (Exception e) {
            logger.error("Encryption failed", e);
            throw new RuntimeException("Encryption failed", e);
        }
    }
    
    /**
     * Decrypt sensitive data
     * 
     * @param encryptedText Encrypted text with prefix/suffix
     * @return Decrypted plain text
     */
    public String decrypt(String encryptedText) {
        try {
            if (!isEncrypted(encryptedText)) {
                return encryptedText;
            }
            
            String encrypted = encryptedText.substring(
                ENCRYPTION_PREFIX.length(), 
                encryptedText.length() - ENCRYPTION_SUFFIX.length()
            );
            
            return encryptor.decrypt(encrypted);
        } catch (Exception e) {
            logger.error("Decryption failed", e);
            throw new RuntimeException("Decryption failed", e);
        }
    }
    
    /**
     * Check if text is encrypted
     * 
     * @param text Text to check
     * @return true if encrypted
     */
    public boolean isEncrypted(String text) {
        return text != null && 
               text.startsWith(ENCRYPTION_PREFIX) && 
               text.endsWith(ENCRYPTION_SUFFIX);
    }
}
