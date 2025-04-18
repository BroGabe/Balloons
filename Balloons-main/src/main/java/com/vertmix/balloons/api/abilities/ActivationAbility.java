package com.vertmix.balloons.api.abilities;

import org.bukkit.entity.Player;

/**
 * Represents an ability that is executed upon balloon activation.

 */
public interface ActivationAbility {
    /**
     * Executes the activation ability for the given player.
     *
     * @param player The player activating the ability.
     */
    void execute(Player player);
}
