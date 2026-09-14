package com.glowstudio.glowduels.manager;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpectateManager {

    private final Map<UUID, GameMode> previousGameMode = new HashMap<>();

    public void spectate(Player spectator, Player target) {
        previousGameMode.put(spectator.getUniqueId(), spectator.getGameMode());
        spectator.setGameMode(GameMode.SPECTATOR);
        spectator.teleport(target.getLocation());
        spectator.sendMessage("§aВы вошли в режим наблюдения за игроком " + target.getName());
    }

    public void stopSpectating(Player spectator) {
        if (previousGameMode.containsKey(spectator.getUniqueId())) {
            spectator.setGameMode(previousGameMode.get(spectator.getUniqueId()));
            previousGameMode.remove(spectator.getUniqueId());
            spectator.teleport(spectator.getWorld().getSpawnLocation());
            spectator.sendMessage("§cВы вышли из режима наблюдения.");
        }
    }
}
