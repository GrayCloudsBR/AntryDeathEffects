package dev.antry.antrydeatheffects.managers;

import dev.antry.antrydeatheffects.AntryDeathEffects;
import dev.antry.antrydeatheffects.effects.*;
import org.bukkit.entity.Player;

import java.util.*;

public class EffectManager {
    private final List<DeathEffect> availableEffects;
    private final Map<UUID, Set<DeathEffect>> playerEffects;

    public EffectManager(AntryDeathEffects plugin) {
        this.availableEffects = new ArrayList<>();
        this.playerEffects = new HashMap<>();
        registerDefaultEffects();
    }

    private void registerDefaultEffects() {
        availableEffects.add(new LightningEffect());
        // Add more effects here
    }

    public void toggleEffect(Player player, DeathEffect effect) {
        UUID uuid = player.getUniqueId();
        playerEffects.putIfAbsent(uuid, new HashSet<>());
        Set<DeathEffect> effects = playerEffects.get(uuid);
        
        if (effects.contains(effect)) {
            effects.remove(effect);
        } else {
            effects.add(effect);
        }
    }

    public Set<DeathEffect> getPlayerEffects(Player player) {
        return playerEffects.getOrDefault(player.getUniqueId(), new HashSet<>());
    }

    public List<DeathEffect> getAvailableEffects() {
        return availableEffects;
    }
} 