package dev.antry.antrydeatheffects.effects;

import dev.antry.antrydeatheffects.gui.FlyingAnimalsSettingsGUI;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlyingAnimalsEffect extends DeathEffect {
    private final Plugin plugin;
    private final Map<UUID, FlyingAnimalSettings> playerSettings;

    public FlyingAnimalsEffect(Plugin plugin) {
        super("Flying Animals", Material.MONSTER_EGG, "antrydeatheffects.effect.flyinganimals");
        this.plugin = plugin;
        this.playerSettings = new HashMap<>();
    }

    @Override
    public void playEffect(Player killer, Player victim, Location location) {
        FlyingAnimalSettings settings = playerSettings.getOrDefault(killer.getUniqueId(), new FlyingAnimalSettings());
        
        // Spawn animals in a circle
        for (int i = 0; i < settings.getAmount(); i++) {
            double angle = (2 * Math.PI * i) / settings.getAmount();
            Location spawnLoc = location.clone().add(Math.cos(angle) * 1, 0, Math.sin(angle) * 1);
            
            Entity animal = location.getWorld().spawnEntity(spawnLoc, settings.getEntityType());
            animal.setMetadata("FlyingAnimal", new FixedMetadataValue(plugin, true));
            
            // Calculate velocity based on direction setting
            Vector velocity;
            switch (settings.getDirection()) {
                case VERTICAL:
                    velocity = new Vector(Math.cos(angle) * 0.5, 1.5, Math.sin(angle) * 0.5);
                    break;
                case HORIZONTAL:
                    velocity = new Vector(Math.cos(angle) * 1.5, 0.5, Math.sin(angle) * 1.5);
                    break;
                default:
                    velocity = new Vector(Math.cos(angle), 1, Math.sin(angle));
            }
            
            animal.setVelocity(velocity);

            // Schedule removal and optional firework
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (settings.isFireworkEnabled()) {
                    spawnFirework(animal.getLocation());
                }
                animal.remove();
            }, 60L); // 3 seconds
        }
    }

    private void spawnFirework(Location location) {
        location.getWorld().spawnEntity(location, EntityType.FIREWORK);
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
        private int amount = 5;

        public EntityType getEntityType() { return entityType; }
        public void setEntityType(EntityType entityType) { this.entityType = entityType; }
        public Direction getDirection() { return direction; }
        public void setDirection(Direction direction) { this.direction = direction; }
        public boolean isFireworkEnabled() { return fireworkEnabled; }
        public void setFireworkEnabled(boolean fireworkEnabled) { this.fireworkEnabled = fireworkEnabled; }
        public int getAmount() { return amount; }
        public void setAmount(int amount) { this.amount = Math.min(Math.max(amount, 1), 10); }
    }

    public enum Direction {
        VERTICAL, HORIZONTAL
    }
} 