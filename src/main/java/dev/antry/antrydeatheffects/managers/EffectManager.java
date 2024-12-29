package dev.antry.antrydeatheffects.managers;

import dev.antry.antrydeatheffects.AntryDeathEffects;
import dev.antry.antrydeatheffects.effects.*;
import org.bukkit.entity.Player;

import java.util.*;

public class EffectManager {
    private final AntryDeathEffects plugin;
    private final List<DeathEffect> availableEffects;
    private final Map<UUID, DeathEffect> playerEffects;

    public EffectManager(AntryDeathEffects plugin) {
        this.plugin = plugin;
        this.availableEffects = new ArrayList<>();
        this.playerEffects = new HashMap<>();
        registerDefaultEffects();
    }

    private void registerDefaultEffects() {
        availableEffects.add(new LightningEffect());
        availableEffects.add(new ExplosionEffect());
        availableEffects.add(new FlyingAnimalsEffect((AntryDeathEffects)plugin));
        availableEffects.add(new SoulEscapeEffect(plugin));
        availableEffects.add(new GraveEffect((AntryDeathEffects)plugin));
        availableEffects.add(new AnvilEffect(plugin));
        availableEffects.add(new FlowerSpreadEffect(plugin));
        availableEffects.add(new FireworkEffect(plugin));
        availableEffects.add(new LaunchEffect(plugin));
    }

    public void toggleEffect(Player player, DeathEffect effect) {
        UUID uuid = player.getUniqueId();
        
        if (playerEffects.containsKey(uuid) && playerEffects.get(uuid).equals(effect)) {
            playerEffects.remove(uuid);
        } else {
            playerEffects.put(uuid, effect);
        }
    }

    public Set<DeathEffect> getPlayerEffects(Player player) {
        Set<DeathEffect> effects = new HashSet<>();
        DeathEffect effect = playerEffects.get(player.getUniqueId());
        if (effect != null) {
            effects.add(effect);
        }
        return effects;
    }

    public List<DeathEffect> getAvailableEffects() {
        return availableEffects;
    }
} 