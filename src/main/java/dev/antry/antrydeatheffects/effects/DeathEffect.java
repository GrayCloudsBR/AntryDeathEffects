package dev.antry.antrydeatheffects.effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class DeathEffect {
    private final String name;
    private final Material icon;
    private final String permission;

    public DeathEffect(String name, Material icon, String permission) {
        this.name = name;
        this.icon = icon;
        this.permission = permission;
    }

    public abstract void playEffect(Player killer, Player victim, Location location);

    public String getName() {
        return name;
    }

    public Material getIcon() {
        return icon;
    }

    public String getPermission() {
        return permission;
    }
} 