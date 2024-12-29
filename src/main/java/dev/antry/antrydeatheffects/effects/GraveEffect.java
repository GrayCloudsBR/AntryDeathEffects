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
        
        // Set sign text using config colors
        sign.setLine(0, configManager.getGraveTitleColor() + "R.I.P");
        sign.setLine(1, configManager.getGraveNameColor() + victim.getName());
        sign.setLine(2, "§7Died Here");  // Gray color
        sign.setLine(3, "§8" + randomYear + " - 2025");  // Dark gray color
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