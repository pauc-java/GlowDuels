package com.glowstudio.glowduels.command;

import com.glowstudio.glowduels.GlowDuelsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DuelCommand implements CommandExecutor {

    private final GlowDuelsPlugin plugin;

    public DuelCommand(GlowDuelsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("stavka") || (args.length > 0 && args[0].equalsIgnoreCase("stavka"))) {
            if (args.length < 3) {
                player.sendMessage(ChatColor.RED + "Использование: /stavka <ник_игрока> <сумма>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null || !target.isOnline()) {
                player.sendMessage(ChatColor.RED + "Игрок не найден или оффлайн.");
                return true;
            }

            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Неверный формат суммы!");
                return true;
            }

            plugin.getBetManager().placeBet(player, target, amount);
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("spectate") && args.length > 1) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null && target.isOnline()) {
                plugin.getSpectateManager().spectate(player, target);
            } else {
                player.sendMessage("§cИгрок не найден.");
            }
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("stats")) {
            int wins = plugin.getProfileManager().getWins(player.getUniqueId());
            int losses = plugin.getProfileManager().getLosses(player.getUniqueId());
            player.sendMessage("§8§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            player.sendMessage("§6§l Ваша статистика дуэлей:");
            player.sendMessage("§7 Победы: §a" + wins);
            player.sendMessage("§7 Поражения: §c" + losses);
            player.sendMessage("§8§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            return true;
        }

        plugin.getDuelManager().openMainMenu(player);
        return true;
    }
}
