package com.lyttldev.lyttleadmin.handlers;

import com.lyttldev.lyttleadmin.LyttleAdmin;
import com.lyttldev.lyttleadmin.commands.StaffCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final LyttleAdmin plugin;

    public PlayerJoinListener(LyttleAdmin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        StaffCommand.onPlayerJoin(event.getPlayer());
    }
}
