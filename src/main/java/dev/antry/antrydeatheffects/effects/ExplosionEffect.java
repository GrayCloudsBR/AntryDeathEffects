package dev.antry.antrydeatheffects.effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ExplosionEffect extends DeathEffect {
    
    public ExplosionEffect() {
        super("Explosion", Material.TNT, "antrydeatheffects.effect.explosion");
    }

    @Override
    public void playEffect(Player killer, Player victim, Location location) {
        location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), 2.0F, false, false);
    }
} 