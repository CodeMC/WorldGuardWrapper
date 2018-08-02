package org.codemc.worldguardwrapper;

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

    private WorldGuardWrapper() {
        // TODO: better way to detect version
        try {
            Class.forName("com.sk89q.worldguard.WorldGuard");
            delegate = new org.codemc.worldguardwrapper.implementation.v7.WorldGuardImplementation();
        } catch (ClassNotFoundException e) {
            delegate = new org.codemc.worldguardwrapper.implementation.v6.WorldGuardImplementation();
        }
    }
    
}
