# Balloons Plugin Documentation

## Table of Contents

- [Introduction](#introduction)
- [Installation](#installation)
- [Player Guide](#player-guide)
    - [Commands](#commands)
    - [Attaching Balloons](#attaching-balloons)
    - [Using Abilities](#using-abilities)
- [Configuration Guide](#configuration-guide)
    - [Balloon Configuration](#balloon-configuration)
    - [Messages Configuration](#messages-configuration)
    - [Menu Configuration](#menu-configuration)
- [Developer Guide](#developer-guide)
    - [Using the API](#using-the-api)
    - [Creating Abilities](#creating-abilities)
        - [Passive Abilities](#passive-abilities)
        - [Activation Abilities](#activation-abilities)
    - [Registering Abilities](#registering-abilities)
    - [Handling Events](#handling-events)

---

## Introduction

The **Balloons** plugin allows players to attach customizable balloons to their weapons, granting them unique passive and activation abilities. These abilities can include potion effects, special attacks, and more. The plugin is designed to be easily extendable, allowing developers to create new abilities and integrate them into the existing system.

---

## Installation
- Make sure you have **ProtocolLib** installed, as it's a dependency.
---

## Player Guide

### Commands

- **`/balloons`**
    - Opens the balloons selection menu.
    - **Usage:** `/balloons`
    - **Permission:** `balloons.use`

- **`/balloons give <player> <balloon>`**
    - Gives a balloon item to a player.
    - **Usage:** `/balloons give <player> <balloon>`
    - **Permission:** `balloons.give`

### Attaching Balloons

1. **Obtain a Balloon Item:**
    - Use the `/balloons give` command or acquire it through in-game events.

2. **Attach to a Weapon:**
    - Hold the weapon you wish to attach the balloon to.
    - Open your inventory and place the balloon item over the weapon.
    - The balloon will attach to the weapon, and the balloon item will be consumed.

3. **Detaching Balloons:**
    - Right-click without sneaking to detach the balloon.
    - The balloon item will return to your inventory.

4. **Visual Effects:**
    - A balloon will appear above your character, tethered by a leash.
    - The balloon's appearance is customizable via configuration.

### Using Abilities

- **Passive Abilities:**
    - Automatically applied when the balloon is attached.
    - Examples include potion effects like Fire Resistance or Speed Boost.

- **Activation Abilities:**
    - Activated by sneaking and right-clicking while holding the weapon with the attached balloon.
    - May have cooldowns to prevent spamming.
    - Examples include area-of-effect attacks or temporary buffs.

---

## Configuration Guide

### Balloon Configuration

- Balloon configurations are stored in the `plugins/Balloons/balloons` directory. Each balloon has its own `.yml` file.

- Customize plugin messages and the menu in `plugins/Balloons/config.yml`.

---

## Developer Guide
### Using the API
The Balloons plugin provides an API that allows developers to create custom abilities and interact with the plugin's functionalities.

### Accessing the API:

```java
BalloonAPI balloonAPI = (BalloonAPI) Bukkit.getServer().getServicesManager().getRegistration(BalloonAPI.class).getProvider();
```

### Creating Abilities
#### Passive Abilities
```java
package me.example.balloons.abilities;

import abilities.api.balloons.com.vertmix.PassiveAbility;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedBoostPassive implements PassiveAbility {
    private final PotionEffect speedEffect = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false);

    @Override
    public void apply(Player player) {
        player.addPotionEffect(speedEffect, true);
    }

    @Override
    public void remove(Player player) {
        player.removePotionEffect(PotionEffectType.SPEED);
    }
}
```

#### Activation Abilities
```java
package me.example.balloons.abilities;

import abilities.api.balloons.com.vertmix.ActivationAbility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class FireBlastActivation implements ActivationAbility {
    @Override
    public void execute(Player player) {
        // Ignite nearby entities
        for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
            if (entity instanceof Player && !entity.equals(player)) {
                entity.setFireTicks(100);
                entity.sendMessage(ChatColor.RED + "You have been ignited by " + player.getName() + "'s Fire Blast!");
            }
        }
        player.sendMessage(ChatColor.RED + "Fire Blast activated!");
    }
}
```

#### Registering Abilities
Register your custom abilities during your plugin's `onEnable` method.
```java
private BalloonAPI balloonAPI;

    @Override
    public void onEnable() {
        // Access the BalloonAPI
        balloonAPI = (BalloonAPI) Bukkit.getServer().getServicesManager().getRegistration(BalloonAPI.class).getProvider();

        // Register abilities
        balloonAPI.registerPassiveAbility("SPEED_BOOST", new SpeedBoostPassive());
        balloonAPI.registerActivationAbility("FIRE_BLAST", new FireBlastActivation());

        getLogger().info("Custom balloon abilities registered!");
    }
```

### Handling Events
#### BalloonActivationEvent
Called when a player activates a balloon.
```java
package me.example.balloons.listeners;

import events.api.balloons.com.vertmix.BalloonActivationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BalloonEventListener implements Listener {
    @EventHandler
    public void onBalloonActivate(BalloonActivationEvent event) {
        // Handle the event
        event.getPlayer().sendMessage("You activated a balloon!");
    }
}
```


### `BalloonAPI` class
Provides a lot of helpful methods.

