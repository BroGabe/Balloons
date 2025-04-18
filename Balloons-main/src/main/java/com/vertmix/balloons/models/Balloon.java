package com.vertmix.balloons.models;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Balloon with its properties and abilities.

 */
public class Balloon {

    private final String key;
    private final String name;
    private final String texture;
    private final List<String> lore;
    private final List<PotionEffect> passivePotions;
    private final List<String> passiveAbilities;
    private final List<PotionEffect> activationPotions;
    private final List<String> activationAbilities;
    private final int activationCooldown;

    public Balloon(ConfigurationSection config) {
        this.key = config.getString("key", "UNNAMED_BALLOON").toUpperCase();
        this.name = ChatColor.translateAlternateColorCodes('&', config.getString("name", "Unnamed Balloon"));
        this.texture = config.getString("texture", "");
        this.lore = config.getStringList("lore");
        this.passivePotions = parsePotionEffects(config.getStringList("passive-potions"));
        this.passiveAbilities = config.getStringList("passive-abilities");
        this.activationPotions = parsePotionEffects(config.getStringList("activation-potions"));
        this.activationAbilities = config.getStringList("activation-abilities");
        this.activationCooldown = config.getInt("activation-cooldown", 30);
    }

    private List<PotionEffect> parsePotionEffects(List<String> potionStrings) {
        List<PotionEffect> potions = new ArrayList<>();
        for (String potionString : potionStrings) {
            String[] parts = potionString.split(":");
            if (parts.length < 2) continue;
            PotionEffectType type = PotionEffectType.getByName(parts[0].toUpperCase());
            if (type == null) continue;
            try {
                int duration;
                if (parts[1].equalsIgnoreCase("INFINITE")) {
                    duration = Integer.MAX_VALUE;
                } else {
                    duration = Integer.parseInt(parts[1]) * 20; // Convert seconds to ticks
                }
                int amplifier = parts.length >= 3 ? Integer.parseInt(parts[2]) - 1 : 0;
                potions.add(new PotionEffect(type, duration, amplifier, false, false));
            } catch (NumberFormatException e) {
                // Invalid number format; skip this potion effect
                continue;
            }
        }
        return potions;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getTexture() {
        return texture;
    }

    public List<String> getLore() {
        return lore;
    }

    public List<PotionEffect> getPassivePotions() {
        return passivePotions;
    }

    public List<String> getPassiveAbilities() {
        return passiveAbilities;
    }

    public List<PotionEffect> getActivationPotions() {
        return activationPotions;
    }

    public List<String> getActivationAbilities() {
        return activationAbilities;
    }

    public int getActivationCooldown() {
        return activationCooldown;
    }
}
