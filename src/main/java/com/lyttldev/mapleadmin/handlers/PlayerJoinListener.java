package com.lyttldev.mapleadmin.handlers;

import com.lyttldev.mapleadmin.MapleAdmin;
import com.lyttldev.mapleadmin.commands.MapleAdminCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    public PlayerJoinListener(MapleAdmin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        MapleAdminCommand.onPlayerJoin(event.getPlayer());
    }
}
