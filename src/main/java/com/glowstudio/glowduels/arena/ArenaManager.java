package com.glowstudio.glowduels.arena;

import com.glowstudio.glowduels.GlowDuelsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
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
            return;
        }

        File mapsFolder = new File(plugin.getDataFolder(), "maps");

        for (String key : config.getConfigurationSection("arenas").getKeys(false)) {
            String path = "arenas." + key + ".";
            String worldName = config.getString(path + "world", key);

            File worldDir = new File(Bukkit.getWorldContainer(), worldName);
            File mapDirInPlugin = new File(mapsFolder, worldName);

            if (!worldDir.exists() && !mapDirInPlugin.exists()) {
                plugin.getLogger().warning("Арена '" + key + "' пропущена: мир '" + worldName + "' не найден ни на сервере, ни в папке plugins/GlowDuels/maps!");
                continue;
            }

            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                WorldCreator creator = new WorldCreator(worldName);
                world = creator.createWorld();
            }

            if (world == null) {
                plugin.getLogger().warning("Не удалось загрузить мир для арены: " + worldName);
                continue;
            }

            Location s1 = new Location(world,
                    config.getDouble(path + "spawn1.x", 0.5),
                    config.getDouble(path + "spawn1.y", 100.0),
                    config.getDouble(path + "spawn1.z", -10.5),
                    (float) config.getDouble(path + "spawn1.yaw", 0.0),
                    (float) config.getDouble(path + "spawn1.pitch", 0.0)
            );

            Location s2 = new Location(world,
                    config.getDouble(path + "spawn2.x", 0.5),
                    config.getDouble(path + "spawn2.y", 100.0),
                    config.getDouble(path + "spawn2.z", 10.5),
                    (float) config.getDouble(path + "spawn2.yaw", 180.0),
                    (float) config.getDouble(path + "spawn2.pitch", 0.0)
            );

            arenas.add(new Arena(key, s1, s2));
            plugin.getLogger().info("Успешно загружена арена: " + key + " (мир: " + worldName + ")");
        }
    }

    public Arena getRandomArena() {
        if (arenas.isEmpty()) {
            World world = Bukkit.getWorlds().get(0);
            return new Arena("default", new Location(world, 0.5, 100, -10.5, 0, 0), new Location(world, 0.5, 100, 10.5, 180, 0));
        }
        return arenas.get(random.nextInt(arenas.size()));
    }

    public List<Arena> getArenas() {
        return arenas;
    }
}
