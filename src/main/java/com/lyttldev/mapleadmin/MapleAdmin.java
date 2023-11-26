package com.lyttldev.mapleadmin;

import com.lyttldev.mapleadmin.commands.*;
//import com.lyttldev.mapleadmin.handlers.*;
import com.lyttldev.mapleadmin.handlers.PlayerJoinListener;
import com.lyttldev.mapleadmin.utils.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class MapleAdmin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Plugin startup logic
        Console.init(this);
        Message.init(this);

        // Commands
        new MapleAdminCommand(this);

        // Listeners
        new PlayerJoinListener(this);
    }
}
