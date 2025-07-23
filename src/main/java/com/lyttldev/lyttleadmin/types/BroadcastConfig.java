package com.lyttldev.lyttleadmin.types;

/**
 * Represents broadcast settings for a role change.
 */
public class BroadcastConfig {
    private boolean global;
    private String message;
    private String permission;

    public boolean isGlobal() {
        return global;
    }

    public String getMessage() {
        return message;
    }

    public String getPermission() { return permission; }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPermission(String permission) { this.permission = permission; }
}
