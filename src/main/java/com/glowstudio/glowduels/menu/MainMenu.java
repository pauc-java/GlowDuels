package com.glowstudio.glowduels.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainMenu {

    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', "&8Дуэли • Выбор режима"));

        inv.setItem(11, createItem(Material.DIAMOND_SWORD, "&a&lДуэль без ставки", "&7Классический бой 1х1 без потерь.", Collections.singletonList("&eНажми, чтобы встать в очередь")));
        inv.setItem(15, createItem(Material.GOLD_INGOT, "&6&lДуэль со ставкой", "&7Бой на игровую валюту.", Arrays.asList("&7Ставка: &6500 монет", "&eНажми, чтобы встать в очередь")));

        player.openInventory(inv);
    }

    private static ItemStack createItem(Material material, String name, String lore1, List<String> extraLore) {
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
}
