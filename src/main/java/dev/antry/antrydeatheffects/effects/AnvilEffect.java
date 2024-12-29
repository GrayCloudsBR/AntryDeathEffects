package dev.antry.antrydeatheffects.effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Effect;
import dev.antry.antrydeatheffects.managers.ConfigManager;
import dev.antry.antrydeatheffects.AntryDeathEffects;

public class AnvilEffect extends DeathEffect {
    private final Plugin plugin;
    
    public AnvilEffect(Plugin plugin) {
        super("Anvil", Material.ANVIL, "antrydeatheffects.effect.anvil");
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void playEffect(Player killer, Player victim, Location location) {
        ConfigManager config = ((AntryDeathEffects)plugin).getConfigManager();
        
        // Get all config values
        int height = config.getConfig().getInt("effects.anvil.height", 5);
        float soundVolume = (float) config.getConfig().getDouble("effects.anvil.sound-volume", 1.0);
        float soundPitch = (float) config.getConfig().getDouble("effects.anvil.sound-pitch", 1.0);
        boolean explodeOnImpact = config.getConfig().getBoolean("effects.anvil.explosion.enabled", true);
        float explosionPower = (float) config.getConfig().getDouble("effects.anvil.explosion.power", 2.0);
            
        // Spawn anvil at configured height
        Location anvilLoc = location.clone().add(0, height, 0);
        
        // Create falling anvil
        FallingBlock anvil = location.getWorld().spawnFallingBlock(anvilLoc, Material.ANVIL, (byte) 0);
        anvil.setMetadata("DeathAnvil", new FixedMetadataValue(plugin, true));
        anvil.setDropItem(false);
        anvil.setHurtEntities(false);
        
        // Play effect when anvil lands
        new BukkitRunnable() {
            @Override
            public void run() {
                if (anvil.isDead()) {
                    Location landLoc = anvil.getLocation();
                    // Play effects with configured values
                    landLoc.getWorld().playEffect(landLoc, Effect.STEP_SOUND, Material.ANVIL.getId());
                    landLoc.getWorld().playSound(landLoc, org.bukkit.Sound.ANVIL_LAND, soundVolume, soundPitch);
                    
                    // Create explosion if enabled
                    if (explodeOnImpact) {
                        landLoc.getWorld().createExplosion(landLoc.getX(), landLoc.getY(), landLoc.getZ(), 
                            explosionPower, false, false);
                    }
                    
                    // Remove the placed anvil block
                    landLoc.getBlock().setType(Material.AIR);
                    
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }
} 