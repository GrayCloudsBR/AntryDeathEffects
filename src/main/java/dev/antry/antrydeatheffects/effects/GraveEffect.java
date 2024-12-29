package dev.antry.antrydeatheffects.effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class GraveEffect extends DeathEffect {
    private final Plugin plugin;
    private final Random random = new Random();
    
    public GraveEffect(Plugin plugin) {
        super("Grave", Material.SIGN, "antrydeatheffects.effect.grave");
        this.plugin = plugin;
    }

    @Override
    public void playEffect(Player killer, Player victim, Location location) {
        // Find suitable location for the sign (one block up from death location)
        Location signLoc = location.clone().add(0, 1, 0);
        Block block = signLoc.getBlock();
        
        // Store original block if it's not air (to restore later)
        Material originalMaterial = block.getType();
        byte originalData = block.getData();
        
        // Place the sign
        block.setType(Material.SIGN_POST);
        Sign sign = (Sign) block.getState();
        
        // Generate random year between 2000 and 2024
        int randomYear = 2000 + random.nextInt(25);
        
        // Set sign text
        sign.setLine(0, "R.I.P");
        sign.setLine(1, victim.getName());
        sign.setLine(2, "Died Here");
        sign.setLine(3, randomYear + " - 2025");
        sign.update();
        
        // Remove sign and restore original block after 5 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                if (block.getType() == Material.SIGN_POST) {
                    block.setType(originalMaterial);
                    block.setData(originalData);
                }
            }
        }.runTaskLater(plugin, 100L); // 5 seconds
    }
} 