package com.vertmix.balloons.utils;

import com.vertmix.balloons.BalloonsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling configurable messages from config.yml.

 */
public class MessageUtils {

    private static final Map<String, String> messages = new HashMap<>();

    public static void loadMessages(BalloonsPlugin plugin) {
        FileConfiguration config = plugin.getConfig();

        if (config.isSet("messages")) {
            for (String key : config.getConfigurationSection("messages").getKeys(false)) {
                String value = config.getString("messages." + key);
                if (value != null) {
                    messages.put(key, value); // Store raw message without translation
                }
            }
        }
    }

    public static String getMessage(String key) {
        String message = messages.getOrDefault(key, "&cUnknown message key: " + key);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
