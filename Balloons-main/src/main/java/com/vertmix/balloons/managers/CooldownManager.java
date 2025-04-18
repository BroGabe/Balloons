package com.vertmix.balloons.managers;

import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Manages cooldown periods for balloon activations to prevent ability spamming.

 */
public class CooldownManager {

    private final Map<UUID, Long> cooldowns;

    /**
     * Constructs a CooldownManager.
     */
    public CooldownManager() {
        this.cooldowns = new HashMap<>();
    }

    /**
     * Checks if a player is currently on cooldown.
     *
     * @param player The player to check.
     * @return True if on cooldown, false otherwise.
     */
    public boolean isOnCooldown(Player player) {
        if (!cooldowns.containsKey(player.getUniqueId())) {
            return false;
        }

        long cooldownEnd = cooldowns.get(player.getUniqueId());
        if (System.currentTimeMillis() > cooldownEnd) {
            cooldowns.remove(player.getUniqueId());
            return false;
        }

        return true;
    }

    public String getCooldown(Player player) {
        String cooldown = "";

        if (!cooldowns.containsKey(player.getUniqueId())) {
            return cooldown;
        }

        long cooldownEnd = cooldowns.get(player.getUniqueId());
        long left = cooldownEnd - System.currentTimeMillis();
        if (left <= 0) {
            return cooldown;
        }

        cooldown = String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(left),
                TimeUnit.MILLISECONDS.toSeconds(left) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(left))
        );
        return cooldown;
    }

    /**
     * Sets a cooldown for a player.
     *
     * @param player  The player to set the cooldown for.
     * @param seconds The duration of the cooldown in seconds.
     */
    public void setCooldown(Player player, int seconds) {
        long cooldownEnd = System.currentTimeMillis() + (seconds * 1000L);
        cooldowns.put(player.getUniqueId(), cooldownEnd);
    }

    /**
     * Removes any existing cooldown for a player.
     *
     * @param player The player whose cooldown should be removed.
     */
    public void removeCooldown(Player player) {
        cooldowns.remove(player.getUniqueId());
    }
}
