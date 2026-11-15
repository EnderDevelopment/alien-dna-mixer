package com.enderdevelopment.plugin;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private NamespacedKey itemKey;
    private AdventureCommand adventureCommand;

    @Override
    public void onEnable() {
        itemKey = new NamespacedKey(this, "adventure_item");
        adventureCommand = new AdventureCommand(this, itemKey);
        getCommand("adventure").setExecutor(adventureCommand);
        getCommand("dna").setExecutor(adventureCommand);
        getCommand("alex").setExecutor(adventureCommand);
        getCommand("alienbrew").setExecutor(adventureCommand);
        getServer().getPluginManager().registerEvents(new AdventureListener(this, itemKey), this);
    }

    @Override
    public void onDisable() {
    }
}
