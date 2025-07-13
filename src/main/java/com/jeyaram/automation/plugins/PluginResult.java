package com.jeyaram.automation.plugins;

/**
 * Plugin execution result
 * Author: Jeyaram K
 */
public class PluginResult {
    private final boolean success;
    private final String message;
    private final Object data;
    
    private PluginResult(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    public static PluginResult success(Object data) {
        return new PluginResult(true, "Success", data);
    }
    
    public static PluginResult success(String message, Object data) {
        return new PluginResult(true, message, data);
    }
    
    public static PluginResult error(String message) {
        return new PluginResult(false, message, null);
    }
    
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Object getData() { return data; }
    
    @Override
    public String toString() {
        return "PluginResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
