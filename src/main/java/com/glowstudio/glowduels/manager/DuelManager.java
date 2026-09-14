package com.glowstudio.glowduels.manager;

import com.glowstudio.glowduels.GlowDuelsPlugin;
import com.glowstudio.glowduels.arena.Arena;
import com.glowstudio.glowduels.security.PermissionManager;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class DuelManager {

    private final GlowDuelsPlugin plugin;
    private final Set<UUID> queueWithoutBet = new HashSet<>();
    private final Set<UUID> queueWithBet = new HashSet<>();
    private final Map<UUID, UUID> activeDuels = new HashMap<>();
    private final Map<UUID, ItemStack[]> savedInventories = new HashMap<>();
    private final Map<UUID, ItemStack[]> savedArmor = new HashMap<>();
    private final Map<UUID, Location> savedLocations = new HashMap<>();

    public DuelManager(GlowDuelsPlugin plugin) {
        this.plugin = plugin;
    }

    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', "&8Дуэли • Выбор режима"));

        inv.setItem(11, createGuiItem(Material.DIAMOND_SWORD, "&c&lОбычная дуэль", "&7Классический бой 1х1 без сохранения инвентаря.", Collections.singletonList("&eНажми, чтобы встать в очередь")));
        inv.setItem(15, createGuiItem(Material.GOLD_INGOT, "&6&lДуэль со ставкой", "&7Бой 1х1 на игровую валюту (500 монет).", Collections.singletonList("&eНажми, чтобы встать в очередь")));

        player.openInventory(inv);
    }

    private ItemStack createGuiItem(Material material, String name, String lore1, List<String> extraLore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.translateAlternateColorCodes('&', lore1));
            for (String s : extraLore) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void handleMenuClick(Player player, int slot) {
        if (!PermissionManager.canPlay(player)) {
            player.sendMessage(ChatColor.RED + "У вас нет прав для участия в дуэлях!");
            player.closeInventory();
            return;
        }

        if (slot == 11) {
            player.closeInventory();
            toggleQueue(player, queueWithoutBet, false);
        } else if (slot == 15) {
            player.closeInventory();
            toggleQueue(player, queueWithBet, true);
        }
    }

    private void toggleQueue(Player player, Set<UUID> queue, boolean bet) {
        UUID uuid = player.getUniqueId();
        if (queue.contains(uuid)) {
            queue.remove(uuid);
            player.sendMessage(ChatColor.RED + "Вы вышли из очереди дуэлей.");
        } else {
            queue.add(uuid);
            player.sendMessage(ChatColor.GREEN + "Вы успешно встали в очередь дуэлей!");
            checkQueue(queue, bet);
        }
    }

    private void checkQueue(Set<UUID> queue, boolean bet) {
        if (queue.size() >= 2) {
            Iterator<UUID> it = queue.iterator();
            UUID p1UUID = it.next();
            it.remove();
            UUID p2UUID = it.next();
            it.remove();

            Player p1 = Bukkit.getPlayer(p1UUID);
            Player p2 = Bukkit.getPlayer(p2UUID);

            if (p1 != null && p2 != null && p1.isOnline() && p2.isOnline()) {
                startDuel(p1, p2);
            }
        }
    }

    public void startDuel(Player p1, Player p2) {
        queueWithoutBet.remove(p1.getUniqueId());
        queueWithoutBet.remove(p2.getUniqueId());
        queueWithBet.remove(p1.getUniqueId());
        queueWithBet.remove(p2.getUniqueId());

        activeDuels.put(p1.getUniqueId(), p2.getUniqueId());
        activeDuels.put(p2.getUniqueId(), p1.getUniqueId());

        savePlayerState(p1);
        savePlayerState(p2);

        Arena arena = plugin.getArenaManager().getRandomArena();

        p1.teleport(arena.getSpawn1());
        p2.teleport(arena.getSpawn2());

        setupDuelGear(p1);
        setupDuelGear(p2);

        p1.sendTitle(ChatColor.RED + "БОЙ!", ChatColor.YELLOW + "Противник: " + p2.getName(), 10, 40, 10);
        p2.sendTitle(ChatColor.RED + "БОЙ!", ChatColor.YELLOW + "Противник: " + p1.getName(), 10, 40, 10);

        p1.playSound(p1.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1f);
        p2.playSound(p2.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1f);
    }

    private void savePlayerState(Player player) {
        savedLocations.put(player.getUniqueId(), player.getLocation());
        savedInventories.put(player.getUniqueId(), player.getInventory().getContents());
        savedArmor.put(player.getUniqueId(), player.getInventory().getArmorContents());
        player.getInventory().clear();
    }

    private void setupDuelGear(Player player) {
        player.getInventory().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.NETHERITE_BOOTS));
        player.getInventory().setItem(0, new ItemStack(Material.NETHERITE_SWORD));
        player.getInventory().setItem(1, new ItemStack(Material.GOLDEN_APPLE, 16));
    }

    public void handleDeath(Player loser) {
        UUID winnerUUID = activeDuels.remove(loser.getUniqueId());
        if (winnerUUID == null) return;

        activeDuels.remove(winnerUUID);
        Player winner = Bukkit.getPlayer(winnerUUID);

        loser.sendTitle(ChatColor.DARK_RED + "ПОРАЖЕНИЕ", ChatColor.GRAY + "Вы проиграли дуэль", 10, 40, 10);
        plugin.getProfileManager().addLoss(loser.getUniqueId());

        loser.getInventory().clear();
        teleportBack(loser);

        if (winner != null && winner.isOnline()) {
            winner.sendTitle(ChatColor.GREEN + "ПОБЕДА!", ChatColor.YELLOW + "Вы выиграли дуэль", 10, 40, 10);
            restorePlayerState(winner);
            winner.sendMessage(ChatColor.GREEN + "Вы одержали победу!");
            plugin.getProfileManager().addWin(winner.getUniqueId());
            spawnVictoryFirework(winner.getLocation());
        }
    }

    private void spawnVictoryFirework(Location loc) {
        if (loc.getWorld() == null) return;
        Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder().flicker(true).trail(true).withColor(Color.GREEN).withFade(Color.YELLOW).with(FireworkEffect.Type.BALL_LARGE).build());
        meta.setPower(1);
        fw.setFireworkMeta(meta);
    }

    private void restorePlayerState(Player player) {
        player.getInventory().clear();
        if (savedInventories.containsKey(player.getUniqueId())) {
            player.getInventory().setContents(savedInventories.get(player.getUniqueId()));
            savedInventories.remove(player.getUniqueId());
        }
        if (savedArmor.containsKey(player.getUniqueId())) {
            player.getInventory().setArmorContents(savedArmor.get(player.getUniqueId()));
            savedArmor.remove(player.getUniqueId());
        }
        teleportBack(player);
    }

    private void teleportBack(Player player) {
        if (savedLocations.containsKey(player.getUniqueId())) {
            player.teleport(savedLocations.get(player.getUniqueId()));
            savedLocations.remove(player.getUniqueId());
        } else {
            player.teleport(player.getWorld().getSpawnLocation());
        }
    }

    public boolean isInDuel(Player player) {
        return activeDuels.containsKey(player.getUniqueId());
    }

    public UUID getOpponent(UUID playerUUID) {
        return activeDuels.get(playerUUID);
    }

    public void shutdown() {
        queueWithoutBet.clear();
        queueWithBet.clear();
        for (UUID uuid : new HashSet<>(activeDuels.keySet())) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                restorePlayerState(p);
            }
        }
        activeDuels.clear();
    }
}
