package dev.antry.antrydeatheffects.effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import dev.antry.antrydeatheffects.AntryDeathEffects;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class FlowerSpreadEffect extends DeathEffect {
    private final AntryDeathEffects plugin;
    private final Random random = new Random();
    private final Material[] flowers = {
        Material.RED_ROSE,
        Material.YELLOW_FLOWER
    };
    
    public FlowerSpreadEffect(AntryDeathEffects plugin) {
        super("Flower Spread", Material.RED_ROSE, "antrydeatheffects.effect.flowerspread");
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void playEffect(Player killer, Player victim, Location location) {
        final int maxRadius = plugin.getConfigManager().getConfig()
            .getInt("effects.flower-spread.radius", 5);
            
        // Store original blocks before modification
        final Map<Location, String> originalBlocks = new HashMap<>();

        Bukkit.getLogger().info("[DEBUG] Starting FlowerSpreadEffect at: " + location);

        new BukkitRunnable() {
            private int radius = 1;
            
            @Override
            public void run() {
                if (radius > maxRadius) {
                    Bukkit.getLogger().info("[DEBUG] Reached max radius: " + maxRadius);
                    this.cancel();
                    return;
                }
                
                Bukkit.getLogger().info("[DEBUG] Spreading radius: " + radius);
                
                // Create circle of blocks
                for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 16) {
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    Location blockLoc = location.clone().add(x, 0, z);
                    
                    // Find the highest non-air block
                    Block ground = blockLoc.getBlock();
                    while (ground.getY() > 0 && (ground.getType() == Material.AIR || 
                           ground.getType() == Material.WATER || 
                           ground.getType() == Material.LAVA)) {
                        ground = ground.getRelative(0, -1, 0);
                    }
                    
                    // Skip if we hit bedrock or went too low
                    if (ground.getY() <= 0 || ground.getType() == Material.BEDROCK) {
                        continue;
                    }
                    
                    blockLoc = ground.getLocation();
                    Block block = blockLoc.getBlock();
                    Block above = blockLoc.clone().add(0, 1, 0).getBlock();
                    
                    Bukkit.getLogger().info("[DEBUG] Found ground block: " + ground.getType() + " at " + ground.getLocation());
                    
                    // Don't modify air or liquid blocks
                    if (block.getType().isSolid()) {  // Only modify solid blocks
                        Bukkit.getLogger().info("[DEBUG] Attempting to place grass at: " + block.getLocation());
                        
                        // Store original blocks to restore later
                        if (block.getType() != Material.GRASS) {
                            String blockData = block.getType().name() + ":" + block.getData();
                            originalBlocks.put(block.getLocation(), blockData);
                            block.setMetadata("FlowerSpread", new FixedMetadataValue(plugin, blockData));
                            block.setType(Material.GRASS);
                            Bukkit.getLogger().info("[DEBUG] Stored original block: " + blockData);
                        }
                        
                        // Random chance to place flower
                        if (above.getType() == Material.AIR && random.nextFloat() < 0.4) {
                            Material flower = flowers[random.nextInt(flowers.length)];
                            byte data = 0;
                            
                            Bukkit.getLogger().info("[DEBUG] Attempting to place flower: " + flower);
                            
                            // Handle special flowers
                            if (flower == Material.RED_ROSE) {
                                data = (byte) random.nextInt(9);
                            } else {
                                data = 0;
                            }
                            
                            above.setType(flower);
                            above.setData(data);
                            above.setMetadata("FlowerSpread", new FixedMetadataValue(plugin, flower.name() + ":" + data));
                            above.setMetadata("NoDrops", new FixedMetadataValue(plugin, true));
                            Bukkit.getLogger().info("[DEBUG] Placed flower: " + flower + " with data: " + data);
                        }
                    }
                }
                
                radius++;
            }
        }.runTaskTimer(plugin, 0L, 5L);
        
        // Restore blocks after duration with animation
        new BukkitRunnable() {
            private final Set<Location> blocksToRestore = new HashSet<>();
            private int currentRing = maxRadius;

            @Override
            public void run() {
                if (currentRing < 0) {
                    this.cancel();
                    return;
                }

                // Restore blocks in current ring
                for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 16) {
                    double x = Math.cos(angle) * currentRing;
                    double z = Math.sin(angle) * currentRing;
                    Location startLoc = location.clone().add(x, 0, z);

                    // Search downward from this location
                    for (int y = 0; y <= location.getBlockY() + 1; y++) {
                        Location blockLoc = startLoc.clone();
                        blockLoc.setY(location.getBlockY() - y);
                        
                        Block block = blockLoc.getBlock();
                        Block above = blockLoc.clone().add(0, 1, 0).getBlock();

                        // Remove flowers without dropping items
                        if (above.hasMetadata("FlowerSpread")) {
                            above.setType(Material.AIR);
                            above.removeMetadata("FlowerSpread", plugin);
                            above.removeMetadata("NoDrops", plugin);
                        }

                        // Restore ground blocks
                        if (block.hasMetadata("FlowerSpread")) {
                            String[] originalData = block.getMetadata("FlowerSpread").get(0).asString().split(":");
                            Material originalType = Material.valueOf(originalData[0]);
                            byte originalDataByte = Byte.parseByte(originalData[1]);
                            
                            block.setType(originalType);
                            block.setData(originalDataByte);
                            block.removeMetadata("FlowerSpread", plugin);
                            
                            // Add particle effect for restoration
                            block.getWorld().playEffect(block.getLocation().add(0.5, 1, 0.5), 
                                Effect.HAPPY_VILLAGER, 0);
                            
                            Bukkit.getLogger().info("[DEBUG] Restored block to: " + originalType + " at " + block.getLocation());
                        }
                    }
                }
                
                currentRing--;
                // Play sound for restoration wave
                location.getWorld().playSound(location, Sound.DIG_GRASS, 0.5f, 1.0f);
                
            }
        }.runTaskTimer(plugin, 200L, 4L); // Start after 10 seconds, run every 4 ticks
    }
} 