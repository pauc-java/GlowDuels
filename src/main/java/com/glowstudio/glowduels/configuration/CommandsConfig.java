package com.glowstudio.glowduels.configuration;

import com.glowstudio.glowduels.GlowDuelsPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class CommandsConfig {

    private final GlowDuelsPlugin plugin;
    private File file;
    private FileConfiguration config;

    public CommandsConfig(GlowDuelsPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        if (file == null) {
            file = new File(plugin.getDataFolder(), "commands.yml");
        }
        if (!file.exists()) {
            plugin.saveResource("commands.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);

        InputStream defStream = plugin.getResource("commands.yml");
        if (defStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defStream, StandardCharsets.UTF_8));
            config.setDefaults(defConfig);
        }
    }

    public List<String> getBlockedCommands() {
        return config.getStringList("blocked-commands");
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
