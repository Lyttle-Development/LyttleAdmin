package com.lyttldev.lyttleadmin.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Adjust package if needed!

public class RolesConfig {
    private Map<String, RoleConfig> roles = new HashMap<>();
    private final List<String> permissions = new ArrayList<>();

    public RolesConfig() {}

    public Map<String, RoleConfig> getRoles() {
        return roles;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void addRole(RoleConfig config) {
        if (roles == null) roles = new HashMap<>();
        roles.put(config.getPermission(), config);
        permissions.add(config.getPermission());
    }
}