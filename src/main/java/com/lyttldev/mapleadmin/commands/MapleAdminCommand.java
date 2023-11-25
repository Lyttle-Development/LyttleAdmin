package com.lyttldev.mapleadmin.commands;

import com.lyttldev.mapleadmin.MapleAdmin;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class MapleAdminCommand implements CommandExecutor, TabExecutor {
    private final String staffKey = "staff";
    private final String staffActiveKey = "active";
    private final String staffInventoryKey = "inventory";
    private final String staffLocationKey = "location";
    private final String splitKey = "__________";

    // define plugin
    private final MapleAdmin plugin;

    public MapleAdminCommand(MapleAdmin plugin) {
        plugin.getCommand("staff").setExecutor(this);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        Player player = (Player) sender;

        // Check permissions
        if (!sender.hasPermission("mapleadmin.staff") || (args.length > 0 && args[0].equals("log"))) {
            String page = args.length > 1 ? (args[1] != null ? args[1] : "1") : "1";
            getStaffLog(player, page);
            return true;
        }

        PlayerInventory playerInventory = player.getInventory();

        boolean staffActive = getStaffActive(player);
        if (!staffActive) {
            if (args.length < 1) {
                sender.sendMessage("Please specify a staff mode reason.");
                return true;
            }
            setStaffActive(player, true);
            setStaffLocation(player, player.getLocation());
            appendStaffLog(player, args[0], true);
            // Save inventory
            saveInventory(playerInventory, player);
            onStaffModeEnabled(player);
        } else {
            String reason = args.length > 0 ? args[0] : "Task completed.";
            setStaffActive(player, false);
            Location location = getStaffLocation(player);
            if (location == null) {
                sender.sendMessage("No saved location found.");
            } else {
                player.teleport(location);
            }
            appendStaffLog(player, reason, false);
            // Restore inventory
            restoreInventory(playerInventory, player);
            onStaffModeDisabled(player);
        }

        return true;
    }

    public String getStaffActiveKey(Player player) {
        return staffKey + "." + player.getName() + "." + staffActiveKey;
    }

    public String getStaffInventoryKey(Player player) {
        return staffKey + "." + player.getName() + "." + staffInventoryKey;
    }

    public String getStaffLocationKey(Player player) {
        return staffKey + "." + player.getName() + "." + staffLocationKey;
    }

    private boolean getStaffActive(Player player) {
        FileConfiguration config = this.plugin.getConfig();

        if (config.contains(getStaffActiveKey(player))) {
            return config.getBoolean(getStaffActiveKey(player));
        } else {
            return false;
        }
    }

    private void setStaffActive(Player player, boolean active) {
        FileConfiguration config = this.plugin.getConfig();

        config.set(getStaffActiveKey(player), active);
        this.plugin.saveConfig();
    }

    private void setStaffLocation(Player player, Location location) {
        FileConfiguration config = this.plugin.getConfig();

        config.set(getStaffLocationKey(player), location);

        this.plugin.saveConfig();
    }

    private Location getStaffLocation(Player player) {
        FileConfiguration config = this.plugin.getConfig();

        if (config.contains(getStaffLocationKey(player))) {
            return (Location) config.get(getStaffLocationKey(player));
        } else {
            return null;
        }
    }

    private void saveInventory(PlayerInventory playerInventory, Player player) {
        FileConfiguration config = this.plugin.getConfig();

        // Serialize inventory to Base64
        String serializedInventory = serializeInventory(playerInventory);

        // Save to config
        config.set(getStaffInventoryKey(player), serializedInventory);
        this.plugin.saveConfig();

        playerInventory.clear();
    }

    private void restoreInventory(PlayerInventory playerInventory, Player player) {
        FileConfiguration config = this.plugin.getConfig();

        if (config.contains(getStaffInventoryKey(player))) {
            // Read from config
            String serializedInventory = config.getString(getStaffInventoryKey(player));

            // Deserialize and restore inventory
            deserializeAndRestore(playerInventory, player, serializedInventory);
        } else {
            player.sendMessage("No saved inventory found.");
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

    private void deserializeAndRestore(PlayerInventory playerInventory, Player player, String serializedInventory) {
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
    }

    private void appendStaffLog(Player player, String message, boolean enabled) {
        FileConfiguration config = this.plugin.getConfig();

        String staffLogKey = "staff_log";
        String staffLog = config.getString(staffLogKey);

        if (staffLog == null) {
            staffLog = "";
        }

        // Get current time: "DD/MM/YYYY HH:MM:SS" in the format of a string
        LocalDateTime now = LocalDateTime.now();

        // Define the desired date-time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        // Format the current date and time using the specified format
        String time = now.format(formatter);

        // Create new staff log entry
        staffLog += time + splitKey + (enabled ? "Enabled" : "Disabled") + splitKey + player.getName() + splitKey + message + "\n";

        // Save staff log
        config.set(staffLogKey, staffLog);
    }
    
    private void getStaffLog(Player player, String page) {
        FileConfiguration config = this.plugin.getConfig();

        String staffLogKey = "staff_log";
        String staffLog = config.getString(staffLogKey);

        if (staffLog == null) {
            staffLog = "";
        }

        // Define list of strings
        String[] staffLogList = staffLog.split("\n");

        // reverse list
        staffLogList = Arrays.stream(staffLogList).toArray(String[]::new);
        for (int i = 0; i < staffLogList.length / 2; i++) {
            String temp = staffLogList[i];
            staffLogList[i] = staffLogList[staffLogList.length - i - 1];
            staffLogList[staffLogList.length - i - 1] = temp;
        }


        if (staffLogList.length == 0) {
            player.sendMessage("No staff log found.");
            return;
        }

        // Get page
        int pageInt = Integer.parseInt(page);
        int pageStart = (pageInt - 1) * 10;
        int pageEnd = pageInt * 10;
        int pages = (int) Math.ceil((double) staffLogList.length / 10);

        if (pageInt > pages) {
            player.sendMessage("Page " + page + " does not exist.");
            return;
        }

        // Get page of staff log
        String[] staffLogPage = Arrays.copyOfRange(staffLogList, pageStart, pageEnd);
        // remove null and empty strings
        staffLogPage = Arrays.stream(staffLogPage).filter(s -> (s != null && s.length() > 0)).toArray(String[]::new);

        // Send page of staff log to player
        String message = "Out Staff did the following:\n";
        for (String staffLogPageItem : staffLogPage) {
            String[] staffLogPageItemSplit = staffLogPageItem.split(splitKey);
            String staffLogPageItemTime = staffLogPageItemSplit[0];
            String staffLogPageItemEnabled = staffLogPageItemSplit[1];
            String staffLogPageItemPlayer = staffLogPageItemSplit[2];
            String staffLogPageItemMessage = staffLogPageItemSplit[3];

            String line =
                    "[" + staffLogPageItemTime + "] ("
                    + staffLogPageItemEnabled + ") "
                    + staffLogPageItemPlayer + ": "
                    + staffLogPageItemMessage;

            // Prevent new line on the last item
            if (staffLogPageItem == staffLogPage[staffLogPage.length - 1]) {
                message += line;
            } else {
                message += line + "\n";
            }
        }
        message += "\nPage " + page + "/" + pages;

        player.sendMessage(message);
    }

    private void onStaffModeEnabled(Player player) {
        player.sendMessage("Staff mode enabled!");

        // Check user type
        if (player.hasPermission("mapleadmin.staff.admin")) {
            onStaffModeEnabledAdmin(player);
        } else if (player.hasPermission("mapleadmin.staff.moderator")) {
            onStaffModeEnabledModerator(player);
        }
    }

    private void onStaffModeEnabledAdmin(Player player) {
        player.setGameMode(GameMode.CREATIVE);
    }

    private void onStaffModeEnabledModerator(Player player) {
        // player.setGameMode(GameMode.CREATIVE);
    }

    private void onStaffModeDisabled(Player player) {
        player.sendMessage("Staff mode disabled!");

        // Check user type
        if (player.hasPermission("mapleadmin.staff.admin")) {
            onStaffModeDisabledAdmin(player);
        } else if (player.hasPermission("mapleadmin.staff.moderator")) {
            onStaffModeDisabledModerator(player);
        }
    }

    private void onStaffModeDisabledAdmin(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
    }

    private void onStaffModeDisabledModerator(Player player) {
        // player.setGameMode(GameMode.SURVIVAL);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] arguments) {
        if (arguments.length == 1) {
            return Arrays.asList("log");
        }

        return null;
    }
}
