package com.vertmix.balloons.api;

import com.vertmix.balloons.models.Balloon;
import com.vertmix.balloons.api.abilities.ActivationAbility;
import com.vertmix.balloons.api.abilities.PassiveAbility;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Primary API interface for the Balloons plugin.
 * Allows external plugins to interact with balloon functionalities.

 */
public interface BalloonAPI {

    /**
     * Registers a new passive ability.
     *
     * @param name    Unique identifier for the passive ability.
     * @param ability Implementation of the PassiveAbility interface.
     */
    void registerPassiveAbility(String name, PassiveAbility ability);

    /**
     * Registers a new activation ability.
     *
     * @param name    Unique identifier for the activation ability.
     * @param ability Implementation of the ActivationAbility interface.
     */
    void registerActivationAbility(String name, ActivationAbility ability);

    /**
     * Retrieves the list of passive abilities associated with a player's attached balloon.
     *
     * @param player The player to retrieve abilities for.
     * @return List of PassiveAbility instances.
     */
    List<PassiveAbility> getPassiveAbilities(Player player);

    /**
     * Retrieves the list of activation abilities associated with a player's attached balloon.
     *
     * @param player The player to retrieve abilities for.
     * @return List of ActivationAbility instances.
     */
    List<ActivationAbility> getActivationAbilities(Player player);

    /**
     * Checks if a player has an active balloon attached to their weapon.
     *
     * @param player The player to check.
     * @return True if a balloon is attached, false otherwise.
     */
    boolean hasActiveBalloon(Player player);

    /**
     * Removes the cooldown for a player's balloon activation.
     *
     * @param player The player whose cooldown should be removed.
     */
    void removeCooldown(Player player);

    /**
     * Retrieves the balloon attached to a specific item (weapon).
     *
     * @param item The weapon item stack.
     * @return The attached Balloon, or null if none.
     */
    Balloon getBalloonAttached(ItemStack item);

    /**
     * Retrieves the balloon represented by a specific item.
     *
     * @param item The item representing the balloon.
     * @return The corresponding Balloon, or null if none.
     */
    Balloon getBalloonFromItem(ItemStack item);

    /**
     * Creates an ItemStack representing the given balloon as a player head with custom texture.
     *
     * @param balloon The balloon to create an item for.
     * @return The ItemStack representing the balloon.
     */
    ItemStack getBalloonItem(Balloon balloon);

    /**
     * Opens the balloons selection menu for the player.
     *
     * @param player The player to open the menu for.
     */
    void openBalloonsMenu(Player player);

    /**
     * Attaches a balloon to a player, applying visual effects and passive abilities.
     *
     * @param player  The player to attach the balloon to.
     * @param balloon The balloon to attach.
     * @param item    The weapon item to attach the balloon to.
     */
    void attachBalloon(Player player, Balloon balloon, ItemStack item);

    /**
     * Detaches the currently attached balloon from a player, removing visual effects and passive abilities.
     *
     * @param player The player to detach the balloon from.
     */
    void detachBalloon(Player player);

    /**
     * Applies all passive abilities and passive potion effects of the given balloon to the player.
     *
     * @param player  The player to apply abilities to.
     * @param balloon The balloon whose passive abilities to apply.
     */
    void applyPassiveAbilities(Player player, Balloon balloon);

    /**
     * Removes all passive abilities and passive potion effects from the player.
     *
     * @param player The player to remove abilities from.
     */
    void removePassiveAbilities(Player player);

    /**
     * Executes all activation abilities of the given balloon for the player.
     *
     * @param player  The player activating the balloon.
     * @param balloon The balloon being activated.
     */
    void executeActivationAbilities(Player player, Balloon balloon);

    /**
     * Applies all activation potion effects of the given balloon to the player.
     *
     * @param player  The player to apply potion effects to.
     * @param balloon The balloon whose activation potions to apply.
     */
    void applyActivationPotions(Player player, Balloon balloon);

    /**
     * Retrieves the active balloon for a player, if any.
     *
     * @param player The player.
     * @return The active Balloon, or null if none.
     */
    Balloon getActiveBalloon(Player player);

    /**
     * Attaches a balloon visually to the player using an ArmorStand and creates a leash between them.
     *
     * @param player  The player to attach the balloon to.
     * @param balloon The balloon to attach.
     */
    void attachBalloonVisual(Player player, Balloon balloon);
}
