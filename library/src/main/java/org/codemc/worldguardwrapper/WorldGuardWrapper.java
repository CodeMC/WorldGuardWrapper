package org.codemc.worldguardwrapper;

import lombok.experimental.Delegate;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.codemc.worldguardwrapper.implementation.IWorldGuardImplementation;

public class WorldGuardWrapper implements IWorldGuardImplementation {

    private static WorldGuardWrapper instance;

    /**
     * Returns the wrapper singleton instance.
     *
     * @return the wrapper singleton
     */
    public static WorldGuardWrapper getInstance() {
        if (instance == null) {
            instance = new WorldGuardWrapper();
        }
        return instance;
    }

    @Delegate
    private IWorldGuardImplementation implementation;
    private Listener listener;

    private WorldGuardWrapper() {
        int targetVersion;
        try {
            Class.forName("com.sk89q.worldguard.WorldGuard");
            targetVersion = 7;
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.sk89q.worldguard.protection.flags.registry.FlagRegistry");
                targetVersion = 6;
            } catch (ClassNotFoundException e1) {
                targetVersion = -6;
            }
        }
        if (targetVersion == 6) {
            implementation = new org.codemc.worldguardwrapper.implementation.v6.WorldGuardImplementation();
            listener = new org.codemc.worldguardwrapper.implementation.v6.event.EventListener();
        } else if (targetVersion == -6) {
            implementation = new org.codemc.worldguardwrapper.implementation.legacy.WorldGuardImplementation();
            listener = new org.codemc.worldguardwrapper.implementation.legacy.event.EventListener();
        } else {
            /*
            if (Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
                implementation = new org.codemc.worldguardwrapper.implementation.v7fawe.WorldGuardImplementation();
                listener = new org.codemc.worldguardwrapper.implementation.v7fawe.event.EventListener();
            } else {
                implementation = new org.codemc.worldguardwrapper.implementation.v7.WorldGuardImplementation();
                listener = new org.codemc.worldguardwrapper.implementation.v7.event.EventListener();
            }
            */
            implementation = new org.codemc.worldguardwrapper.implementation.v7.WorldGuardImplementation();
            listener = new org.codemc.worldguardwrapper.implementation.v7.event.EventListener();
        }
    }

    /**
     * Forward WorldGuard event calls to wrapped events to allow listening to them
     * without having to use WorldGuard's events. This is optional.
     *
     * @param plugin the plugin instance
     */
    public void registerEvents(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

}
