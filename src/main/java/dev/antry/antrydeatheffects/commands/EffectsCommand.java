package dev.antry.antrydeatheffects.commands;

import dev.antry.antrydeatheffects.AntryDeathEffects;
import dev.antry.antrydeatheffects.gui.EffectsGUI;
import org.bukkit.ChatColor;
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
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("antrydeatheffects.use")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        new EffectsGUI(plugin).openGUI(player);
        return true;
    }
} 