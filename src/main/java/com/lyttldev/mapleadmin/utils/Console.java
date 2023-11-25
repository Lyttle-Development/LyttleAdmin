package com.lyttldev.mapleadmin.utils;

import com.lyttldev.mapleadmin.MapleAdmin;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public class Console {
    public static MapleAdmin plugin;

    public static void init(MapleAdmin plugin) {
        Console.plugin = plugin;
    }

    public static void run(String command) {
        if (command == null || command.isEmpty()) return;
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        Bukkit.getScheduler().callSyncMethod( plugin, () -> Bukkit.dispatchCommand( console, command ) );
    }
}
