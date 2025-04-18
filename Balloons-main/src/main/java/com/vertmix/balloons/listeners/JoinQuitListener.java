package com.vertmix.balloons.listeners;

import com.vertmix.balloons.BalloonsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    private final BalloonsPlugin plugin;

    public JoinQuitListener(BalloonsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getBalloonManager().cleanupPlayerBalloon(player);
        plugin.getBalloonManager().getOnlinePlayers().remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(plugin,
                () -> plugin.getBalloonManager().getOnlinePlayers().add(player.getUniqueId()), 20L);
    }
}