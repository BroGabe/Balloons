package com.vertmix.balloons.listeners;

import com.vertmix.balloons.BalloonsPlugin;
import com.vertmix.balloons.menus.BalloonMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BalloonMenuListener implements Listener {

    private final BalloonsPlugin plugin;

    public BalloonMenuListener(BalloonsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        String menuTitle = ChatColor.stripColor(inventory.getTitle());
        String configuredTitle = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("balloons_menu.title", "Available Balloons")));

        if (!menuTitle.equals(configuredTitle)) return;

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        ItemMeta itemMeta = clickedItem.getItemMeta();
        if (itemMeta == null || !itemMeta.hasDisplayName()) return;
        String displayName = ChatColor.stripColor(itemMeta.getDisplayName());

        ConfigurationSection navButtons = plugin.getConfig().getConfigurationSection("balloons_menu.navigation_buttons");
        String prevPageName = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', navButtons.getString("previous_page", "Previous Page")));
        String nextPageName = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', navButtons.getString("next_page", "Next Page")));

        if (clickedItem.getType() == Material.ARROW) {
            int currentPage = getCurrentPage(inventory, navButtons);

            if (displayName.equalsIgnoreCase(nextPageName)) {
                BalloonMenu menu = new BalloonMenu(plugin);
                menu.openMenu(player, currentPage + 1);
            } else if (displayName.equalsIgnoreCase(prevPageName)) {
                BalloonMenu menu = new BalloonMenu(plugin);
                menu.openMenu(player, currentPage - 1);
            }
        }
    }

    private int getCurrentPage(Inventory inventory, ConfigurationSection navButtons) {
        ItemStack currentPageItem = inventory.getItem(49);
        if (currentPageItem == null || !currentPageItem.hasItemMeta()) return 1;

        String displayName = ChatColor.stripColor(currentPageItem.getItemMeta().getDisplayName());
        String currentPageTemplate = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', navButtons.getString("current_page", "Page {current} of {total}")));
        String[] parts = currentPageTemplate.split("\\{current\\}");
        if (parts.length < 2) return 1;

        String before = parts[0];
        String after = parts[1].split("\\{total\\}")[0];

        String pageInfo = displayName.replace(before, "").replace(after, "").trim();

        try {
            return Integer.parseInt(pageInfo);
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
