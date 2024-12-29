package dev.antry.antrydeatheffects.effects;

import dev.antry.antrydeatheffects.AntryDeathEffects;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Firework;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;
import org.bukkit.configuration.ConfigurationSection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Random;

public class FlyingAnimalsEffect extends DeathEffect {
    private final AntryDeathEffects plugin;
    private final Random random = new Random();

    public FlyingAnimalsEffect(AntryDeathEffects plugin) {
        super("Flying Animals", Material.MONSTER_EGG, "antrydeatheffects.effect.flyinganimals");
        this.plugin = plugin;
    }

    @Override
    public void playEffect(Player killer, Player victim, Location location) {
        ConfigurationSection config = plugin.getConfigManager().getConfig()
            .getConfigurationSection("effects.flying-animals");
            
        // Get configuration values
        boolean useFirework = config.getBoolean("firework.enabled", true);
        EntityType animalType = EntityType.valueOf(config.getString("animal-type", "SHEEP"));
        
        if (useFirework) {
            // Create and setup firework
            Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();
            
            // Random firework effect
            List<String> colorStrings = config.getStringList("firework.colors");
            List<Color> colors = colorStrings.stream()
                .map(s -> {
                    String[] rgb = s.split(",");
                    return Color.fromRGB(
                        Integer.parseInt(rgb[0]),
                        Integer.parseInt(rgb[1]),
                        Integer.parseInt(rgb[2])
                    );
                })
                .collect(Collectors.toList());
            
            Color randomColor = colors.get(random.nextInt(colors.size()));
            
            List<String> types = config.getStringList("firework.types");
            String typeString = types.get(random.nextInt(types.size()));
            org.bukkit.FireworkEffect.Type type = org.bukkit.FireworkEffect.Type.valueOf(typeString);
            
            org.bukkit.FireworkEffect effect = org.bukkit.FireworkEffect.builder()
                .withColor(randomColor)
                .with(type)
                .trail(config.getBoolean("firework.trail", true))
                .flicker(config.getBoolean("firework.flicker", true))
                .build();
            
            fwm.addEffect(effect);
            fwm.setPower(config.getInt("firework.power", 2));
            fw.setFireworkMeta(fwm);
            
            // Spawn animal and make it ride the firework
            Entity animal = location.getWorld().spawnEntity(location, animalType);
            animal.setMetadata("FlyingAnimal", new FixedMetadataValue(plugin, true));
            
            if (animal instanceof org.bukkit.entity.Damageable) {
                ((org.bukkit.entity.Damageable) animal).setMaxHealth(2048.0);
                ((org.bukkit.entity.Damageable) animal).setHealth(2048.0);
            }
            
            fw.setPassenger(animal);
            
            // Remove animal when firework explodes
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (animal.isValid()) {
                    animal.remove();
                }
            }, config.getInt("duration", 40));
            
        } else {
            // Create animal
            Entity animal = location.getWorld().spawnEntity(location, animalType);
            animal.setMetadata("FlyingAnimal", new FixedMetadataValue(plugin, true));
            
            if (animal instanceof org.bukkit.entity.Damageable) {
                ((org.bukkit.entity.Damageable) animal).setMaxHealth(2048.0);
                ((org.bukkit.entity.Damageable) animal).setHealth(2048.0);
            }
            
            // Calculate velocities
            double verticalVel = config.getDouble("velocity.vertical", 0.6);
            double horizontalVel = config.getDouble("velocity.horizontal", 0.6);
            double upwardOffset = config.getDouble("velocity.upward-offset", 0.2);
            
            // Launch in circle pattern
            for (int i = 0; i < 360; i += 45) {
                double angle = Math.toRadians(i);
                double dx = Math.cos(angle) * horizontalVel;
                double dz = Math.sin(angle) * horizontalVel;

                Vector velocity = new Vector(dx, verticalVel + upwardOffset, dz);
                animal.setVelocity(velocity);
            }
            
            // Remove animal after duration
            Bukkit.getScheduler().runTaskLater(plugin, animal::remove, 
                config.getInt("duration", 40));
        }
    }
} 