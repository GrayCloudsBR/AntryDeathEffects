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
import org.bukkit.configuration.ConfigurationSection;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class FlowerSpreadEffect extends DeathEffect {
    private final AntryDeathEffects plugin;
    private final Random random = new Random();
    private List<Material> protectedBlocks;
    private Map<Material, List<Byte>> flowerTypes;
    
    public FlowerSpreadEffect(AntryDeathEffects plugin) {
        super("Flower Spread", Material.RED_ROSE, "antrydeatheffects.effect.flowerspread");
        this.plugin = plugin;
        loadConfiguration();
    }
    
    private void loadConfiguration() {
        ConfigurationSection config = plugin.getConfigManager().getConfig().getConfigurationSection("effects.flower-spread");
        
        // Load protected blocks
        protectedBlocks = config.getStringList("protection.protected-blocks").stream()
            .map(Material::valueOf)
            .collect(Collectors.toList());
            
        // Load flower types
        flowerTypes = new HashMap<>();
        ConfigurationSection flowersSection = config.getConfigurationSection("flowers");
        for (String flowerName : flowersSection.getKeys(false)) {
            if (flowersSection.getBoolean(flowerName + ".enabled")) {
                Material flowerType = Material.valueOf(flowerName);
                List<Byte> dataValues = flowersSection.getIntegerList(flowerName + ".data-values")
                    .stream()
                    .map(i -> (byte)(int)i)
                    .collect(Collectors.toList());
                flowerTypes.put(flowerType, dataValues);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void playEffect(Player killer, Player victim, Location location) {
        ConfigurationSection config = plugin.getConfigManager().getConfig().getConfigurationSection("effects.flower-spread");
        final int maxRadius = config.getInt("radius", 5);
        final int spreadDelay = config.getInt("spread-delay", 5);
        final int duration = config.getInt("duration", 200);
        final double flowerChance = config.getDouble("flower-chance", 0.4);
        final boolean protectionEnabled = config.getBoolean("protection.enabled", true);
        
        // Store original blocks before modification
        final Map<Location, String> originalBlocks = new HashMap<>();

        new BukkitRunnable() {
            private int radius = 1;
            
            @Override
            public void run() {
                if (radius > maxRadius) {
                    this.cancel();
                    return;
                }
                
                // Create circle of blocks
                for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 16) {
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    Location blockLoc = location.clone().add(x, 0, z);
                    
                    Block ground = findGround(blockLoc);
                    if (ground == null) continue;
                    
                    blockLoc = ground.getLocation();
                    Block block = blockLoc.getBlock();
                    Block above = blockLoc.clone().add(0, 1, 0).getBlock();
                    
                    // Check if block is protected
                    if (protectionEnabled && protectedBlocks.contains(block.getType())) {
                        continue;
                    }
                    
                    // Don't modify air or liquid blocks
                    if (block.getType().isSolid()) {
                        if (block.getType() != Material.GRASS) {
                            String blockData = block.getType().name() + ":" + block.getData();
                            originalBlocks.put(block.getLocation(), blockData);
                            block.setMetadata("FlowerSpread", new FixedMetadataValue(plugin, blockData));
                            if (!protectionEnabled) {
                                block.setMetadata("FlowerSpread_Breakable", new FixedMetadataValue(plugin, true));
                            }
                            block.setType(Material.GRASS);
                        }
                        
                        // Random chance to place flower
                        if (above.getType() == Material.AIR && random.nextFloat() < flowerChance) {
                            placeRandomFlower(above);
                        }
                    }
                }
                
                radius++;
            }
        }.runTaskTimer(plugin, 0L, spreadDelay);
        
        // Restoration animation
        new BukkitRunnable() {
            private int currentRing = maxRadius;

            @Override
            public void run() {
                if (currentRing < 0) {
                    this.cancel();
                    return;
                }

                restoreRing(location, currentRing, config);
                currentRing--;
            }
        }.runTaskTimer(plugin, duration, 4L);
    }
    
    private Block findGround(Location location) {
        Block ground = location.getBlock();
        while (ground.getY() > 0 && (ground.getType() == Material.AIR || 
               ground.getType() == Material.WATER || 
               ground.getType() == Material.LAVA)) {
            ground = ground.getRelative(0, -1, 0);
        }
        
        return ground.getY() <= 0 || ground.getType() == Material.BEDROCK ? null : ground;
    }
    
    @SuppressWarnings("deprecation")
    private void placeRandomFlower(Block block) {
        List<Material> availableFlowers = new ArrayList<>(flowerTypes.keySet());
        Material flower = availableFlowers.get(random.nextInt(availableFlowers.size()));
        List<Byte> possibleData = flowerTypes.get(flower);
        byte data = possibleData.get(random.nextInt(possibleData.size()));
        
        block.setType(flower);
        block.setData(data);
        block.setMetadata("FlowerSpread", new FixedMetadataValue(plugin, flower.name() + ":" + data));
        block.setMetadata("NoDrops", new FixedMetadataValue(plugin, true));
    }
    
    @SuppressWarnings("deprecation")
    private void restoreRing(Location center, int radius, ConfigurationSection config) {
        String particleType = config.getString("particles.type", "HAPPY_VILLAGER");
        int particleCount = config.getInt("particles.count", 1);
        String soundType = config.getString("sounds.restore.type", "DIG_GRASS");
        float soundVolume = (float) config.getDouble("sounds.restore.volume", 0.5);
        float soundPitch = (float) config.getDouble("sounds.restore.pitch", 1.0);
        
        for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 16) {
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            
            for (int y = 0; y < center.getWorld().getMaxHeight(); y++) {
                Location checkLoc = center.clone().add(x, y - center.getBlockY(), z);
                Block block = checkLoc.getBlock();
                
                if (block.hasMetadata("FlowerSpread")) {
                    if (block.getType() == Material.GRASS) {
                        String[] originalData = block.getMetadata("FlowerSpread").get(0).asString().split(":");
                        block.setType(Material.valueOf(originalData[0]));
                        block.setData(Byte.parseByte(originalData[1]));
                    } else {
                        block.setType(Material.AIR);
                    }
                    block.removeMetadata("FlowerSpread", plugin);
                    block.removeMetadata("NoDrops", plugin);
                    block.removeMetadata("FlowerSpread_Breakable", plugin);
                    
                    // Play effects
                    block.getWorld().playEffect(block.getLocation().add(0.5, 1, 0.5), 
                        Effect.valueOf(particleType), particleCount);
                }
            }
        }
        
        // Play sound for restoration wave
        center.getWorld().playSound(center, Sound.valueOf(soundType), soundVolume, soundPitch);
    }
} 