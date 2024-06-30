package com.lyttldev.lyttleadmin;

import com.lyttldev.lyttleadmin.commands.StaffCommand;
import com.lyttldev.lyttleadmin.database.SQLite;
import com.lyttldev.lyttleadmin.handlers.PlayerJoinListener;
import com.lyttldev.lyttleadmin.utils.Console;
import com.lyttldev.lyttleadmin.utils.Message;
import org.bukkit.plugin.java.JavaPlugin;

public final class LyttleAdmin extends JavaPlugin {
    public SQLite sqlite;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Initialize and connect to SQLite database
        sqlite = new SQLite();
        sqlite.connect(this);
        sqlite.createTable();

        // Plugin startup logic
        Console.init(this);
        Message.init(this);

        // Commands
        new StaffCommand(this);

        // Listeners
        new PlayerJoinListener(this);
    }

    @Override
    public void onDisable() {
        // Close SQLite database connection
        if (sqlite != null) {
            sqlite.closeConnection();
        }
    }
}
