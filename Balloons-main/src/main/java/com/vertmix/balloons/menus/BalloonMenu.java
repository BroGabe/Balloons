package com.vertmix.balloons.menus;

import com.vertmix.balloons.models.Balloon;
import com.vertmix.balloons.BalloonsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BalloonMenu {

    private final BalloonsPlugin plugin;
    private final List<Balloon> balloonList;
    private final int pageSize = 45; // 5 rows for items, 1 row for navigation

    public BalloonMenu(BalloonsPlugin plugin) {
        this.plugin = plugin;
        this.balloonList = new ArrayList<>(plugin.getBalloonManager().getAllBalloons());
    }

    /**
     * Opens the balloons menu for a player at a specific page.
     *
     * @param player The player.
     * @param page   The page number (starting from 1).
     */
    public void openMenu(Player player, int page) {
        int totalPages = (int) Math.ceil((double) balloonList.size() / pageSize);
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;


        String inventoryNameTemplate = plugin.getConfig().getString("balloons_menu.title", "Available Balloons");
        String inventoryName = ChatColor.translateAlternateColorCodes('&', inventoryNameTemplate);

        Inventory menu = Bukkit.createInventory(null, 54, inventoryName);

        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, balloonList.size());


        for (int i = startIndex; i < endIndex; i++) {
            Balloon balloon = balloonList.get(i);
            ItemStack item = plugin.getBalloonManager().getBalloonItem(balloon);
            menu.addItem(item);
        }


        ConfigurationSection navButtons = plugin.getConfig().getConfigurationSection("balloons_menu.navigation_buttons");

        // Previous Page Button
        if (page > 1) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta meta = prevPage.getItemMeta();
            String prevPageName = navButtons.getString("previous_page", "&aPrevious Page");
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', prevPageName));
            prevPage.setItemMeta(meta);
            menu.setItem(45, prevPage);
        }

        // Current Page Indicator
        ItemStack currentPage = new ItemStack(Material.PAPER);
        ItemMeta meta = currentPage.getItemMeta();
        String currentPageTemplate = navButtons.getString("current_page", "&ePage {current} of {total}");
        String currentPageName = currentPageTemplate.replace("{current}", String.valueOf(page)).replace("{total}", String.valueOf(totalPages));
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', currentPageName));
        currentPage.setItemMeta(meta);
        menu.setItem(49, currentPage);

        // Next Page Button
        if (page < totalPages) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            meta = nextPage.getItemMeta();
            String nextPageName = navButtons.getString("next_page", "&aNext Page");
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', nextPageName));
            nextPage.setItemMeta(meta);
            menu.setItem(53, nextPage);
        }

        player.openInventory(menu);
    }
}
