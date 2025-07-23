package com.lyttldev.lyttleadmin.types;

import java.util.HashMap;
import java.util.Map;

// Adjust package if needed!

public class RolesConfig {
    private Map<String, RoleConfig> roles = new HashMap<>();

    public RolesConfig() {}

    public Map<String, RoleConfig> getRoles() {
        return roles;
    }

    public void addRole(String name, RoleConfig config) {
        if (roles == null) roles = new HashMap<>();
        roles.put(name, config);
    }
}