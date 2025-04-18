package com.vertmix.balloons;

import com.vertmix.balloons.api.BalloonAPI;
import com.vertmix.balloons.api.abilities.InfernoWrathActivation;
import com.vertmix.balloons.api.abilities.InfernoWrathPassive;
import com.vertmix.balloons.commands.BalloonsCommand;
import com.vertmix.balloons.listeners.*;
import com.vertmix.balloons.managers.BalloonManager;
import com.vertmix.balloons.managers.CooldownManager;
import com.vertmix.balloons.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class for the Balloons plugin.

 */
public class BalloonsPlugin extends JavaPlugin {

    private static BalloonsPlugin instance;
    private BalloonManager balloonManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        MessageUtils.loadMessages(this);

        balloonManager = new BalloonManager(this);
        cooldownManager = new CooldownManager();

        registerAbilities();

        balloonManager.loadBalloons();

        getCommand("balloons").setExecutor(new BalloonsCommand(this));

        getServer().getPluginManager().registerEvents(new BalloonListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        getServer().getPluginManager().registerEvents(new BalloonMenuListener(this), this);
        getServer().getPluginManager().registerEvents(new LeashListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinQuitListener(this), this);

        getServer().getServicesManager().register(BalloonAPI.class, balloonManager, this, ServicePriority.Normal);

        balloonManager.startBalloonUpdater();



        getLogger().info("Balloons Plugin Enabled!");
    }

    @Override
    public void onDisable() {
        balloonManager.cleanup();
        getLogger().info("Balloons Plugin Disabled.");
    }

    /**
     * Retrieves the instance of the plugin.
     *
     * @return The BalloonsPlugin instance.
     */
    public static BalloonsPlugin getInstance() {
        return instance;
    }

    /**
     * Retrieves the BalloonManager.
     *
     * @return The BalloonManager instance.
     */
    public BalloonManager getBalloonManager() {
        return balloonManager;
    }

    /**
     * Retrieves the CooldownManager.
     *
     * @return The CooldownManager instance.
     */
    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    /**
     * Registers all passive and activation abilities.
     */
    private void registerAbilities() {
        balloonManager.registerPassiveAbility("INFERNOWRATH", new InfernoWrathPassive());
        balloonManager.registerActivationAbility("INFERNOWRATH_ACTIVATION", new InfernoWrathActivation());
    }
}
