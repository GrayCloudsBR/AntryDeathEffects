package dev.antry.antrydeatheffects.gui;

import dev.antry.antrydeatheffects.effects.FlyingAnimalsEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class FlyingAnimalsSettingsGUI implements Listener {
    private final FlyingAnimalsEffect effect;
    private final Player player;
    private final String guiTitle = ChatColor.DARK_PURPLE + "Flying Animals Settings";

    public FlyingAnimalsSettingsGUI(Plugin plugin, FlyingAnimalsEffect effect, Player player) {
        this.effect = effect;
        this.player = player;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open() {
        Inventory inv = Bukkit.createInventory(null, 27, guiTitle);
        updateInventory(inv);
        player.openInventory(inv);
    }

    private void updateInventory(Inventory inv) {
        inv.clear();
        FlyingAnimalsEffect.FlyingAnimalSettings settings = effect.getPlayerSettings(player.getUniqueId());

        // Animal Type Selection
        ItemStack animalType = new ItemStack(Material.MONSTER_EGG);
        ItemMeta animalMeta = animalType.getItemMeta();
        animalMeta.setDisplayName(ChatColor.GOLD + "Animal Type: " + settings.getEntityType().name());
        animalMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Current: " + settings.getEntityType().name(),
            ChatColor.YELLOW + "Click to change"
        ));
        animalType.setItemMeta(animalMeta);
        inv.setItem(11, animalType);

        // Direction Toggle
        ItemStack direction = new ItemStack(Material.COMPASS);
        ItemMeta directionMeta = direction.getItemMeta();
        directionMeta.setDisplayName(ChatColor.GOLD + "Direction: " + settings.getDirection().name());
        directionMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Current: " + settings.getDirection().name(),
            ChatColor.YELLOW + "Click to toggle"
        ));
        direction.setItemMeta(directionMeta);
        inv.setItem(13, direction);

        // Firework Toggle
        ItemStack firework = new ItemStack(Material.FIREWORK);
        ItemMeta fireworkMeta = firework.getItemMeta();
        fireworkMeta.setDisplayName(ChatColor.GOLD + "Firework Effect");
        fireworkMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Enabled: " + (settings.isFireworkEnabled() ? "Yes" : "No"),
            ChatColor.YELLOW + "Click to toggle"
        ));
        firework.setItemMeta(fireworkMeta);
        inv.setItem(15, firework);

        // Amount Setting
        ItemStack amount = new ItemStack(Material.DIODE);
        ItemMeta amountMeta = amount.getItemMeta();
        amountMeta.setDisplayName(ChatColor.GOLD + "Amount: " + settings.getAmount());
        amountMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Current: " + settings.getAmount(),
            ChatColor.YELLOW + "Left Click: +1",
            ChatColor.YELLOW + "Right Click: -1"
        ));
        amount.setItemMeta(amountMeta);
        inv.setItem(22, amount);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(guiTitle)) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getCurrentItem() == null) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        FlyingAnimalsEffect.FlyingAnimalSettings settings = effect.getPlayerSettings(player.getUniqueId());

        switch (event.getSlot()) {
            case 11: // Animal Type
                cycleEntityType(settings);
                break;
            case 13: // Direction
                settings.setDirection(settings.getDirection() == FlyingAnimalsEffect.Direction.VERTICAL ?
                        FlyingAnimalsEffect.Direction.HORIZONTAL : FlyingAnimalsEffect.Direction.VERTICAL);
                break;
            case 15: // Firework
                settings.setFireworkEnabled(!settings.isFireworkEnabled());
                break;
            case 22: // Amount
                if (event.isLeftClick()) {
                    settings.setAmount(settings.getAmount() + 1);
                } else if (event.isRightClick()) {
                    settings.setAmount(settings.getAmount() - 1);
                }
                break;
        }

        effect.setPlayerSettings(player.getUniqueId(), settings);
        updateInventory(event.getInventory());
        player.playSound(player.getLocation(), org.bukkit.Sound.CLICK, 1.0f, 1.0f);
    }

    private void cycleEntityType(FlyingAnimalsEffect.FlyingAnimalSettings settings) {
        EntityType[] types = {
            EntityType.PIG, EntityType.SHEEP, EntityType.COW, EntityType.CHICKEN
        };
        
        int currentIndex = Arrays.asList(types).indexOf(settings.getEntityType());
        settings.setEntityType(types[(currentIndex + 1) % types.length]);
    }
} 