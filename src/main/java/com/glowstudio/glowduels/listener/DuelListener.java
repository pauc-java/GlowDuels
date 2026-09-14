package com.glowstudio.glowduels.listener;

import com.glowstudio.glowduels.GlowDuelsPlugin;
import com.glowstudio.glowduels.security.PermissionManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class DuelListener implements Listener {

    private final GlowDuelsPlugin plugin;

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
            if (PermissionManager.hasPermission(player, "glowduels.bypass.commands")) {
                return;
            }
            String message = event.getMessage().toLowerCase();
            List<String> blocked = plugin.getCommandsConfig().getBlockedCommands();
            for (String cmd : blocked) {
                if (message.startsWith(cmd.toLowerCase())) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Нельзя использовать эту команду во время дуэли!");
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (plugin.getDuelManager().isInDuel(player)) {
            event.setCancelled(true);
        }
    }
}
