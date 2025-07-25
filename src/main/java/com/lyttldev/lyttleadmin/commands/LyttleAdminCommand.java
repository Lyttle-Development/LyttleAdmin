package com.lyttldev.lyttleadmin.commands;

import com.lyttldev.lyttleadmin.LyttleAdmin;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class LyttleAdminCommand implements CommandExecutor, TabCompleter {
    private final LyttleAdmin plugin;

    public LyttleAdminCommand(LyttleAdmin plugin) {
        plugin.getCommand("lyttleadmin").setExecutor(this);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check for permission
        if (!(sender.hasPermission("lyttleadmin.lyttleadmin"))) {
            plugin.message.sendMessage(sender, "no_permission");
            return true;
        }

        if (args.length == 0) {
            Component message = Component.text("plugin version: " + plugin.getDescription().getVersion());
            plugin.message.sendMessageRaw(sender, message);
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.config.reload();
                plugin.message.sendMessageRaw(sender, Component.text("The config has been reloaded"));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] arguments) {
        if (arguments.length == 1) {
            return List.of("reload");
        }
        return List.of();
    }
}
