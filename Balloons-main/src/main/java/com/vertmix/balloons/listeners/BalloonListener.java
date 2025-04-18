package com.vertmix.balloons.listeners;

import com.vertmix.balloons.api.events.BalloonActivationEvent;
import com.vertmix.balloons.models.Balloon;
import com.vertmix.balloons.BalloonsPlugin;
import com.vertmix.balloons.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.block.Action;

public class BalloonListener implements Listener {

    private final BalloonsPlugin plugin;

    public BalloonListener(BalloonsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ItemStack weapon = player.getItemInHand();
        if (weapon == null || weapon.getType() == Material.AIR) return;

        if (!plugin.getBalloonManager().isWeapon(weapon)) return;

        Balloon attachedBalloon = plugin.getBalloonManager().getBalloonAttached(weapon);
        if (attachedBalloon == null) return;

        Action action = event.getAction();

        if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && player.isSneaking()) {
            // Activate the balloon
            activateBalloon(player, attachedBalloon);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlaceBalloon(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ItemStack item = player.getItemInHand();
        if (item == null || item.getType() == Material.AIR) return;

        Balloon balloon = plugin.getBalloonManager().getBalloonFromItem(item);
        if (balloon == null) return;

        event.setCancelled(true);
    }

    private void activateBalloon(Player player, Balloon attachedBalloon) {
        if (plugin.getCooldownManager().isOnCooldown(player)) {
            player.sendMessage(MessageUtils.getMessage("balloon-on-cooldown")
                    .replace("{time_left}", plugin.getCooldownManager().getCooldown(player)));
            return;
        }

        BalloonActivationEvent activationEvent = new BalloonActivationEvent(player, attachedBalloon);
        plugin.getServer().getPluginManager().callEvent(activationEvent);

        if (activationEvent.isCancelled()) return;

        // Execute activation abilities and apply potions
        plugin.getBalloonManager().executeActivationAbilities(player, attachedBalloon);
        plugin.getBalloonManager().applyActivationPotions(player, attachedBalloon);

        plugin.getCooldownManager().setCooldown(player, attachedBalloon.getActivationCooldown());
        player.sendMessage(MessageUtils.getMessage("balloon-activated").replace("{balloon}", attachedBalloon.getName()));
    }
}
