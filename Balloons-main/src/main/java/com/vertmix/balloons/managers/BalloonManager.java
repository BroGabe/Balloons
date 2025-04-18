package com.vertmix.balloons.managers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.vertmix.balloons.api.BalloonAPI;
import com.vertmix.balloons.api.abilities.ActivationAbility;
import com.vertmix.balloons.api.abilities.PassiveAbility;
import com.vertmix.balloons.models.AppliedBalloonEffects;
import com.vertmix.balloons.models.Balloon;
import de.tr7zw.changeme.nbtapi.NBT;
import com.vertmix.balloons.BalloonsPlugin;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.*;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages all balloon-related functionalities, including loading configurations,
 * handling abilities, and managing visual effects.

 */
public class BalloonManager implements BalloonAPI {

    private final BalloonsPlugin plugin;
    private final Map<String, Balloon> balloons;
    private final Map<String, PassiveAbility> passiveAbilities;
    private final Map<String, ActivationAbility> activationAbilities;


    private final Set<UUID> playersWithActiveBalloons;
    private final Map<UUID, ArmorStand> balloonArmorStands;
    private final Map<UUID, AppliedBalloonEffects> appliedPassiveEffects;

    private final List<UUID> onlinePlayers;

    private final Map<UUID, Pig> leashHolders;

    public static final String BALLOON_NBT_KEY = "BalloonKey";

    public BalloonManager(BalloonsPlugin plugin) {
        this.plugin = plugin;
        this.balloons = new HashMap<>();
        this.passiveAbilities = new HashMap<>();
        this.activationAbilities = new HashMap<>();

        this.playersWithActiveBalloons = new HashSet<>();
        this.balloonArmorStands = new HashMap<>();
        this.appliedPassiveEffects = new HashMap<>();
        this.leashHolders = new HashMap<>();
        this.onlinePlayers = new ArrayList<>();
    }

