package com.lyttldev.mapleadmin.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Message {
    public static void send(CommandSender sender, String message ) {
        send(sender, message, "&a&lMapleAdmin&r &8- &7&o");
    }
    public static void sendBulk(CommandSender sender, String message ) {
        send(sender, message, "&a&lMapleAdmin&r\n&7");
    }

    public static void send(CommandSender sender, String message, String prefix) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message + "&r"));
    }
}
