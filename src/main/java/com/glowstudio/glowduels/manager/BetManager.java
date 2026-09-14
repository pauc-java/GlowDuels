package com.glowstudio.glowduels.manager;

import com.glowstudio.glowduels.GlowDuelsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BetManager {

    private final GlowDuelsPlugin plugin;
    private final Map<UUID, Map<UUID, Double>> bets = new HashMap<>();

    public BetManager(GlowDuelsPlugin plugin) {
        this.plugin = plugin;
    }

    public void placeBet(Player better, Player target, double amount) {
        if (amount <= 0) {
            better.sendMessage(ChatColor.RED + "Сумма ставки должна быть больше нуля!");
            return;
        }

        if (!plugin.getDuelManager().isInDuel(target)) {
            better.sendMessage(ChatColor.RED + "Этот игрок сейчас не находится в дуэли!");
            return;
        }

        if (better.equals(target) || better.getUniqueId().equals(plugin.getDuelManager().getOpponent(target.getUniqueId()))) {
            better.sendMessage(ChatColor.RED + "Вы не можете ставить на самого себя или своего противника!");
            return;
        }

        bets.computeIfAbsent(target.getUniqueId(), k -> new HashMap()).put(better.getUniqueId(), amount);
        better.sendMessage(ChatColor.GREEN + "Вы успешно поставили " + amount + " монет на победу игрока " + target.getName() + "!");
    }

    public void distributeBets(Player winner) {
        Map<UUID, Double> winnerBets = bets.remove(winner.getUniqueId());
        if (winnerBets != null) {
            for (Map.Entry<UUID, Double> entry : winnerBets.entrySet()) {
                Player better = Bukkit.getPlayer(entry.getKey());
                double reward = entry.getValue() * 2;
                if (better != null && better.isOnline()) {
                    better.sendMessage(ChatColor.GOLD + "Ваша ставка на " + winner.getName() + " сыграла! Вы выиграли " + reward + " монет.");
                }
            }
        }

        UUID opponentUUID = plugin.getDuelManager().getOpponent(winner.getUniqueId());
        if (opponentUUID != null) {
            bets.remove(opponentUUID);
        }
    }
}