    /**
     * Loads all balloon configurations from the 'balloons' directory.
     */
    public void loadBalloons() {
        File balloonsFolder = new File(plugin.getDataFolder(), "balloons");
        if (!balloonsFolder.exists()) {
            balloonsFolder.mkdirs();
            plugin.saveResource("balloons/Inferno.yml", false);
        }

        for (File file : Objects.requireNonNull(balloonsFolder.listFiles())) {
            if (file.getName().endsWith(".yml")) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                Balloon balloon = new Balloon(config);
                String balloonKey = balloon.getKey();
                if (balloons.containsKey(balloonKey)) {
                    plugin.getLogger().severe("Duplicate balloon key detected: " + balloonKey + ". Skipping this balloon.");
                    continue;
                }
                balloons.put(balloonKey, balloon);
                plugin.getLogger().info("Loaded balloon: " + balloon.getName() + " with key: " + balloonKey);
            }
        }
    }

    @Override
    public void registerPassiveAbility(String name, PassiveAbility ability) {
        passiveAbilities.put(name.toUpperCase(), ability);
    }

    @Override
    public void registerActivationAbility(String name, ActivationAbility ability) {
        activationAbilities.put(name.toUpperCase(), ability);
    }

    @Override
    public List<PassiveAbility> getPassiveAbilities(Player player) {
        Balloon balloon = getActiveBalloon(player);
        if (balloon == null) return Collections.emptyList();

        return balloon.getPassiveAbilities().stream()
                .map(name -> passiveAbilities.get(name.toUpperCase()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivationAbility> getActivationAbilities(Player player) {
        Balloon balloon = getActiveBalloon(player);
        if (balloon == null) return Collections.emptyList();

        return balloon.getActivationAbilities().stream()
                .map(name -> activationAbilities.get(name.toUpperCase()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasActiveBalloon(Player player) {
        return getActiveBalloon(player) != null;
    }

    @Override
    public void removeCooldown(Player player) {
        plugin.getCooldownManager().removeCooldown(player);
    }

    @Override
    public Balloon getActiveBalloon(Player player) {
        if (!playersWithActiveBalloons.contains(player.getUniqueId())) {
            return null;
        }
        ItemStack itemInHand = player.getItemInHand();
        return getBalloonAttached(itemInHand);
    }

    @Override
    public Balloon getBalloonAttached(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return null;
        if (!isWeapon(item)) return null;
        NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasKey(BALLOON_NBT_KEY)) return null;
        String balloonKey = nbtItem.getString(BALLOON_NBT_KEY).toUpperCase();
        return balloons.get(balloonKey);
    }

    @Override
    public Balloon getBalloonFromItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return null;
        NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasKey(BALLOON_NBT_KEY)) return null;
        String balloonKey = nbtItem.getString(BALLOON_NBT_KEY).toUpperCase();
        return balloons.get(balloonKey);
    }

    private ItemStack setSkull(String base64){
        ItemStack head = new ItemStack(Material.SKULL_ITEM,1,(short)3);

        if(base64.isEmpty())
            return head;

        NBT.modify(head, nbt -> {
            final ReadWriteNBT skullOwnerCompound = nbt.getOrCreateCompound("SkullOwner");

            skullOwnerCompound.setUUID("Id", UUID.randomUUID());
            skullOwnerCompound.getOrCreateCompound("Properties")
                    .getCompoundList("textures")
                    .addCompound()
                    .setString("Value", base64);
        });

        return head;
    }

    @Override
    public ItemStack getBalloonItem(Balloon balloon) {
        ItemStack skull = setSkull(balloon.getTexture());

        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta == null) return null;

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', balloon.getName()));
        if (balloon.getLore() != null && !balloon.getLore().isEmpty()) {
            meta.setLore(balloon.getLore().stream()
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .collect(Collectors.toList()));
        }
        skull.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(skull);
        nbtItem.setString(BALLOON_NBT_KEY, balloon.getKey());
        return nbtItem.getItem();
    }

    @Override
    public void openBalloonsMenu(Player player) {
        String inventoryName = plugin.getConfig().getString("balloons_menu.title", "Available Balloons");
        int size = plugin.getConfig().getInt("balloons_menu.size", 54);

        Inventory menu = Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&', inventoryName));

        for (Balloon balloon : balloons.values()) {
            ItemStack item = getBalloonItem(balloon);
            menu.addItem(item);
        }

        player.openInventory(menu);
    }

    @Override
    public void attachBalloon(Player player, Balloon balloon, ItemStack item) {
        // Set NBT data on the weapon to mark the attached balloon
        NBTItem nbtWeapon = new NBTItem(item);
        nbtWeapon.setString(BALLOON_NBT_KEY, balloon.getKey());

        ItemMeta changedMeta = nbtWeapon.getItem().getItemMeta();
        List<String> lore = changedMeta.getLore();
        if(lore == null) {
            lore = new ArrayList<>();
        }
        lore.addAll(plugin.getConfig().getStringList("extra_lore")
                .stream().map(str -> ChatColor.translateAlternateColorCodes('&',
                        str.replace("{balloon}", balloon.getName())))
                .collect(Collectors.toList()));
        changedMeta.setLore(lore);

        item.setItemMeta(changedMeta);
    }

    @Override
    public void detachBalloon(Player player) {
        detachBalloon(player, player.getInventory().getItemInHand());
    }

    public void detachBalloon(Player player, ItemStack weapon) {
        if (weapon == null || weapon.getType() == Material.AIR) return;

        // Remove NBT data from the weapon
        NBTItem nbtWeapon = new NBTItem(weapon);
        String balloonName = getBalloon(nbtWeapon.getString(BALLOON_NBT_KEY)).getName();
        nbtWeapon.removeKey(BALLOON_NBT_KEY);

        ItemMeta changedMeta = nbtWeapon.getItem().getItemMeta();
        List<String> lore = changedMeta.getLore();
        lore.removeAll(plugin.getConfig().getStringList("extra_lore")
                .stream().map(str -> ChatColor.translateAlternateColorCodes('&',
                        str.replace("{balloon}", balloonName)))
                .collect(Collectors.toList()));
        changedMeta.setLore(lore);

        weapon.setItemMeta(changedMeta);

        // Remove abilities and visuals
        removePassiveAbilities(player);
        detachBalloonVisual(player);
        playersWithActiveBalloons.remove(player.getUniqueId());
    }

    @Override
    public void applyPassiveAbilities(Player player, Balloon balloon) {
        List<PassiveAbility> abilities = balloon.getPassiveAbilities().stream()
                .map(name -> passiveAbilities.get(name.toUpperCase()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        for (PassiveAbility ability : abilities) {
            ability.apply(player);
        }

        // Apply passive potions and collect their types
        List<PotionEffectType> potionEffectTypes = new ArrayList<>();
        for (PotionEffect potion : balloon.getPassivePotions()) {
            PotionEffectType type = potion.getType();
            boolean hasHigherEffect = false;

            for (PotionEffect activeEffect : player.getActivePotionEffects()) {
                if (activeEffect.getType() == type) {
                    if (activeEffect.getAmplifier() >= potion.getAmplifier()) {

                        hasHigherEffect = true;
                        break;
                    }
                }
            }

            if (!hasHigherEffect) {
                player.addPotionEffect(potion, true);
                potionEffectTypes.add(potion.getType());
            }
        }

        // Track applied abilities and potion effects for later removal
        appliedPassiveEffects.put(player.getUniqueId(), new AppliedBalloonEffects(balloon, abilities, potionEffectTypes));
    }



    @Override
    public void removePassiveAbilities(Player player) {
        UUID playerId = player.getUniqueId();

        AppliedBalloonEffects appliedEffects = appliedPassiveEffects.get(playerId);

        if (appliedEffects != null) {
            for (PassiveAbility ability : appliedEffects.getAbilities()) {
                ability.remove(player);
            }

            for (PotionEffectType effectType : appliedEffects.getPotionEffects()) {
                if (effectType != null && player.hasPotionEffect(effectType)) {
                    player.removePotionEffect(effectType);
                }
            }

            appliedPassiveEffects.remove(playerId);
        }
    }

    @Override
    public void executeActivationAbilities(Player player, Balloon balloon) {
        for (ActivationAbility ability : balloon.getActivationAbilities().stream()
                .map(name -> activationAbilities.get(name.toUpperCase()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList())) {
            ability.execute(player);
        }
    }

    @Override
    public void applyActivationPotions(Player player, Balloon balloon) {
        for (PotionEffect potion : balloon.getActivationPotions()) {
            PotionEffectType type = potion.getType();
            boolean hasHigherEffect = false;

            for (PotionEffect activeEffect : player.getActivePotionEffects()) {
                if (activeEffect.getType() == type) {
                    if (activeEffect.getAmplifier() >= potion.getAmplifier()) {
                        hasHigherEffect = true;
                        break;
                    }
                }
            }

            if (!hasHigherEffect) {
                player.addPotionEffect(potion, true);
            }
        }
    }


    @Override
    public void attachBalloonVisual(Player player, Balloon balloon) {
        Location balloonLoc = player.getLocation().clone().add(0, 2.1, 0)
                .add(player.getLocation().clone().getDirection().normalize().multiply(-1.5));

        ArmorStand balloonStand = player.getWorld().spawn(balloonLoc, ArmorStand.class);
        balloonStand.setVisible(false);
        balloonStand.setGravity(false);
        balloonStand.setHelmet(getBalloonItem(balloon));
        balloonStand.setCustomName(balloon.getName());
        balloonStand.setCustomNameVisible(false);
        balloonStand.setCanPickupItems(false);
        balloonStand.setRemoveWhenFarAway(false);
        balloonStand.setMarker(true);

        //workaround to display the leash nicely
        Pig leashHolder = player.getWorld().spawn(balloonLoc, Pig.class);
        leashHolder.setNoDamageTicks(Integer.MAX_VALUE);
        leashHolder.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE,0, false, false));

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            leashHolder.setLeashHolder(player);
            NBT.modify(leashHolder, nbt -> {
                nbt.setBoolean("Silent", true);
                nbt.setShort("NoAI", (short) 1);
            });
        }, 2L);

        balloonArmorStands.put(player.getUniqueId(), balloonStand);
        leashHolders.put(player.getUniqueId(), leashHolder);
    }


    /**
     * Updates the positions of the balloon visual and leash holder to follow the player.
     *
     * @param player The player whose balloon visual should be updated.
     */
    public void updateBalloonVisual(Player player) {
        UUID playerId = player.getUniqueId();
        ArmorStand balloonStand = balloonArmorStands.get(playerId);

        if(balloonStand != null && !balloonStand.isDead()) {
            Pig leashHolder = leashHolders.get(playerId);

            NBT.modify(leashHolder, nbt -> {
                nbt.setBoolean("Silent", true);
                nbt.setShort("NoAI", (short) 1);
            });

            Location balloonLoc = player.getLocation().clone().add(0, 2.1, 0)
                    .add(player.getLocation().clone().getDirection().normalize().multiply(-1.5));
            balloonStand.teleport(balloonLoc);
            leashHolder.teleport(balloonLoc);
        }
    }


    /**
     * Detaches the balloon visual and leash holder from the player.
     *
     * @param player The player to detach the balloon from.
     */
    public void detachBalloonVisual(Player player) {
        UUID playerId = player.getUniqueId();

        ArmorStand stand = balloonArmorStands.get(playerId);
        if (stand != null && !stand.isDead()) {
            stand.remove();
        }
        balloonArmorStands.remove(playerId);

        Entity leashHolder = leashHolders.get(playerId);
        if (leashHolder != null && !leashHolder.isDead()) {
            leashHolder.remove();
        }
        leashHolders.remove(playerId);
    }

    /**
     * Starts a repeating task to update the balloon visuals and handle active balloons.
     */
    public void startBalloonUpdater() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (UUID playerId : onlinePlayers) {
                Player player = Bukkit.getPlayer(playerId);

                if(player == null) continue; // fail proof

                ItemStack itemInHand = player.getItemInHand();
                Balloon balloon = getBalloonAttached(itemInHand);

                boolean hasActiveBalloon = playersWithActiveBalloons.contains(playerId);

                if (balloon != null && !hasActiveBalloon) {
                    // Player started holding the weapon with a balloon attached
                    playersWithActiveBalloons.add(playerId);
                    applyPassiveAbilities(player, balloon);
                    attachBalloonVisual(player, balloon);
                } else if (balloon == null && hasActiveBalloon) {
                    // Player stopped holding the weapon with a balloon attached
                    playersWithActiveBalloons.remove(playerId);
                    removePassiveAbilities(player);
                    detachBalloonVisual(player);
                }

                if (balloon != null) {
                    updateBalloonVisual(player);
                }
            }
        }, 10L, 5L);
    }

    public void cleanupPlayerBalloon(Player player) {
        UUID playerId = player.getUniqueId();

        removePassiveAbilities(player);
        detachBalloonVisual(player);

        playersWithActiveBalloons.remove(playerId);
    }


    /**
     * Cleans up all active balloons and leash holders, typically called during plugin disable.
     */
    public void cleanup() {
        for (ArmorStand stand : balloonArmorStands.values()) {
            if (stand != null && !stand.isDead()) {
                stand.remove();
            }
        }
        balloonArmorStands.clear();

        for (Entity leashHolder : leashHolders.values()) {
            if (leashHolder != null && !leashHolder.isDead()) {
                leashHolder.remove();
            }
        }
        leashHolders.clear();

        appliedPassiveEffects.clear();
        playersWithActiveBalloons.clear();
    }

    /**
     * Retrieves a balloon by its key.
     *
     * @param key The unique key of the balloon.
     * @return The Balloon instance, or null if not found.
     */
    public Balloon getBalloon(String key) {
        if (key == null) return null;
        return balloons.get(key.toUpperCase());
    }

    /**
     * Checks if the given item is a weapon (sword, axe, pickaxe, shovel, or hoe).
     *
     * @param item The ItemStack to check.
     * @return True if the item is a weapon, false otherwise.
     */
    public boolean isWeapon(ItemStack item) {
        if (item == null) return false;
        Material type = item.getType();
        return type.toString().endsWith("_SWORD") ||
                type.toString().endsWith("_AXE") ||
                type.toString().endsWith("_PICKAXE") ||
                type.toString().endsWith("_SHOVEL") ||
                type.toString().endsWith("_HOE");
    }

    public Set<String> getAllBalloonKeys() {
        return balloons.keySet();
    }

    public Collection<Balloon> getAllBalloons() {
        return balloons.values();
    }

    public Map<UUID, Pig> getLeashHolders() {
        return leashHolders;
    }

    public Map<UUID, ArmorStand> getBalloonArmorStands() {
        return balloonArmorStands;
    }

    public List<UUID> getOnlinePlayers() {
        return onlinePlayers;
    }
}
