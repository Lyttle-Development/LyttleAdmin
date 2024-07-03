package com.lyttldev.lyttleadmin.types;

import com.lyttldev.lyttleadmin.LyttleAdmin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Config {
    private final String pluginFolderPath;
    private final String configPath;
    private YamlConfiguration config = null;
    private LyttleAdmin plugin;

    public Config(LyttleAdmin plugin, String configPath) {
        this.pluginFolderPath = plugin.getDataFolder().getPath();
        this.configPath = configPath;
        this.plugin = plugin;
    }

    private YamlConfiguration getConfig() {
        if (this.config == null) {
            try {
                // read the config as a string
                String configString = Files.readString(Paths.get(pluginFolderPath, configPath));

                // clean the config string
                configString = this.cleanConfig(configString);

                // load the config from the string
                this.config = new YamlConfiguration();
                this.config.loadFromString(configString);

                // save the cleaned config
                this.saveConfig();
            } catch (IOException | InvalidConfigurationException ignored) {
                this.config = null;
            }
        }

        if (this.config == null) {
            plugin.getLogger().severe("Failed to load config " + configPath);
            return null;
        }

        return this.config;
    }

    private String cleanConfig(String configString) {
        // Remove all comments from the configuration
        configString = configString.replaceAll("!!.+", "");

        // Return the cleaned configuration
        return configString;
    }

    private void saveConfig() {
        try {
            // Retrieve the configuration and a backup of the original configuration
            String originalConfigString = config.saveToString();
            String configString = config.saveToString();

            try {
                configString = this.cleanConfig(configString);

                // Save the modified configuration back to the file
                config.loadFromString(configString);
                config.save(new File(pluginFolderPath, configPath));
            } catch (IOException ignored) { }

            // Reload the original config
            config.loadFromString(originalConfigString);
        } catch (InvalidConfigurationException ignored) { }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(new File(pluginFolderPath, configPath));
    }

    public @Nullable Object get(String path) {
        YamlConfiguration cfg = this.getConfig();
        if (cfg.contains(path)) {
            return cfg.get(path);
        } else {
            return null;
        }
    }

    public @Nullable ConfigurationSection getSection(String path) {
        YamlConfiguration cfg = this.getConfig();
        if (cfg.contains(path)) {
            return cfg.getConfigurationSection(path);
        } else {
            return null;
        }
    }

    public void set(String path, @Nullable Object value) {
        YamlConfiguration cfg = this.getConfig();
        cfg.set(path, value);
        this.saveConfig();
    }

    public boolean remove(String path) {
        YamlConfiguration cfg = this.getConfig();
        if (cfg.contains(path)) {
            cfg.set(path, null);
            this.saveConfig();
            return true;
        } else {
            return false;
        }
    }

    public boolean contains(String path) {
        YamlConfiguration cfg = this.getConfig();
        return cfg.contains(path);
    }

    public boolean containsLowercase(String path) {
        YamlConfiguration cfg = this.getConfig();
        // Check if it contains it, but check with all to lower case.
        for (String key : cfg.getKeys(false)) {
            if (key.toLowerCase().equals(path.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public String[] getKeys(String path) {
        YamlConfiguration cfg = this.getConfig();
        if (cfg.contains(path)) {
            return cfg.getConfigurationSection(path).getKeys(false).toArray(new String[0]);
        } else {
            return null;
        }
    }

    public Object[] getAll(String path) {
        YamlConfiguration cfg = this.getConfig();
        if (cfg.contains(path)) {
            return cfg.getConfigurationSection(path).getValues(false).values().toArray(new Object[0]);
        } else {
            return null;
        }
    }

    public void clear() {
        File configFile = new File(pluginFolderPath, configPath);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        // Clear all existing keys in the root of the configuration
        for (String key : config.getKeys(false)) {
            config.set(key, null);
        }

        try {
            // Save the modified configuration back to the file
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.reload();
    }
}