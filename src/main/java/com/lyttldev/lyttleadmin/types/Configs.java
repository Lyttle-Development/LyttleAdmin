package com.lyttldev.lyttleadmin.types;

import com.lyttldev.lyttleadmin.LyttleAdmin;
import com.lyttledev.lyttleutils.types.YamlConfig;

public class Configs {
    private final LyttleAdmin plugin;

    // Configs
    public YamlConfig general;
    public YamlConfig messages;

    // Default configs
    public YamlConfig defaultGeneral;
    public YamlConfig defaultMessages;


    public Configs(LyttleAdmin plugin) {
        this.plugin = plugin;

        // Configs
        general = new YamlConfig(plugin, "config.yml");
        messages = new YamlConfig(plugin, "messages.yml");

        // Default configs
        defaultGeneral = new YamlConfig(plugin, "#defaults/config.yml");
        defaultMessages = new YamlConfig(plugin, "#defaults/messages.yml");
    }

    public void reload() {
        general.reload();
        messages.reload();

        plugin.reloadConfig();
    }

    private String getConfigPath(String path) {
        return plugin.getConfig().getString("configs." + path);
    }
}
