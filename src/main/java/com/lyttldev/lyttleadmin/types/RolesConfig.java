package com.lyttldev.lyttleadmin.types;

import java.util.List;
import java.util.Map;

/**
 * Represents the top-level roles object in the config.yml.
 * Example usage: Map<String, RoleConfig> roles;
 */
public class RolesConfig {
    // Key: role name ("admin", "moderator", etc.), Value: RoleConfig instance
    private Map<String, RoleConfig> roles;

    public Map<String, RoleConfig> getRoles() {
        return roles;
    }

    public void setRoles(Map<String, RoleConfig> roles) {
        this.roles = roles;
    }
}

