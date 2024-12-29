package dev.antry.antrydeatheffects.effects;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SoulEscapeEffect extends DeathEffect {
    private final Plugin plugin;
    
    public SoulEscapeEffect(Plugin plugin) {
        super("Soul Escape", Material.SOUL_SAND, "antrydeatheffects.effect.soulescape");
        this.plugin = plugin;
    }

    @Override
    public void playEffect(Player killer, Player victim, Location location) {
        // Spawn multiple bats in a circle
        for (int i = 0; i < 12; i++) { // Increased number of bats
            double angle = (2 * Math.PI * i) / 12;
            Location spawnLoc = location.clone().add(
                Math.cos(angle) * 1.5,
                0,
                Math.sin(angle) * 1.5
            );
            
            Bat bat = (Bat) location.getWorld().spawnEntity(spawnLoc, EntityType.BAT);
            bat.setMetadata("FlyingAnimal", new FixedMetadataValue(plugin, true));
            
            // Random initial velocity
            Vector velocity = new Vector(
                (Math.random() - 0.5) * 2, // Random X between -1 and 1
                Math.random() * 1.5,       // Random Y between 0 and 1.5
                (Math.random() - 0.5) * 2  // Random Z between -1 and 1
            );
            
            // Keep bats flying in random patterns
            new BukkitRunnable() {
                int ticks = 0;
                
                @Override
                public void run() {
                    if (!bat.isValid() || ticks++ > 40) { // 2 seconds
                        if (bat.isValid()) {
                            // Create explosion effect and remove bat
                            bat.getWorld().playEffect(bat.getLocation(), Effect.SMOKE, 4);
                            bat.getWorld().playEffect(bat.getLocation(), Effect.CLOUD, 0);
                            bat.remove();
                        }
                        this.cancel();
                        return;
                    }
                    
                    // Create random movement every tick
                    Vector newVel = new Vector(
                        velocity.getX() + (Math.random() - 0.5) * 0.5,  // Add random X motion
                        velocity.getY() + (Math.random() - 0.3) * 0.3,  // Add random Y motion
                        velocity.getZ() + (Math.random() - 0.5) * 0.5   // Add random Z motion
                    ).normalize().multiply(1.2); // Normalize and set consistent speed
                    
                    bat.setVelocity(newVel);
                    
                    // Add particle effects while flying
                    bat.getWorld().playEffect(bat.getLocation(), Effect.SMOKE, 0);
                }
            }.runTaskTimer(plugin, 0L, 1L);
        }
    }
} 