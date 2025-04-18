package com.vertmix.balloons.api.abilities;

import org.bukkit.entity.Player;

/**
 * Interface for defining passive abilities that are continuously applied while a balloon is attached.

 */
public interface PassiveAbility {

    /**
     * Applies the passive ability effect to the player.
     *
     * @param player The player to apply the ability to.
     */
    void apply(Player player);

    /**
     * Removes the passive ability effect from the player.
     *
     * @param player The player to remove the ability from.
     */
    void remove(Player player);
}
