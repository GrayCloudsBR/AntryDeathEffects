package dev.antry.antrydeatheffects.effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;

import java.util.Random;

import dev.antry.antrydeatheffects.managers.ConfigManager;
import dev.antry.antrydeatheffects.AntryDeathEffects;

public class GraveEffect extends DeathEffect {
    private final AntryDeathEffects plugin;
    private final Random random = new Random();
    private final ConfigManager configManager;
    
    public GraveEffect(AntryDeathEffects plugin) {
        super("Grave", Material.SIGN, "antrydeatheffects.effect.grave");
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    private String formatLine(String line, String color, Player victim, ConfigurationSection config) {
        return color + line
            .replace("%player%", victim.getName())
            .replace("%year%", String.valueOf(
                random.nextInt(
                    config.getInt("effects.grave.years.max", 2024) - 
                    config.getInt("effects.grave.years.min", 2000) + 1) + 
                config.getInt("effects.grave.years.min", 2000)
            ));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void playEffect(Player killer, Player victim, Location location) {
        ConfigurationSection config = plugin.getConfigManager().getConfig()
            .getConfigurationSection("effects.grave");
            
        ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);
        // ... other armor stand setup ...

        String[] lines = new String[4];
        lines[0] = formatLine(config.getString("text.line1", "R.I.P"), 
                            config.getString("colors.line1", "§c§l"), victim, config);
        lines[1] = formatLine(config.getString("text.line2", "%player%"), 
                            config.getString("colors.line2", "§e"), victim, config);
        lines[2] = formatLine(config.getString("text.line3", "Died Here"), 
                            config.getString("colors.line3", "§7"), victim, config);
        lines[3] = formatLine(config.getString("text.line4", "%year% - 2025"), 
                            config.getString("colors.line4", "§8"), victim, config);

        // ... rest of the effect code ...
    }
} 