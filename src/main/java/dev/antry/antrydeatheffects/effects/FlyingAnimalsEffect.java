package dev.antry.antrydeatheffects.effects;

import dev.antry.antrydeatheffects.gui.FlyingAnimalsSettingsGUI;
import dev.antry.antrydeatheffects.managers.ConfigManager;
import dev.antry.antrydeatheffects.AntryDeathEffects;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlyingAnimalsEffect extends DeathEffect {
    private final Plugin plugin;
    private final Map<UUID, FlyingAnimalSettings> playerSettings;
    private final ConfigManager configManager;

    public FlyingAnimalsEffect(AntryDeathEffects plugin) {
        super("Flying Animals", Material.MONSTER_EGG, "antrydeatheffects.effect.flyinganimals");
        this.plugin = plugin;
        this.playerSettings = new HashMap<>();
        this.configManager = plugin.getConfigManager();
    }

    @Override
    public void playEffect(Player killer, Player victim, Location location) {
        FlyingAnimalSettings settings = playerSettings.getOrDefault(killer.getUniqueId(), new FlyingAnimalSettings());
        
        Bukkit.getLogger().info("[DEBUG] Starting FlyingAnimalsEffect");
        
        if (settings.isFireworkEnabled()) {
            // Create and setup firework first
            Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();
            
            Bukkit.getLogger().info("[DEBUG] Firework spawned at: " + fw.getLocation());
            
            // Random firework effect
            FireworkEffect effect = FireworkEffect.builder()
                .withColor(Color.fromRGB(
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255)))
                .with(FireworkEffect.Type.BALL_LARGE)
                .withTrail()
                .build();
            
            fwm.addEffect(effect);
            fwm.setPower(2); // Make it fly higher
            fw.setFireworkMeta(fwm);
            
            // Spawn animal and make it ride the firework
            Entity animal = location.getWorld().spawnEntity(location, settings.getEntityType());
            animal.setMetadata("FlyingAnimal", new FixedMetadataValue(plugin, true));
            
            if (animal instanceof org.bukkit.entity.Damageable) {
                ((org.bukkit.entity.Damageable) animal).setMaxHealth(2048.0);
                ((org.bukkit.entity.Damageable) animal).setHealth(2048.0);
            }
            
            // Make animal ride firework immediately
            boolean success = fw.setPassenger(animal);
            Bukkit.getLogger().info("[DEBUG] Attempt to set animal as passenger: " + success);
            Bukkit.getLogger().info("[DEBUG] Firework passenger: " + fw.getPassenger());
            
            // Remove animal when firework explodes
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (animal.isValid()) {
                    animal.remove();
                    Bukkit.getLogger().info("[DEBUG] Removed animal after firework");
                }
            }, 40L);
            
        } else {
            // Original flying animal behavior without firework
            Entity animal = location.getWorld().spawnEntity(location, settings.getEntityType());
            animal.setMetadata("FlyingAnimal", new FixedMetadataValue(plugin, true));
            
            if (animal instanceof org.bukkit.entity.Damageable) {
                ((org.bukkit.entity.Damageable) animal).setMaxHealth(2048.0);
                ((org.bukkit.entity.Damageable) animal).setHealth(2048.0);
            }
            
            Vector velocity;
            if (settings.getDirection() == Direction.VERTICAL) {
                velocity = new Vector(0, configManager.getVerticalVelocity(), 0);
            } else {
                // Get direction from killer to victim and normalize it
                Location killerLoc = killer.getLocation();
                Location victimLoc = victim.getLocation();
                
                // Calculate direction vector
                double dx = killerLoc.getX() - victimLoc.getX();
                double dz = killerLoc.getZ() - victimLoc.getZ();
                
                // Normalize and apply configured velocity
                double length = Math.sqrt(dx * dx + dz * dz);
                dx = (dx / length) * configManager.getHorizontalVelocity();
                dz = (dz / length) * configManager.getHorizontalVelocity();
                
                velocity = new Vector(dx, 0.2, dz); // Small upward component to prevent immediate falling
            }
            
            animal.setVelocity(velocity);
            
            // Keep velocity constant
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!animal.isValid()) {
                        this.cancel();
                        return;
                    }
                    animal.setVelocity(velocity);
                }
            }.runTaskTimer(plugin, 1L, 1L);
            
            Bukkit.getScheduler().runTaskLater(plugin, animal::remove, configManager.getFlyingAnimalsDuration());
        }
        
        Bukkit.getLogger().info("[DEBUG] Effect setup complete");
    }

    public void openSettingsGUI(Player player) {
        new FlyingAnimalsSettingsGUI(plugin, this, player).open();
    }

    public FlyingAnimalSettings getPlayerSettings(UUID playerUUID) {
        return playerSettings.computeIfAbsent(playerUUID, k -> new FlyingAnimalSettings());
    }

    public void setPlayerSettings(UUID playerUUID, FlyingAnimalSettings settings) {
        playerSettings.put(playerUUID, settings);
    }

    public static class FlyingAnimalSettings {
        private EntityType entityType = EntityType.PIG;
        private Direction direction = Direction.VERTICAL;
        private boolean fireworkEnabled = true;

        public EntityType getEntityType() { return entityType; }
        public void setEntityType(EntityType entityType) { this.entityType = entityType; }
        public Direction getDirection() { return direction; }
        public void setDirection(Direction direction) { this.direction = direction; }
        public boolean isFireworkEnabled() { return fireworkEnabled; }
        public void setFireworkEnabled(boolean fireworkEnabled) { this.fireworkEnabled = fireworkEnabled; }
    }

    public enum Direction {
        VERTICAL, HORIZONTAL
    }
} 