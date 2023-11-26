package com.lyttldev.mapleadmin.utils;

import com.lyttldev.mapleadmin.MapleAdmin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class Message {
    public static MapleAdmin plugin;

    public static void init(MapleAdmin plugin) {
        Message.plugin = plugin;
    }

    public static String getPrefix() {
        return plugin.getConfig().getString("prefix");
    }

    public static void sendPlayer(Player player, String message, boolean prefix) {
        if (prefix) {
            player.sendMessage(getMessage(getPrefix() + message));
            return;
        }
        player.sendMessage(getMessage(message));
    }

    public static void sendConsole(String message) {
        Console.log(getMessage(message));
    }

    public static void sendChat(String message, boolean prefix) {
        if (prefix) {
            Bukkit.broadcastMessage(getMessage(getPrefix() + message));
            return;
        }
        Bukkit.broadcastMessage(getMessage(message));
    }

    public static String getMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message + "&r");
    }
}
