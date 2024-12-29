package dev.antry.antrydeatheffects.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;

public class GraveProtectionListener implements Listener {
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.hasMetadata("GraveSign")) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (block.hasMetadata("GraveSign")) {
            event.setCancelled(true);
        }
    }
} 