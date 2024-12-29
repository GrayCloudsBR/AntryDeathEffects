package dev.antry.antrydeatheffects.effects;

import dev.antry.antrydeatheffects.AntryDeathEffects;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.Sound;
import org.bukkit.Effect;
import org.bukkit.configuration.ConfigurationSection;

public class LaunchEffect extends DeathEffect {
    private final AntryDeathEffects plugin;

    public LaunchEffect(AntryDeathEffects plugin) {
        super("Launch", Material.FEATHER, "antrydeatheffects.effect.launch");
        this.plugin = plugin;
    }

    @Override
    public void playEffect(Player killer, Player victim, Location location) {
        ConfigurationSection config = plugin.getConfigManager().getConfig()
            .getConfigurationSection("effects.launch");
            
        double velocity = config.getDouble("velocity", 3.0);
        int duration = config.getInt("duration", 60);
        boolean particles = config.getBoolean("particles.enabled", true);
        String particleType = config.getString("particles.type", "CLOUD");
        boolean sound = config.getBoolean("sound.enabled", true);
        String soundType = config.getString("sound.type", "ENDERDRAGON_WINGS");
        float soundVolume = (float) config.getDouble("sound.volume", 1.0);
        float soundPitch = (float) config.getDouble("sound.pitch", 1.0);
        
        // Launch the player
        victim.setVelocity(new Vector(0, velocity, 0));
        
        // Effects
        new BukkitRunnable() {
            int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= duration || !victim.isOnline()) {
                    this.cancel();
                    return;
                }
                
                if (particles) {
                    victim.getWorld().playEffect(victim.getLocation(), 
                        Effect.valueOf(particleType), 0);
                }
                
                if (sound && ticks % 5 == 0) {
                    victim.getWorld().playSound(victim.getLocation(),
                        Sound.valueOf(soundType), soundVolume, soundPitch);
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
} 