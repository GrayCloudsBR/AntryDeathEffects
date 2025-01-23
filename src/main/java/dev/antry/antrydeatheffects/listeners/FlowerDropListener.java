package dev.antry.antrydeatheffects.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

public class FlowerDropListener implements Listener {
    
    private final Plugin plugin;
    
    public FlowerDropListener(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.hasMetadata("FlowerSpread")) {
            if (!block.hasMetadata("FlowerSpread_Breakable")) {
                event.setCancelled(true);
                return;
            }
            block.setType(Material.AIR);
            block.removeMetadata("FlowerSpread", plugin);
            block.removeMetadata("FlowerSpread_Breakable", plugin);
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (block.hasMetadata("NoDrops") || block.hasMetadata("FlowerSpread")) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        Block block = event.getLocation().getBlock();
        if (block.hasMetadata("NoDrops") || block.hasMetadata("FlowerSpread")) {
            event.setCancelled(true);
        }
    }
} 