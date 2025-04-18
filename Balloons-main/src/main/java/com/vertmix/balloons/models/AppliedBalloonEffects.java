package com.vertmix.balloons.models;

import com.vertmix.balloons.api.abilities.PassiveAbility;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Stores applied passive abilities and potion effects for a player.

 */
public class AppliedBalloonEffects {
    private final Balloon balloon;
    private final List<PassiveAbility> abilities;
    private final List<PotionEffectType> potionEffects;

    public AppliedBalloonEffects(Balloon balloon, List<PassiveAbility> abilities, List<PotionEffectType> potionEffects) {
        this.balloon = balloon;
        this.abilities = abilities;
        this.potionEffects = potionEffects;
    }

    public Balloon getBalloon() {
        return balloon;
    }

    public List<PassiveAbility> getAbilities() {
        return abilities;
    }

    public List<PotionEffectType> getPotionEffects() {
        return potionEffects;
    }
}
