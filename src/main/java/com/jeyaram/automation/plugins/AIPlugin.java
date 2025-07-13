package com.jeyaram.automation.plugins;

import java.util.Map;

/**
 * Base interface for AI plugins
 * Author: Jeyaram K
 */
public interface AIPlugin {
    String getName();
    String getVersion();
    String getDescription();
    String getProvider();
    boolean isEnabled();
    PluginResult execute(Map<String, Object> parameters);
}
