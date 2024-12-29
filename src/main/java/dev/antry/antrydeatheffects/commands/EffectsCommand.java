package dev.antry.antrydeatheffects.commands;

import dev.antry.antrydeatheffects.AntryDeathEffects;
import dev.antry.antrydeatheffects.gui.EffectsGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EffectsCommand implements CommandExecutor {
    private final AntryDeathEffects plugin;

    public EffectsCommand(AntryDeathEffects plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("antrydeatheffects.reload")) {
                plugin.getConfigManager().loadConfig();
                sender.sendMessage(plugin.getConfigManager().formatMessage("reload-success"));
                return true;
            }
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().formatMessage("player-only"));
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("antrydeatheffects.use")) {
            player.sendMessage(plugin.getConfigManager().formatMessage("no-permission"));
            return true;
        }

        new EffectsGUI(plugin).openGUI(player);
        return true;
    }
} 