package com.glowstudio.glowduels.arena;

import com.glowstudio.glowduels.GlowDuelsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArenaManager {

    private final GlowDuelsPlugin plugin;
    private final List<Arena> arenas = new ArrayList<>();
    private final Random random = new Random();

    public ArenaManager(GlowDuelsPlugin plugin) {
        this.plugin = plugin;
        loadArenas();
    }

    public void loadArenas() {
        arenas.clear();
        FileConfiguration config = plugin.getConfig();
        if (!config.contains("arenas")) {
            World world = Bukkit.getWorlds().get(0);
            arenas.add(new Arena("default", new Location(world, 0.5, 100, -10.5, 0, 0), new Location(world, 0.5, 100, 10.5, 180, 0)));
            return;
        }

        for (String key : config.getConfigurationSection("arenas").getKeys(false)) {
            String path = "arenas." + key + ".";
            World world = Bukkit.getWorld(config.getString(path + "world", "world"));
            if (world == null) continue;

            Location s1 = new Location(world,
                    config.getDouble(path + "spawn1.x"),
                    config.getDouble(path + "spawn1.y"),
                    config.getDouble(path + "spawn1.z"),
                    (float) config.getDouble(path + "spawn1.yaw"),
                    (float) config.getDouble(path + "spawn1.pitch")
            );

            Location s2 = new Location(world,
                    config.getDouble(path + "spawn2.x"),
                    config.getDouble(path + "spawn2.y"),
                    config.getDouble(path + "spawn2.z"),
                    (float) config.getDouble(path + "spawn2.yaw"),
                    (float) config.getDouble(path + "spawn2.pitch")
            );

            arenas.add(new Arena(key, s1, s2));
        }
    }

    public Arena getRandomArena() {
        if (arenas.isEmpty()) {
            World world = Bukkit.getWorlds().get(0);
            return new Arena("default", new Location(world, 0.5, 100, -10.5, 0, 0), new Location(world, 0.5, 100, 10.5, 180, 0));
        }
        return arenas.get(random.nextInt(arenas.size()));
    }
}
