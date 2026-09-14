package com.glowstudio.glowduels.arena;

import com.glowstudio.glowduels.GlowDuelsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;

public class MapManager {

    private final GlowDuelsPlugin plugin;

    public MapManager(GlowDuelsPlugin plugin) {
        this.plugin = plugin;
        verifyMapsDirectory();
    }

    private void verifyMapsDirectory() {
        File mapsDir = new File(plugin.getDataFolder(), "maps");
        if (!mapsDir.exists()) {
            mapsDir.mkdirs();
        }
    }

    public void loadWorld(String worldName) {
        if (Bukkit.getWorld(worldName) == null) {
            WorldCreator creator = new WorldCreator(worldName);
            creator.createWorld();
        }
    }
}
