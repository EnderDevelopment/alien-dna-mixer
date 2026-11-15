package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public final class AdventureListener implements Listener {
    private final Main plugin;
    private final NamespacedKey itemKey;
    private final Map<EntityType, String> dnaTypes = Map.of(
            EntityType.ZOMBIE, "zombie", EntityType.SPIDER, "spider", EntityType.SKELETON, "skeleton",
            EntityType.SLIME, "slime", EntityType.CREEPER, "creeper", EntityType.ENDERMAN, "enderman",
            EntityType.BEE, "bee", EntityType.BLAZE, "blaze");

    public AdventureListener(Main plugin, NamespacedKey itemKey) {
        this.plugin = plugin;
        this.itemKey = itemKey;
    }

    @EventHandler
    public void onCreatureDefeat(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        String type = dnaTypes.get(event.getEntityType());
        if (player == null || type == null) return;
        player.getInventory().addItem(taggedItem("DNA Sample: " + type, Material.AMETHYST_SHARD, "dna_" + type));
        player.sendMessage(Component.text("You found " + type + " DNA! Alex says every sample in a mix must be different.").color(NamedTextColor.LIGHT_PURPLE));
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack stack = event.getItem();
        if (stack == null || !stack.hasItemMeta()) return;
        String id = stack.getItemMeta().getPersistentDataContainer().get(itemKey, PersistentDataType.STRING);
        if (id == null) return;
        Player player = event.getPlayer();
        if (id.equals("capsule")) {
            event.setCancelled(true);
            stack.setAmount(stack.getAmount() - 1);
            player.getInventory().addItem(taggedItem("Alien Watch", Material.CLOCK, "alien_watch"));
            player.sendMessage(Component.text("The capsule opens! Steve discovers an Alien Watch. Ask Alex what to do with it.").color(NamedTextColor.AQUA));
        } else if (id.equals("alien_serum")) {
            event.setCancelled(true);
            stack.setAmount(stack.getAmount() - 1);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 1200, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1200, 0));
            player.sendMessage(Component.text("Transform complete! You are a friendly super alien for one minute.").color(NamedTextColor.GREEN));
        }
    }

    private ItemStack taggedItem(String name, Material material, String id) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(Component.text(name).color(NamedTextColor.AQUA));
        meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, id);
        stack.setItemMeta(meta);
        return stack;
    }
}
