package dev.antry.antrydeatheffects.effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Random;

import dev.antry.antrydeatheffects.managers.ConfigManager;
import dev.antry.antrydeatheffects.AntryDeathEffects;

public class GraveEffect extends DeathEffect {
    private final Plugin plugin;
    private final Random random = new Random();
    private final ConfigManager configManager;
    
    public GraveEffect(AntryDeathEffects plugin) {
        super("Grave", Material.SIGN, "antrydeatheffects.effect.grave");
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void playEffect(Player killer, Player victim, Location location) {
        // Find ground location
        Location groundLoc = location.clone();
        while (groundLoc.getBlock().getType() == Material.AIR && groundLoc.getY() > 0) {
            groundLoc.subtract(0, 1, 0);
        }
        Location signLoc = groundLoc.add(0, 1, 0);
        
        Block block = signLoc.getBlock();
        
        // Store original block if it's not air (to restore later)
        Material originalMaterial = block.getType();
        byte originalData = block.getData();
        
        // Place the sign
        block.setType(Material.SIGN_POST);
        Sign sign = (Sign) block.getState();
        
        // Generate random year using config values
        int minYear = configManager.getGraveMinYear();
        int maxYear = configManager.getGraveMaxYear();
        int randomYear = minYear + random.nextInt(maxYear - minYear + 1);
        
        // Get text from config and replace placeholders
        String line1 = configManager.getConfig().getString("effects.grave.text.line1", "§c§lR.I.P");
        String line2 = configManager.getConfig().getString("effects.grave.text.line2", "§e%player%").replace("%player%", victim.getName());
        String line3 = configManager.getConfig().getString("effects.grave.text.line3", "§7Died Here");
        String line4 = configManager.getConfig().getString("effects.grave.text.line4", "§8%year% - 2025")
            .replace("%year%", String.valueOf(randomYear));
        
        // Set sign text
        sign.setLine(0, line1);
        sign.setLine(1, line2);
        sign.setLine(2, line3);
        sign.setLine(3, line4);
        sign.update();
        
        // Add metadata to identify this as a grave sign
        block.setMetadata("GraveSign", new FixedMetadataValue(plugin, true));
        
        // Remove sign and restore original block after 5 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                if (block.hasMetadata("GraveSign")) {
                    block.removeMetadata("GraveSign", plugin);
                    block.setType(originalMaterial);
                    block.setData(originalData);
                }
            }
        }.runTaskLater(plugin, configManager.getGraveDuration());
    }
} 