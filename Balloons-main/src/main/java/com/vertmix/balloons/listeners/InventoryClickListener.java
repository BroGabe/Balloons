package com.vertmix.balloons.listeners;

import com.vertmix.balloons.managers.BalloonManager;
import com.vertmix.balloons.models.Balloon;
import com.vertmix.balloons.BalloonsPlugin;
import com.vertmix.balloons.utils.MessageUtils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;


public class InventoryClickListener implements Listener {

    private final BalloonsPlugin plugin;

    public InventoryClickListener(BalloonsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();


        switch (event.getClick()) {
            case LEFT: {
                ItemStack cursorItem = event.getCursor();
                ItemStack clickedItem = event.getCurrentItem();

                if (cursorItem == null || cursorItem.getType() == Material.AIR || cursorItem.getAmount() == 0) return;
                if (clickedItem == null || clickedItem.getType() == Material.AIR || clickedItem.getAmount() == 0) return;

                NBTItem nbtCursor = new NBTItem(cursorItem);
                if (!nbtCursor.hasKey(BalloonManager.BALLOON_NBT_KEY)) return;

                Balloon balloon = plugin.getBalloonManager().getBalloonFromItem(cursorItem);
                if (balloon == null) return;

                if (!plugin.getBalloonManager().isWeapon(clickedItem)) return;

                if (plugin.getBalloonManager().getBalloonAttached(clickedItem) != null) {
                    player.sendMessage(MessageUtils.getMessage("weapon-already-has-balloon"));
                    event.setCancelled(true);
                    return;
                }

                plugin.getBalloonManager().attachBalloon(player, balloon, clickedItem);
                event.setCurrentItem(clickedItem);

                if (cursorItem.getAmount() > 1) {
                    cursorItem.setAmount(cursorItem.getAmount() - 1);
                    event.setCursor(cursorItem);
                } else {
                    event.setCursor(null);
                }

                player.sendMessage(MessageUtils.getMessage("balloon-attached").replace("{balloon}", balloon.getName()));

                event.setCancelled(true);

                break;
            }
            case RIGHT: {
                ItemStack weapon = event.getCurrentItem();
                if (weapon == null || weapon.getType() == Material.AIR) return;

                if (!plugin.getBalloonManager().isWeapon(weapon)) return;

                Balloon attachedBalloon = plugin.getBalloonManager().getBalloonAttached(weapon);
                if (attachedBalloon == null) return;

                plugin.getBalloonManager().detachBalloon(player, weapon);

                ItemStack balloonItem = plugin.getBalloonManager().getBalloonItem(attachedBalloon);

                if (!player.getInventory().addItem(balloonItem).isEmpty()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), balloonItem);
                }

                player.sendMessage(MessageUtils.getMessage("balloon-detached").replace("{balloon}", attachedBalloon.getName()));

                event.setCancelled(true);

                break;
            }
        }

    }
}
