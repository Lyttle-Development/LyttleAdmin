package com.lyttldev.lyttleadmin;

import com.lyttldev.lyttleadmin.commands.*;
//import com.lyttldev.lyttleadmin.handlers.*;
import com.lyttldev.lyttleadmin.handlers.Listener_PlayerJoin;
import com.lyttldev.lyttleadmin.utils.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class LyttleAdmin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Plugin startup logic
        Console.init(this);
        Message.init(this);

        // Commands
        new Command_Staff(this);

        // Listeners
        new Listener_PlayerJoin(this);
    }
}
