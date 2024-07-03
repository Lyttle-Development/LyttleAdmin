package com.lyttldev.lyttleadmin.utils;

import com.lyttldev.lyttleadmin.LyttleAdmin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class Message {
    public static LyttleAdmin plugin;

    public static void init(LyttleAdmin plugin) {
        Message.plugin = plugin;
    }

    private static String _getPrefix() {
        return _getConfigMessage("prefix");
    }

    private static String _getConfigMessage(String messageKey) {
        @Nullable String message = (String) plugin.config.messages.get(messageKey);
        if (message == null) {
            Console.log("Message key " + messageKey + " not found in messages.yml");
            message = (String) plugin.config.messages.get("message_not_found");
        }

        if (message == null) {
            Console.log("Even the message_not_found not found in messages.yml...");
            message = "&cOh... I can't react to that. (Contact the Administrators)";
        }

        return message;
    }

    public static String getConfigMessage(String messageKey) {
        return _getConfigMessage(messageKey);
    }

    private static String _replaceMessageStrings(String message, String[][] replacements) {
        for (String[] replacement : replacements) {
            message = message.replace(replacement[0], replacement[1]);
        }
        return message;
    }

    public static void sendPlayer(Player player, String message) {
        String msg = _getConfigMessage(message);
        player.sendMessage(_getMessage(_getPrefix() + msg));
    }

    public static void sendPlayer(Player player, String message, @Nullable String[][] replacements) {
        String msg = _replaceMessageStrings(_getConfigMessage(message), replacements);
        player.sendMessage(_getMessage(_getPrefix() + msg));
    }

    public static void sendPlayerRaw(Player player, String message) {
        player.sendMessage(_getMessage(_getPrefix() + message));
    }

    public static void sendConsole(String message, String[][] replacements) {
        Console.log(_getMessage(_replaceMessageStrings(_getConfigMessage(message), replacements)));
    }

    public static void sendConsole(String message) {
        Console.log(_getConfigMessage(message));
    }

    public static void sendConsoleRaw(String message) {
        Console.log(_getMessage(message));
    }

    public static void sendBroadcast(String message, String[][] replacements) {
        String msg = _replaceMessageStrings(_getConfigMessage(message), replacements);
        Bukkit.broadcastMessage(_getMessage(msg));
    }

    public static void sendBroadcast(String message, String[][] replacements, boolean prefix) {
        String msg = _replaceMessageStrings(_getConfigMessage(message), replacements);
        if (prefix) {
            Bukkit.broadcastMessage(_getMessage(_getPrefix() + msg));
            return;
        }
        Bukkit.broadcastMessage(_getMessage(msg));
    }

    public static void sendBroadcastRaw(String message, boolean prefix) {
        if (prefix) {
            Bukkit.broadcastMessage(_getMessage(_getPrefix() + message));
            return;
        }
        Bukkit.broadcastMessage(_getMessage(message));
    }

    private static String _getMessage(String message) {
        // Replace all \n with real newlines
        message = message.replace("\\n", "\n");
        return ChatColor.translateAlternateColorCodes('&', message + "&r");
    }

    public static String getMessage(String message, String[][] replacements) {
        return _getMessage(_replaceMessageStrings(_getConfigMessage(message), replacements));
    }

    public static String getMessage(String message) {
        return _getMessage(_getConfigMessage(message));
    }
}
