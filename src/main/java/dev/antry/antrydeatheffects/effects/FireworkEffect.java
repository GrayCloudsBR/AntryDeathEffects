package dev.antry.antrydeatheffects.effects;

import dev.antry.antrydeatheffects.AntryDeathEffects;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Random;

public class FireworkEffect extends DeathEffect {
    private final AntryDeathEffects plugin;
    private final Random random = new Random();

    public FireworkEffect(AntryDeathEffects plugin) {
        super("Firework", Material.FIREWORK, "antrydeatheffects.effect.firework");
        this.plugin = plugin;
    }

    @Override
    public void playEffect(Player killer, Player victim, Location location) {
        ConfigurationSection config = plugin.getConfigManager().getConfig()
            .getConfigurationSection("effects.firework");
        
        int amount = config.getInt("amount", 1);
        int power = config.getInt("power", 2);
        boolean trail = config.getBoolean("trail", true);
        boolean flicker = config.getBoolean("flicker", true);
        
        for (int i = 0; i < amount; i++) {
            Firework fw = location.getWorld().spawn(location, Firework.class);
            FireworkMeta fwm = fw.getFireworkMeta();
            
            // Random colors
            Color color1 = Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255));
            Color color2 = Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255));
            
            // Random firework type
            Type[] types = Type.values();
            Type type = types[random.nextInt(types.length)];
            
            org.bukkit.FireworkEffect effect = org.bukkit.FireworkEffect.builder()
                .withColor(color1)
                .withFade(color2)
                .with(type)
                .trail(trail)
                .flicker(flicker)
                .build();
                
            fwm.addEffect(effect);
            fwm.setPower(power);
            fw.setFireworkMeta(fwm);
        }
    }
} 