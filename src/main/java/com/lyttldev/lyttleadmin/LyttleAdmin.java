package com.lyttldev.lyttleadmin;

import com.lyttldev.lyttleadmin.commands.Command_Staff;
import com.lyttldev.lyttleadmin.database.SQLite;
import com.lyttldev.lyttleadmin.handlers.Listener_PlayerJoin;
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
        new Command_Staff(this);

        // Listeners
        new Listener_PlayerJoin(this);
    }

    @Override
    public void onDisable() {
        // Close SQLite database connection
        if (sqlite != null) {
            sqlite.closeConnection();
        }
    }
}
