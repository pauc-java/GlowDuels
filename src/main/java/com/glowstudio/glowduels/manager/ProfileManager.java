package com.glowstudio.glowduels.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileManager {

    private final Map<UUID, Integer> wins = new HashMap<>();
    private final Map<UUID, Integer> losses = new HashMap<>();

    public void addWin(UUID uuid) {
        wins.put(uuid, getWins(uuid) + 1);
    }

    public void addLoss(UUID uuid) {
        losses.put(uuid, getLosses(uuid) + 1);
    }

    public int getWins(UUID uuid) {
        return wins.getOrDefault(uuid, 0);
    }

    public int getLosses(UUID uuid) {
        return losses.getOrDefault(uuid, 0);
    }
}
