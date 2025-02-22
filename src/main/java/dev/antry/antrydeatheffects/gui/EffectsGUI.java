package dev.antry.antrydeatheffects.gui;

import dev.antry.antrydeatheffects.AntryDeathEffects;
import dev.antry.antrydeatheffects.effects.DeathEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class EffectsGUI implements Listener {
    private final AntryDeathEffects plugin;
    private final String guiTitle;
    private final HashMap<Player, Inventory> openInventories = new HashMap<>();

    public EffectsGUI(AntryDeathEffects plugin) {
        this.plugin = plugin;
        this.guiTitle = plugin.getConfigManager().colorize("&5Death Effects");
    }

    public void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, guiTitle);
        updateGUI(player, inv);
        player.openInventory(inv);
        openInventories.put(player, inv);
    }

    private void updateGUI(Player player, Inventory inv) {
        inv.clear();
        Set<DeathEffect> playerEffects = plugin.getEffectManager().getPlayerEffects(player);
        
        for (DeathEffect effect : plugin.getEffectManager().getAvailableEffects()) {
            ItemStack item = new ItemStack(effect.getIcon());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(plugin.getConfigManager().colorize("&6" + effect.getName()));
            
            List<String> lore = new ArrayList<>();
            boolean enabled = playerEffects.contains(effect);
            lore.add(plugin.getConfigManager().colorize(enabled ? "&aSelected" : "&cNot Selected"));
            lore.add(plugin.getConfigManager().colorize("&7Click to " + (enabled ? "deselect" : "select")));
            meta.setLore(lore);
            
            item.setItemMeta(meta);
            inv.addItem(item);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(guiTitle)) return;
        
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() == null) return;
        if (!event.getCurrentItem().hasItemMeta()) return;
        if (!event.getCurrentItem().getItemMeta().hasDisplayName()) return;

        DeathEffect clickedEffect = plugin.getEffectManager().getAvailableEffects().stream()
                .filter(effect -> effect.getName().equals(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())))
                .findFirst()
                .orElse(null);

        if (clickedEffect != null) {
            if (player.hasPermission(clickedEffect.getPermission())) {
                plugin.getEffectManager().toggleEffect(player, clickedEffect);
                updateGUI(player, event.getInventory());
                player.playSound(player.getLocation(), 
                    plugin.getEffectManager().getPlayerEffects(player).contains(clickedEffect) ? 
                    org.bukkit.Sound.CLICK : org.bukkit.Sound.NOTE_BASS, 1.0f, 1.0f);
            } else {
                player.sendMessage(ChatColor.RED + "You don't have permission to use this effect!");
                player.playSound(player.getLocation(), org.bukkit.Sound.NOTE_BASS, 1.0f, 0.5f);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            openInventories.remove((Player) event.getPlayer());
        }
    }
} 