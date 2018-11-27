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
    private IWorldGuardImplementation delegate;
    private Listener eventListener;

    private WorldGuardWrapper() {
        String version;
        try {
            Class.forName("com.sk89q.worldguard.WorldGuard");
            version = "v7";
        } catch (ClassNotFoundException e) {
            version = "v6";
        }
        try {
            delegate = (IWorldGuardImplementation) Class.forName("org.codemc.worldguardwrapper.implementation."
                    + version + ".WorldGuardImplementation").newInstance();
            eventListener = (Listener) Class.forName("new org.codemc.worldguardwrapper.implementation."
                    + version + ".event.EventListener").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException("Unable to initialize WorldGuard implementation " + version, e);
        }
    }

    /**
     * Forward WorldGuard event calls to wrapped events to allow listening to them
     * without having to use WorldGuard's events. This is optional.
     *
     * @param plugin the plugin instance
     */
    public void registerEvents(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(eventListener, plugin);
    }

}
