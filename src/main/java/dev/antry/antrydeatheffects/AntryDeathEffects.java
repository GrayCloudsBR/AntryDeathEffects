package dev.antry.antrydeatheffects;

import dev.antry.antrydeatheffects.commands.EffectsCommand;
import dev.antry.antrydeatheffects.gui.EffectsGUI;
import dev.antry.antrydeatheffects.listeners.DeathListener;
import dev.antry.antrydeatheffects.listeners.AnimalProtectionListener;
import dev.antry.antrydeatheffects.listeners.GraveProtectionListener;
import dev.antry.antrydeatheffects.listeners.FlowerDropListener;
import dev.antry.antrydeatheffects.managers.EffectManager;
import dev.antry.antrydeatheffects.managers.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class AntryDeathEffects extends JavaPlugin {
    private static AntryDeathEffects instance;
    private EffectManager effectManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.effectManager = new EffectManager(this);
        
        // Register events
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(new EffectsGUI(this), this);
        getServer().getPluginManager().registerEvents(new AnimalProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new GraveProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new FlowerDropListener(), this);
        
        // Register commands
        getCommand("deatheffects").setExecutor(new EffectsCommand(this));
    }

    public static AntryDeathEffects getInstance() {
        return instance;
    }

    public EffectManager getEffectManager() {
        return effectManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
