package com.vertmix.balloons.api.abilities;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class InfernoWrathPassive implements PassiveAbility {
    private final PotionEffect fireResistance = new PotionEffect(
            PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false);

    @Override
    public void apply(Player player) {
        player.addPotionEffect(fireResistance, false);

    }

    @Override
    public void remove(Player player) {
        player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
    }
}
