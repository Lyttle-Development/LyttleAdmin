package com.lyttldev.lyttleadmin.commands;

import com.lyttldev.lyttleadmin.LyttleAdmin;
import com.lyttldev.lyttleadmin.database.Inventory;
import com.lyttldev.lyttleadmin.database.Log;
import com.lyttldev.lyttleadmin.database.SQLite;
import com.lyttldev.lyttleadmin.types.*;
import com.lyttldev.lyttleadmin.utils.LocationUtil;
import com.lyttldev.lyttleadmin.utils.RolesConfigLoader;
import com.lyttledev.lyttleutils.types.Message.Replacements;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
import java.util.*;
import java.util.logging.Level;

public class StaffCommand implements CommandExecutor, TabExecutor {
    // define plugin
    private static LyttleAdmin plugin;
    private final SQLite sqlite;
    private static final MiniMessage mini = MiniMessage.miniMessage();

    public StaffCommand(LyttleAdmin plugin) {
        plugin.getCommand("staff").setExecutor(this);
        this.plugin = plugin;
        this.sqlite = plugin.sqlite;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.message.sendMessage(sender,"must_be_player");
            return true;
        }

        Player player = (Player) sender;

        // Check permissions
        if (!sender.hasPermission("lyttleadmin.staff") || (args.length > 0 && args[0].equals("log"))) {
            if (args.length > 0 && args[0].equals("--restore")) {
                plugin.message.sendMessage(player, "no_permission");
                return true;
            }
            String page = args.length > 1 ? (args[1] != null ? args[1] : "1") : "1";
            getStaffLog(player, page);
            return true;
        }

        // Check if the args is --restore
        if (args.length > 0 && args[0].equals("--restore")) {
            if (args.length < 3) {
                plugin.message.sendMessage(player, "staff_usage");
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
                plugin.message.sendMessage(player, "staff_no_reason");
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
                plugin.message.sendMessage(player, "staff_no_location");
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
                plugin.message.sendMessage(player, "staff_no_location");
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
            plugin.message.sendMessage(player, "staff_no_inventory");
        }
    }

