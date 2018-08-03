package org.codemc.worldguardwrapper;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.codemc.worldguardwrapper.event.DamageEntityEvent;
import org.codemc.worldguardwrapper.event.UseBlockEvent;
import org.codemc.worldguardwrapper.event.UseEntityEvent;
import org.codemc.worldguardwrapper.implementation.IWorldGuardImplementation;

import lombok.experimental.Delegate;

public class WorldGuardWrapper implements IWorldGuardImplementation {

    private static WorldGuardWrapper instance;

    public static WorldGuardWrapper getInstance() {
        if (instance == null) {
            instance = new WorldGuardWrapper();
        }
        return instance;
    }

    @Delegate
    private IWorldGuardImplementation delegate;
    private Listener eventListener;

    private WorldGuardWrapper() {
        // TODO: better way to detect version
        try {
            Class.forName("com.sk89q.worldguard.WorldGuard");
            delegate = new org.codemc.worldguardwrapper.implementation.v7.WorldGuardImplementation();
            eventListener = new org.codemc.worldguardwrapper.implementation.v7.EventListener(
                UseBlockEvent.class, UseEntityEvent.class, DamageEntityEvent.class);
        } catch (ClassNotFoundException e) {
            delegate = new org.codemc.worldguardwrapper.implementation.v6.WorldGuardImplementation();
            eventListener = new org.codemc.worldguardwrapper.implementation.v6.EventListener(
                UseBlockEvent.class, UseEntityEvent.class, DamageEntityEvent.class);
        }
    }

    /**
     * Forward WorldGuard event calls to wrapped events to allow listening to them
     * without having to use WorldGuard's events.
     * 
     * @param plugin The plugin
     */
    public void registerEvents(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(eventListener, plugin);
    }

}
