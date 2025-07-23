package com.lyttldev.lyttleadmin.types;

import org.bukkit.GameMode;

/**
 * Contains action sets for a role, for enabling/disabling modes.
 */
public class ActionsConfig {
    private RoleAction on_enable;
    private RoleAction on_disable;
    private String gamemode;

    public RoleAction getOn_enable() {
        return on_enable;
    }

    public RoleAction getOn_disable() {
        return on_disable;
    }

    public void setOn_enable(RoleAction on_enable) {
        this.on_enable = on_enable;
    }

    public void setOn_disable(RoleAction on_disable) {
        this.on_disable = on_disable;
    }
}
