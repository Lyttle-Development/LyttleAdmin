package com.lyttldev.lyttleadmin.types;

import org.bukkit.GameMode;

/**
 * Represents the actions taken when enabling/disabling a mode.
 */
public class RoleAction {
    private RoleChange give;
    private RoleChange remove;
    private String gamemode;
    private String actionBar;
    private BroadcastConfig broadcast;

    public RoleChange getGive() {
        return give;
    }

    public RoleChange getRemove() {
        return remove;
    }

    public GameMode getGameMode() {
        if (gamemode == null || gamemode.isEmpty()) {
            return null;
        }
        return switch (gamemode.toUpperCase()) {
            case "SURVIVAL" -> GameMode.SURVIVAL;
            case "CREATIVE" -> GameMode.CREATIVE;
            case "ADVENTURE" -> GameMode.ADVENTURE;
            case "SPECTATOR" -> GameMode.SPECTATOR;
            default -> null;
        };
    }

    public String getActionBar() {
        return actionBar;
    }

    public BroadcastConfig getBroadcast() {
        return broadcast;
    }

    public void setGive(RoleChange give) {
        this.give = give;
    }

    public void setRemove(RoleChange remove) {
        this.remove = remove;
    }

    public void setGamemode(String gamemode) {
        this.gamemode = gamemode;
    }

    public void setActionBar(String actionBar) {
        this.actionBar = actionBar;
    }

    public void setBroadcast(BroadcastConfig broadcast) {
        this.broadcast = broadcast;
    }
}