    private void restoreLostInventory(PlayerInventory playerInventory, Player player, Timestamp datetime) {
        Timestamp date = new Timestamp(datetime.getTime());
        Inventory inventory = sqlite.getInventory(player.getUniqueId().toString(), date);

        if (inventory != null) {
            // Read from config
            String serializedInventory = inventory.getInventoryContents();

            // Deserialize and restore inventory
            deserializeAndRestore(playerInventory, player, serializedInventory, 0);
            inventory.setEnabled(false);
            sqlite.updateInventory(inventory);
        } else {
            plugin.message.sendMessage(player, "staff_no_inventory");
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
                plugin.message.sendMessage(player, "staff_inventory_restore_failed");
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

        String header = ((TextComponent) plugin.message.getMessage("staff_log")).content(); // Assuming this returns a MiniMessage string
        String fullMessage = header + logBuilder;

        plugin.message.sendMessageRaw(player, MiniMessage.miniMessage().deserialize(fullMessage));
    }

    private void onStaffModeEnabled(Player player, String reason, int tries) {
        try {
            Replacements replacements = new Replacements.Builder()
                .add("<PLAYER>", player.getName())
                .add("<REASON>", reason)
                .build();

            runActions(player, replacements, true);
        } catch (Exception e) {
            plugin.console.log("Error enabling staff mode for player " + player.getName() + ": " + e.getMessage());
            if (tries > 10) {
                plugin.message.sendMessage(player, "staff_enable_failed");
                return;
            }

            onStaffModeEnabled(player, reason, tries + 1);
        }
    }

    private void onStaffModeDisabled(Player player, String reason, boolean doNotAnnounce, int tries) {
        try {
            Replacements replacements = new Replacements.Builder()
                .add("<PLAYER>", player.getName())
                .add("<REASON>", reason)
                .build();

            runActions(player, replacements, false);
        } catch (Exception e) {
            plugin.console.log("Error disabling staff mode for player " + player.getName() + ": " + e.getMessage());
            if (tries > 10) {
                plugin.message.sendMessage(player, "staff_disable_failed");
                return;
            }
            onStaffModeDisabled(player, reason, doNotAnnounce, tries + 1);
        }
    }

    private void runActions(Player player, Replacements replacements, boolean enable) {
        Object rolesObj = plugin.config.general.get("roles");
        RolesConfig rolesConfig = RolesConfigLoader.fromConfig(rolesObj); // convert to POJO
        if (rolesConfig == null) {
            plugin.message.sendMessage(player, "roles_config_invalid");
            return;
        }

        Map<String, RoleConfig> roles = rolesConfig.getRoles();
        if (roles == null || roles.isEmpty()) {
            plugin.message.sendMessage(player, "roles_not_found");
            return;
        }

        for (RoleConfig role : roles.values()) {
            String permission = role.getPermission();
            if (permission == null || permission.isEmpty()) {
                plugin.message.sendMessage(player, "role_permission_not_found");
                continue;
            }

            if (!player.hasPermission(permission)) { continue; }

            ActionsConfig roleActions = role.getActions();
            if (roleActions == null) {
                plugin.message.sendMessage(player, "role_actions_not_found");
                return;
            }

            if (enable) {
                RoleAction action = roleActions.getOn_enable();

                RoleChange remove = action.getRemove();
                if (remove != null) {
                    // Set Operator status
                    if (remove.isOperator()) {
                        setOperator(player, false);
                    }

                    // Set game mode
                    for (String item : remove.getRoles()) {
                        setRole(player, item, false);
                    }
                }

                RoleChange give = action.getGive();
                if (give != null) {
                    // Set Operator status
                    if (give.isOperator()) {
                        setOperator(player, true);
                    }

                    // Set game mode
                    for (String item : give.getRoles()) {
                        setRole(player, item, true);
                    }
                }

                // Set game mode
                GameMode gameMode = action.getGameMode();
                if (gameMode != null) {
                    setGameMode(player, gameMode);
                }

                // Send broadcast message
                BroadcastConfig broadcastConfig = action.getBroadcast();
                if (broadcastConfig != null) {
                    sendBroadcast(broadcastConfig.getMessage(), replacements, broadcastConfig.isGlobal(), broadcastConfig.getPermission());
                }
            } else {
                RoleAction action = roleActions.getOn_disable();

                RoleChange remove = action.getRemove();
                if (remove != null) {
                    // Set Operator status
                    if (remove.isOperator()) {
                        setOperator(player, false);
                    }

                    // Set game mode
                    for (String item : remove.getRoles()) {
                        setRole(player, item, false);
                    }
                }

                RoleChange give = action.getGive();
                if (give != null) {
                    // Set Operator status
                    if (give.isOperator()) {
                        setOperator(player, true);
                    }

                    // Set game mode
                    for (String item : give.getRoles()) {
                        setRole(player, item, true);
                    }
                }

                // Set game mode
                GameMode gameMode = action.getGameMode();
                if (gameMode != null) {
                    setGameMode(player, gameMode);
                }

                // Send broadcast message
                BroadcastConfig broadcastConfig = action.getBroadcast();
                if (broadcastConfig != null) {
                    sendBroadcast(broadcastConfig.getMessage(), replacements, broadcastConfig.isGlobal(), broadcastConfig.getPermission());
                }
            }
        }
    }

    private void setGameMode(Player player, GameMode gameMode) {
        if (player != null && gameMode != null) {
            player.setGameMode(gameMode);
        }
    }

    private void setOperator(Player player, boolean isOp) {
        if (player != null) {
            if (isOp) {
                plugin.console.run("op " + player.getName());
            } else {
                plugin.console.run("deop " + player.getName());
            }
        }
    }

    private void setRole(Player player, String role, boolean add) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        Node node = Node.builder("group." + role).build();
        if (add) {
            luckPerms.getUserManager().modifyUser(player.getUniqueId(), user -> user.data().add(node));
        } else {
            luckPerms.getUserManager().modifyUser(player.getUniqueId(), user -> user.data().remove(node));
        }
    }

    private void sendBroadcast(String message, Replacements replacements, boolean global, String permission) {
        if (global) {
            plugin.message.sendBroadcast(true, plugin.message.getMessageRaw(message, replacements));
        } else {
            Player[] permissionPlayers = Bukkit.getOnlinePlayers().stream()
                    .filter(p -> p.hasPermission(permission))
                    .toArray(Player[]::new);
            for (Player permissionPlayer : permissionPlayers) {
                plugin.message.sendMessageRaw(permissionPlayer, plugin.message.getMessageRaw(message, replacements), replacements);
            }
        }
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
