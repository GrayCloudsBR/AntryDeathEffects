package dev.antry.antrydeatheffects.gui;

import dev.antry.antrydeatheffects.AntryDeathEffects;
import dev.antry.antrydeatheffects.effects.DeathEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EffectsGUI implements Listener {
    private final AntryDeathEffects plugin;
    private final String guiTitle = ChatColor.DARK_PURPLE + "Death Effects";

    public EffectsGUI(AntryDeathEffects plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, guiTitle);
        updateGUI(player, inv);
        player.openInventory(inv);
    }

    private void updateGUI(Player player, Inventory inv) {
        Set<DeathEffect> playerEffects = plugin.getEffectManager().getPlayerEffects(player);
        
        for (DeathEffect effect : plugin.getEffectManager().getAvailableEffects()) {
            ItemStack item = new ItemStack(effect.getIcon());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + effect.getName());
            
            List<String> lore = new ArrayList<>();
            lore.add(playerEffects.contains(effect) 
                    ? ChatColor.GREEN + "Enabled" 
                    : ChatColor.RED + "Disabled");
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

        DeathEffect clickedEffect = plugin.getEffectManager().getAvailableEffects().stream()
                .filter(effect -> effect.getName().equals(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())))
                .findFirst()
                .orElse(null);

        if (clickedEffect != null) {
            if (player.hasPermission(clickedEffect.getPermission())) {
                plugin.getEffectManager().toggleEffect(player, clickedEffect);
                updateGUI(player, event.getInventory());
            } else {
                player.sendMessage(ChatColor.RED + "You don't have permission to use this effect!");
            }
        }
    }
} 