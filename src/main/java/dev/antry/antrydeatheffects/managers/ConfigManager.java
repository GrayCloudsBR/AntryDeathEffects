package dev.antry.antrydeatheffects.managers;

import dev.antry.antrydeatheffects.AntryDeathEffects;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.ChatColor;

public class ConfigManager {
    private final AntryDeathEffects plugin;
    private FileConfiguration config;
    private String prefix;

    public ConfigManager(AntryDeathEffects plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
        prefix = config.getString("prefix", "&f&l[&3&lAntryPlugins&7&l] ");
    }

    public String getPrefix() {
        return prefix;
    }

    public String formatMessage(String path) {
        String message = config.getString("messages." + path, "");
        return ChatColor.translateAlternateColorCodes('&', 
            message.replace("%prefix%", prefix));
    }

    // Flying Animals Effect
    public int getFlyingAnimalsDuration() {
        return config.getInt("effects.flying-animals.duration", 40);
    }

    public int getFireworkPower() {
        return config.getInt("effects.flying-animals.firework.power", 2);
    }

    public boolean isFireworkEnabledByDefault() {
        return config.getBoolean("effects.flying-animals.firework.enabled-by-default", true);
    }

    public double getVerticalVelocity() {
        return config.getDouble("effects.flying-animals.velocity.vertical", 0.6);
    }

    public double getHorizontalVelocity() {
        return config.getDouble("effects.flying-animals.velocity.horizontal", 0.6);
    }

    // Soul Escape Effect
    public int getSoulEscapeDuration() {
        return config.getInt("effects.soul-escape.duration", 40);
    }

    public int getBatCount() {
        return config.getInt("effects.soul-escape.bat-count", 12);
    }

    public double getBatBaseVelocity() {
        return config.getDouble("effects.soul-escape.velocity.base", 1.2);
    }

    public double getBatRandomFactor() {
        return config.getDouble("effects.soul-escape.velocity.random-factor", 0.5);
    }

    // Grave Effect
    public int getGraveDuration() {
        return config.getInt("effects.grave.duration", 100);
    }

    public int getGraveMinYear() {
        return config.getInt("effects.grave.years.min", 2000);
    }

    public int getGraveMaxYear() {
        return config.getInt("effects.grave.years.max", 2024);
    }

    public String getGraveTitleColor() {
        return config.getString("effects.grave.colors.title", "§c§l");
    }

    public String getGraveNameColor() {
        return config.getString("effects.grave.colors.name", "§e");
    }

    public FileConfiguration getConfig() {
        return config;
    }

    // ... Add other getters as needed
} 