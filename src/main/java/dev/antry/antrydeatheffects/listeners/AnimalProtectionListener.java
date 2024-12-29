package dev.antry.antrydeatheffects.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class AnimalProtectionListener implements Listener {
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity.hasMetadata("FlyingAnimal")) {
            event.setCancelled(true);
        }
    }
} 