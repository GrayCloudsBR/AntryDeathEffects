package dev.antry.antrydeatheffects.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.block.Block;
import org.bukkit.Material;

public class FlowerDropListener implements Listener {
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.hasMetadata("NoDrops")) {
            event.setCancelled(true);
            block.setType(Material.AIR);
        }
    }
} 