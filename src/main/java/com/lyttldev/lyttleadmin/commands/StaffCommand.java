package com.lyttldev.lyttleadmin.commands;

import com.lyttldev.lyttleadmin.LyttleAdmin;
import com.lyttldev.lyttleadmin.database.Inventory;
import com.lyttldev.lyttleadmin.database.Log;
import com.lyttldev.lyttleadmin.database.SQLite;
import com.lyttldev.lyttleadmin.utils.Console;
import com.lyttldev.lyttleadmin.utils.LocationUtil;
import com.lyttldev.lyttleadmin.utils.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class StaffCommand implements CommandExecutor, TabExecutor {
    // define plugin
    private final LyttleAdmin plugin;
    private final SQLite sqlite;

    public StaffCommand(LyttleAdmin plugin) {
        plugin.getCommand("staff").setExecutor(this);
        this.plugin = plugin;
        this.sqlite = plugin.sqlite;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Message.sendMessage(sender,"must_be_player");
            return true;
        }

        Player player = (Player) sender;

        // Check permissions
        if (!sender.hasPermission("lyttleadmin.staff") || (args.length > 0 && args[0].equals("log"))) {
            if (args.length > 0 && args[0].equals("--restore")) {
                Message.sendMessage(player, "no_permission");
                return true;
            }
            String page = args.length > 1 ? (args[1] != null ? args[1] : "1") : "1";
            getStaffLog(player, page);
            return true;
        }

        // Check if the args is --restore
        if (args.length > 0 && args[0].equals("--restore")) {
            if (args.length < 3) {
                Message.sendMessage(player, "staff_usage");
                return true;
            }
            try {
                // Converts 2024-01-01 00:00:00 to a timestamp, found in args[1] and args[2]
                String dateTimeString = args[1] + " " + args[2];
                Timestamp timestamp = Timestamp.valueOf(dateTimeString);
                timestamp.setNanos(0);
                PlayerInventory playerInventory = player.getInventory();
                restoreLostInventory(playerInventory, player, timestamp);
                return true;
            } catch (Exception e) {
                sender.sendMessage("Invalid date or time format. Usage: /staff --restore <date> <time>");
                return true;
            }
        }

        PlayerInventory playerInventory = player.getInventory();

        boolean staffActive = getStaffActive(player);
        if (!staffActive) {
            if (args.length < 1) {
                Message.sendMessage(player, "staff_no_reason");
                return true;
            }
            // join the args into a string
            String reason = args.length > 0 ? String.join(" ", Arrays.copyOfRange(args, 0, args.length)) : "Task completed.";
            appendStaffLog(player, reason, true);
            // Save inventory
            saveInventory(playerInventory, player);
            onStaffModeEnabled(player, reason, 0);
            actionBar(true, player);
        } else {
            Location location = getStaffLocation(player);
            if (location == null) {
                Message.sendMessage(player, "staff_no_location");
            } else {
                player.teleport(location);
            }
            // join the args into a string
            String reason = args.length > 0 ? String.join(" ", Arrays.copyOfRange(args, 0, args.length)) : "Task completed.";
            appendStaffLog(player, reason, false);
            // Restore inventory
            restoreInventory(playerInventory, player);
            onStaffModeDisabled(player, reason, false, 0);
            actionBar(false, player);
        }

        return true;
    }

    HashMap<Player, BukkitTask> activeActionBar = new HashMap<>();
    private void actionBar(boolean active, Player player) {
        if (active) {
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                player.sendActionBar(Component.text("STAFF MODE ACTIVE").color(NamedTextColor.RED));
            }, 0, 40);
            activeActionBar.put(player, task);
        } else {
            BukkitTask task = activeActionBar.get(player);
            task.cancel();
        }
    }

    public static void onPlayerJoin(Player player) {
        StaffCommand commandStaff = new StaffCommand(LyttleAdmin.getPlugin(LyttleAdmin.class));
        boolean staffActive = commandStaff.getStaffActive(player);
        if (staffActive) {
            PlayerInventory playerInventory = player.getInventory();

            Location location = commandStaff.getStaffLocation(player);
            if (location == null) {
                Message.sendMessage(player, "staff_no_location");
            } else {
                player.teleport(location);
            }

            commandStaff.appendStaffLog(player, "Task completed.", false);
            commandStaff.restoreInventory(playerInventory, player);
            commandStaff.onStaffModeDisabled(player, "Task completed.", true, 0);
        }
    }

    private boolean getStaffActive(Player player) {
        Inventory inventory = sqlite.getInventory(player.getUniqueId().toString());

        if (inventory != null) {
            return inventory.getEnabled();
        } else {
            return false;
        }
    }

    private Location getStaffLocation(Player player) {
        Inventory inventory = sqlite.getInventory(player.getUniqueId().toString());

        if (inventory != null) {
            String locationString = inventory.getLocation();
            return LocationUtil.stringToLocation(locationString);
        } else {
            return null;
        }
    }

    private void saveInventory(PlayerInventory playerInventory, Player player) {
        // Serialize inventory to Base64
        String serializedInventory = serializeInventory(playerInventory);

        Timestamp datetime = new Timestamp(System.currentTimeMillis());
        datetime.setNanos(0);

        Inventory inventory = new Inventory(0, player.getUniqueId().toString(), player.getName(), LocationUtil.locationToString(player.getLocation()), true, datetime, serializedInventory);
        sqlite.insertInventory(inventory);

        playerInventory.clear();
    }

    private void restoreInventory(PlayerInventory playerInventory, Player player) {
        Inventory inventory = sqlite.getInventory(player.getUniqueId().toString());

        if (inventory != null) {
            // Read from config
            String serializedInventory = inventory.getInventoryContents();

            // Deserialize and restore inventory
            deserializeAndRestore(playerInventory, player, serializedInventory, 0);
            inventory.setEnabled(false);
            sqlite.updateInventory(inventory);
        } else {
            Message.sendMessage(player, "staff_no_inventory");
        }
    }

    private void restoreLostInventory(PlayerInventory playerInventory, Player player, Timestamp datetime) {
        java.sql.Timestamp date = new java.sql.Timestamp(datetime.getTime());
        Inventory inventory = sqlite.getInventory(player.getUniqueId().toString(), date);

        if (inventory != null) {
            // Read from config
            String serializedInventory = inventory.getInventoryContents();

            // Deserialize and restore inventory
            deserializeAndRestore(playerInventory, player, serializedInventory, 0);
            inventory.setEnabled(false);
            sqlite.updateInventory(inventory);
        } else {
            Message.sendMessage(player, "staff_no_inventory");
        }
    }

    private String serializeInventory(PlayerInventory playerInventory) {
        StringBuilder serialized = new StringBuilder();
        for (ItemStack item : playerInventory.getContents()) {
            if (item != null) {
                // Serialize each item to Base64
                String serializedItem = Base64.getEncoder().encodeToString(item.serializeAsBytes());
                serialized.append(serializedItem).append(";");
            } else {
                serialized.append("null;");
            }
        }
        return serialized.toString();
    }

    private void deserializeAndRestore(PlayerInventory playerInventory, Player player, String serializedInventory, int tries) {
        try {
            String[] serializedItems = serializedInventory.split(";");
            ItemStack[] inventoryContents = new ItemStack[serializedItems.length];

            for (int i = 0; i < serializedItems.length; i++) {
                String serializedItem = serializedItems[i];
                if (!serializedItem.equals("null")) {
                    // Deserialize each item from Base64
                    byte[] serializedData = Base64.getDecoder().decode(serializedItem);
                    inventoryContents[i] = ItemStack.deserializeBytes(serializedData);
                }
            }

            // Clear current inventory
            playerInventory.clear();

            // Restore inventory
            playerInventory.setContents(inventoryContents);
        } catch (Exception e) {
            if (tries > 10) {
                Message.sendMessage(player, "staff_inventory_restore_failed");
                return;
            }
            deserializeAndRestore(playerInventory, player, serializedInventory, tries + 1);
        }
    }

    private void appendStaffLog(Player player, String message, boolean enabled) {
        Timestamp datetime = new Timestamp(System.currentTimeMillis());
        datetime.setNanos(0);
        Log log = new Log(0, player.getUniqueId().toString(), player.getName(), datetime, enabled, message);
        sqlite.insertLog(log);
    }

    private void getStaffLog(Player player, String page) {
        int selectedPage = !page.isEmpty() ? Integer.parseInt(page) : 1;
        List<Log> logs = sqlite.getLogs(10, selectedPage - 1);

        StringBuilder logBuilder = new StringBuilder();

        for (Log log : logs) {
            logBuilder.append("\n")
                    .append("<gray>[<white>")
                    .append(log.getDateCreated())
                    .append("<gray>] ")
                    .append(log.getEnabled() ? "<green>+" : "<red>-")
                    .append(" <blue>")
                    .append(log.getUsername())
                    .append("<gray>: <white>")
                    .append(log.getMessage());
        }

        String header = ((TextComponent) Message.getMessage("staff_log")).content(); // Assuming this returns a MiniMessage string
        String fullMessage = header + logBuilder;

        Message.sendMessageRaw(player, fullMessage);
    }


    private void giveRole(Player player, String role) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        Node node = Node.builder("group." + role).build();
        luckPerms.getUserManager().modifyUser(player.getUniqueId(), user -> user.data().add(node));
    }

    private void removeRole(Player player, String role) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        Node node = Node.builder("group." + role).build();
        luckPerms.getUserManager().modifyUser(player.getUniqueId(), user -> user.data().remove(node));
    }

    private void onStaffModeEnabled(Player player, String reason, int tries) {
        try {
            String[][] messageReplacements = {
                { "<USER>", player.getName() },
                { "<REASON>", reason },
            };
            Message.sendBroadcast("staff_enabled", messageReplacements, true);

            // Check user type
            if (player.hasPermission("lyttleadmin.staff.admin")) {
                onStaffModeEnabledAdmin(player);
            } else if (player.hasPermission("lyttleadmin.staff.moderator")) {
                onStaffModeEnabledModerator(player);
            }
        } catch (Exception e) {
            if (tries > 10) {
                Message.sendMessage(player, "staff_enable_failed");
                return;
            }

            onStaffModeEnabled(player, reason, tries + 1);
        }
    }

    private void onStaffModeEnabledAdmin(Player player) {
        player.setGameMode(GameMode.CREATIVE);
        giveRole(player, "admin_active");
        Console.run("op " + player.getName());
    }

    private void onStaffModeEnabledModerator(Player player) {
        player.setGameMode(GameMode.CREATIVE);
        giveRole(player, "mod_active");
    }

    private void onStaffModeDisabled(Player player, String reason, boolean doNotAnnounce, int tries) {
        try {
            if (!doNotAnnounce) {
                String[][] messageReplacements = {
                    { "<USER>", player.getName() },
                    { "<REASON>", reason },
                };
                Message.sendBroadcast("staff_disabled", messageReplacements, true);
            }

            // Check user type
            if (player.hasPermission("lyttleadmin.staff.admin")) {
                onStaffModeDisabledAdmin(player);
            } else if (player.hasPermission("lyttleadmin.staff.moderator")) {
                onStaffModeDisabledModerator(player);
            }
        } catch (Exception e) {
            if (tries > 10) {
                Message.sendMessage(player, "staff_disable_failed");
                return;
            }
            onStaffModeDisabled(player, reason, doNotAnnounce, tries + 1);
        }
    }

    private void onStaffModeDisabledAdmin(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        removeRole(player, "admin_active");
        Console.run("deop " + player.getName());
    }

    private void onStaffModeDisabledModerator(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        removeRole(player, "mod_active");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] arguments) {
        if (arguments.length == 1) {
            if (sender.hasPermission("lyttleadmin.staff")) {
                return Arrays.asList("log", "--restore");
            }
            return List.of("log");
        }

        return List.of();
    }
}
