package com.glowstudio.glowduels.listener;

import com.glowstudio.glowduels.GlowDuelsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.List;

public class DuelListener implements Listener {

    private final GlowDuelsPlugin plugin;
    private final List<String> forbiddenCommands = Arrays.asList("/spawn", "/home", "/tp", "/fly", "/warp", "/rtp", "/hub", "/lobby");

    public DuelListener(GlowDuelsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("Дуэли")) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player && event.getCurrentItem() != null) {
                Player player = (Player) event.getWhoClicked();
                plugin.getDuelManager().handleMenuClick(player, event.getSlot());
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (plugin.getDuelManager().isInDuel(player)) {
            event.getDrops().clear();
            event.setDroppedExp(0);
            plugin.getDuelManager().handleDeath(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (plugin.getDuelManager().isInDuel(player)) {
            plugin.getDuelManager().handleDeath(player);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (plugin.getDuelManager().isInDuel(player)) {
            String msg = event.getMessage().toLowerCase();
            for (String cmd : forbiddenCommands) {
                if (msg.startsWith(cmd)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Нельзя использовать эту команду во время дуэли!");
                    return;
                }
            }
        }
    }
}
