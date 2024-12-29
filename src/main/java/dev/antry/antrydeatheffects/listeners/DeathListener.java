package dev.antry.antrydeatheffects.listeners;

import dev.antry.antrydeatheffects.AntryDeathEffects;
import dev.antry.antrydeatheffects.effects.DeathEffect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Set;

public class DeathListener implements Listener {
    private final AntryDeathEffects plugin;

    public DeathListener(AntryDeathEffects plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null) {
            Set<DeathEffect> effects = plugin.getEffectManager().getPlayerEffects(killer);
            for (DeathEffect effect : effects) {
                effect.playEffect(killer, victim, victim.getLocation());
            }
        }
    }
} 