package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

public final class AdventureCommand implements CommandExecutor {
    private static final Set<String> DNA_TYPES = Set.of("zombie", "spider", "skeleton", "slime", "creeper", "enderman", "bee", "blaze");
    private final NamespacedKey itemKey;

    public AdventureCommand(Main plugin, NamespacedKey itemKey) {
        this.itemKey = itemKey;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This adventure is for players.");
            return true;
        }
        return switch (command.getName()) {
            case "adventure" -> start(player, args);
            case "dna" -> mix(player, args);
            case "alex" -> alex(player);
            case "alienbrew" -> brew(player);
            default -> false;
        };
    }

    private boolean start(Player player, String[] args) {
        if (args.length != 1 || !args[0].equalsIgnoreCase("start")) {
            player.sendMessage(Component.text("Use /adventure start to begin.").color(NamedTextColor.YELLOW));
            return true;
        }
        player.getInventory().addItem(item("capsule", "Mysterious Capsule", Material.HEART_OF_THE_SEA,
                "A tiny silver capsule that fell from the sky."));
        player.getInventory().addItem(item("guide", "Alex's Field Guide", Material.BOOK,
                "Alex says: defeat creatures to collect their DNA."));
        player.sendMessage(Component.text("Steve found a strange capsule! Right-click it to reveal the alien watch.").color(NamedTextColor.AQUA));
        player.sendMessage(Component.text("Alex: Collect three different DNA samples, then use /dna mix <one> <two> <three>.").color(NamedTextColor.GREEN));
        return true;
    }

    private boolean mix(Player player, String[] args) {
        if (args.length != 4 || !args[0].equalsIgnoreCase("mix")) {
            player.sendMessage(Component.text("Alex's recipe: /dna mix zombie spider skeleton").color(NamedTextColor.YELLOW));
            return true;
        }
        String[] types = Arrays.stream(Arrays.copyOfRange(args, 1, 4)).map(value -> value.toLowerCase(Locale.ROOT)).toArray(String[]::new);
        if (!DNA_TYPES.containsAll(Arrays.asList(types)) || types[0].equals(types[1]) || types[0].equals(types[2]) || types[1].equals(types[2])) {
            player.sendMessage(Component.text("The DNA must be three known, different creatures. Repeated DNA makes the mixture wobble!").color(NamedTextColor.RED));
            return true;
        }
        for (String type : types) {
            if (!hasDna(player, type)) {
                player.sendMessage(Component.text("You need a " + type + " DNA sample. Defeat that creature first!").color(NamedTextColor.RED));
                return true;
            }
        }
        for (String type : types) removeDna(player, type);
        player.getInventory().addItem(item("alien_dna", "Complete Alien DNA", Material.DRAGON_BREATH,
                "Three unique mob samples have become one friendly alien blueprint."));
        player.sendMessage(Component.text("The DNA glows! Alex says the complete alien is ready for brewing.").color(NamedTextColor.LIGHT_PURPLE));
        return true;
    }

    private boolean alex(Player player) {
        player.sendMessage(Component.text("Alex's Adventure Lab").color(NamedTextColor.GREEN));
        player.sendMessage(Component.text("1. Find the capsule and right-click it to activate the Alien Watch.").color(NamedTextColor.WHITE));
        player.sendMessage(Component.text("2. Defeat creatures to find DNA samples. Use three different names in /dna mix.").color(NamedTextColor.WHITE));
        player.sendMessage(Component.text("3. Craft a brewing kit with /alienbrew after you have the watch and complete DNA.").color(NamedTextColor.WHITE));
        player.sendMessage(Component.text("The finished serum lets Steve transform with a right-click!").color(NamedTextColor.GOLD));
        return true;
    }

    private boolean brew(Player player) {
        if (!hasItem(player, "alien_watch") || !hasItem(player, "alien_dna")) {
            player.sendMessage(Component.text("Alex needs the Alien Watch and Complete Alien DNA before brewing.").color(NamedTextColor.RED));
            return true;
        }
        removeItem(player, "alien_watch");
        removeItem(player, "alien_dna");
        player.getInventory().addItem(item("alien_serum", "Friendly Alien Serum", Material.POTION,
                "Brewed by Alex. Right-click to become a super alien for one minute."));
        player.sendMessage(Component.text("Alex finishes the bubbling brew! Right-click the Friendly Alien Serum.").color(NamedTextColor.GREEN));
        return true;
    }

    private ItemStack item(String id, String name, Material material, String loreText) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(Component.text(name).color(NamedTextColor.AQUA));
        meta.lore(java.util.List.of(Component.text(loreText).color(NamedTextColor.GRAY)));
        meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, id);
        stack.setItemMeta(meta);
        return stack;
    }

    private boolean hasDna(Player player, String type) {
        return hasItem(player, "dna_" + type);
    }

    private boolean hasItem(Player player, String id) {
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack != null && stack.hasItemMeta()
                    && id.equals(stack.getItemMeta().getPersistentDataContainer().get(itemKey, PersistentDataType.STRING))) {
                return true;
            }
        }
        return false;
    }

    private void removeDna(Player player, String type) {
        removeItem(player, "dna_" + type);
    }

    private void removeItem(Player player, String id) {
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack != null && stack.hasItemMeta() && id.equals(stack.getItemMeta().getPersistentDataContainer().get(itemKey, PersistentDataType.STRING))) {
                stack.setAmount(stack.getAmount() - 1);
                return;
            }
        }
    }
}
