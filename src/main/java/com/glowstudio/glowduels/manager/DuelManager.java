package com.glowstudio.glowduels.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
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

    public DuelManager(GlowDuelsPlugin plugin) {
        this.plugin = plugin;
    }

    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', "&8Дуэли • Выбор режима"));
        inv.setItem(11, createGuiItem(Material.DIAMOND_SWORD, "&a&lДуэль без ставки", "&7Классический бой 1х1 без потерь.", Collections.singletonList("&eНажми, чтобы встать в очередь")));
        inv.setItem(15, createGuiItem(Material.GOLD_INGOT, "&6&lДуэль со ставкой", "&7Бой на игровую валюту.", Arrays.asList("&7Ставка: &6500 монет", "&eНажми, чтобы встать в очередь")));
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
        if (slot == 11) {
            player.closeInventory();
            if (queueWithoutBet.contains(player.getUniqueId())) {
                queueWithoutBet.remove(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "Вы вышли из очереди дуэлей без ставки.");
            } else {
                queueWithoutBet.add(player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "Вы встали в очередь дуэлей без ставки.");
                checkQueue(false);
            }
        } else if (slot == 15) {
            player.closeInventory();
            if (queueWithBet.contains(player.getUniqueId())) {
                queueWithBet.remove(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "Вы вышли из очереди дуэлей со ставкой.");
            } else {
                queueWithBet.add(player.getUniqueId());
                player.sendMessage(ChatColor.GOLD + "Вы встали в очередь дуэлей со ставкой (500 монет).");
                checkQueue(true);
            }
        }
    }

    private void checkQueue(boolean bet) {
        Set<UUID> queue = bet ? queueWithBet : queueWithoutBet;
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
        activeDuels.put(p1.getUniqueId(), p2.getUniqueId());
        activeDuels.put(p2.getUniqueId(), p1.getUniqueId());

        savePlayerState(p1);
        savePlayerState(p2);

        setupDuelGear(p1);
        setupDuelGear(p2);

        Location spawn1 = new Location(p1.getWorld(), 0.5, 100, -10.5, 0, 0);
        Location spawn2 = new Location(p2.getWorld(), 0.5, 100, 10.5, 180, 0);

        p1.teleport(spawn1);
        p2.teleport(spawn2);

        p1.sendTitle(ChatColor.RED + "БОЙ!", ChatColor.YELLOW + "Противник: " + p2.getName(), 10, 40, 10);
        p2.sendTitle(ChatColor.RED + "БОЙ!", ChatColor.YELLOW + "Противник: " + p1.getName(), 10, 40, 10);

        p1.playSound(p1.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1f);
        p2.playSound(p2.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1f);
    }

    private void savePlayerState(Player player) {
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
        restorePlayerState(loser);
        loser.teleport(loser.getWorld().getSpawnLocation());
        plugin.getProfileManager().addLoss(loser.getUniqueId());

        if (winner != null && winner.isOnline()) {
            winner.sendTitle(ChatColor.GREEN + "ПОБЕДА!", ChatColor.YELLOW + "Вы выиграли дуэль", 10, 40, 10);
            restorePlayerState(winner);
            winner.teleport(winner.getWorld().getSpawnLocation());
            winner.sendMessage(ChatColor.GREEN + "Вы одержали победу!");
            plugin.getProfileManager().addWin(winner.getUniqueId());

            spawnVictoryFirework(winner.getLocation());
        }
    }

    private void spawnVictoryFirework(Location loc) {
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
    }

    public boolean isInDuel(Player player) {
        return activeDuels.containsKey(player.getUniqueId());
    }

    public void shutdown() {
        queueWithoutBet.clear();
        queueWithBet.clear();
        activeDuels.clear();
    }
}
