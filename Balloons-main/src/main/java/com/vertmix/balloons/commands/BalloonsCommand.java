package com.vertmix.balloons.commands;

import com.vertmix.balloons.models.Balloon;
import com.vertmix.balloons.BalloonsPlugin;
import com.vertmix.balloons.menus.BalloonMenu;
import com.vertmix.balloons.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class BalloonsCommand implements CommandExecutor, TabCompleter {

    private final BalloonsPlugin plugin;

    public BalloonsCommand(BalloonsPlugin plugin) {
        this.plugin = plugin;

        plugin.getCommand("balloons").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(MessageUtils.getMessage("only-players-command"));
                return true;
            }

            Player player = (Player) sender;
            BalloonMenu menu = new BalloonMenu(plugin);
            menu.openMenu(player, 1);
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (!sender.hasPermission("balloons.give")) {
                sender.sendMessage(MessageUtils.getMessage("no-permission"));
                return true;
            }

            if (args.length < 3) {
                sender.sendMessage(MessageUtils.getMessage("usage"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(MessageUtils.getMessage("player-not-found").replace("{player}", args[1]));
                return true;
            }

            String balloonKey = args[2].toUpperCase();
            Balloon balloon = plugin.getBalloonManager().getBalloon(balloonKey);
            if (balloon == null) {
                sender.sendMessage(MessageUtils.getMessage("balloon-not-found").replace("{balloon}", balloonKey));
                return true;
            }

            ItemStack balloonItem = plugin.getBalloonManager().getBalloonItem(balloon);
            target.getInventory().addItem(balloonItem);
            target.sendMessage(MessageUtils.getMessage("received-balloon").replace("{balloon}", balloon.getName()));
            sender.sendMessage(MessageUtils.getMessage("gave-balloon")
                    .replace("{balloon}", balloon.getName())
                    .replace("{player}", target.getName()));
            return true;
        }

        sender.sendMessage(MessageUtils.getMessage("invalid-subcommand"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Collections.singletonList("give");
        }

        if(args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName)
                    .filter(name -> name.startsWith(args[1])).collect(Collectors.toList());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            List<String> balloonKeys = new ArrayList<>(plugin.getBalloonManager().getAllBalloonKeys());
            String currentArg = args[2].toUpperCase();
            List<String> suggestions = new ArrayList<>();
            for (String key : balloonKeys) {
                if (key.startsWith(currentArg)) {
                    suggestions.add(key);
                }
            }
            return suggestions;
        }

        return Collections.emptyList();
    }
}
