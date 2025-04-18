package com.vertmix.balloons.api.abilities;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;


public class InfernoWrathActivation implements ActivationAbility {
    @Override
    public void execute(Player player) {

        for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
            if (entity instanceof Player && !entity.equals(player)) {
                entity.setFireTicks(100);
                entity.sendMessage(ChatColor.RED + "You have been ignited by " + player.getName() + "'s Inferno Balloon!");
            }
        }
        player.sendMessage(ChatColor.RED + "Inferno Wrath activated!");
    }
}
