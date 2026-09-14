package com.glowstudio.glowduels.security;

import org.bukkit.entity.Player;

public final class PermissionManager {

    private PermissionManager() {}

    public static boolean hasPermission(Player player, String node) {
        if (player.hasPermission("glow.*") || player.hasPermission("glowduels.*") || player.isOp()) {
            return true;
        }
        return player.hasPermission(node);
    }

    public static boolean canUseCommand(Player player) {
        return hasPermission(player, "glowduels.admin");
    }

    public static boolean canPlay(Player player) {
        return hasPermission(player, "glowduels.player");
    }

    public static boolean canKeepInventory(Player player) {
        return hasPermission(player, "glowduels.keepinventory");
    }
}
