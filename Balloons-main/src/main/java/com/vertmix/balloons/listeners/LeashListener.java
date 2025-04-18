package com.vertmix.balloons.listeners;

import com.vertmix.balloons.BalloonsPlugin;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class LeashListener implements Listener {

    private final BalloonsPlugin plugin;

    public LeashListener(BalloonsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Pig)) return;
        if(plugin.getBalloonManager().getLeashHolders().containsValue((Pig) event.getEntity()))
            event.setCancelled(true);
    }
}
