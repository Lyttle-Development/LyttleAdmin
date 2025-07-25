package com.lyttldev.lyttleadmin.types;

/**
 * Represents a single role's configuration.
 * Contains name, permission node, and actions.
 */
public class RoleConfig {
    private String name;
    private String permission;
    private ActionsConfig actions;

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public ActionsConfig getActions() {
        return actions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setActions(ActionsConfig actions) {
        this.actions = actions;
    }
}
