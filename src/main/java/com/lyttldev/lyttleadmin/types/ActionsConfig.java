package com.lyttldev.lyttleadmin.types;

/**
 * Contains action sets for a role, for enabling/disabling modes.
 */
public class ActionsConfig {
    private RoleAction onEnable;
    private RoleAction onDisable;

    public RoleAction getOnEnable() {
        return onEnable;
    }

    public RoleAction getOnDisable() {
        return onDisable;
    }

    public void setOnEnable(RoleAction onEnable) {
        this.onEnable = onEnable;
    }

    public void setOnDisable(RoleAction onDisable) {
        this.onDisable = onDisable;
    }
}
