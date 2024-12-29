package dev.antry.antrydeatheffects.effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LightningEffect extends DeathEffect {
    
    public LightningEffect() {
        super("Lightning", Material.BLAZE_ROD, "antrydeatheffects.effect.lightning");
    }

    @Override
    public void playEffect(Player killer, Player victim, Location location) {
        location.getWorld().strikeLightningEffect(location);
    }
} 