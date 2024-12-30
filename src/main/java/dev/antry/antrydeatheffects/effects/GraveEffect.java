package dev.antry.antrydeatheffects.effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.Bukkit;

import java.util.Random;

import dev.antry.antrydeatheffects.AntryDeathEffects;

public class GraveEffect extends DeathEffect {
    private final AntryDeathEffects plugin;
    private final Random random = new Random();
    
    public GraveEffect(AntryDeathEffects plugin) {
        super("Grave", Material.SIGN, "antrydeatheffects.effect.grave");
        this.plugin = plugin;
    }

    private String formatLine(String text, Player victim, ConfigurationSection config) {
        return plugin.getConfigManager().colorize(text
            .replace("%player%", victim.getName())
            .replace("%year%", String.valueOf(
                random.nextInt(
                    config.getInt("years.max", 2024) - 
                    config.getInt("years.min", 2000) + 1) + 
                config.getInt("years.min", 2000)
            )));
    }

    @Override
    public void playEffect(Player killer, Player victim, Location location) {
        ConfigurationSection config = plugin.getConfigManager().getConfig()
            .getConfigurationSection("effects.grave");
            
        ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setCustomNameVisible(true);

        String[] lines = new String[4];
        lines[0] = formatLine(config.getString("lines.line1", "&c&lR.I.P"), victim, config);
        lines[1] = formatLine(config.getString("lines.line2", "&e%player%"), victim, config);
        lines[2] = formatLine(config.getString("lines.line3", "&7Died Here"), victim, config);
        lines[3] = formatLine(config.getString("lines.line4", "&8%year% - 2025"), victim, config);

        stand.setCustomName(String.join("\n", lines));
        
        // Remove after duration
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (stand.isValid()) {
                stand.remove();
            }
        }, config.getInt("duration", 100));
    }
} 