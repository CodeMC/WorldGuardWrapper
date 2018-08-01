package org.codemc.worldguardwrapper;

import lombok.experimental.Delegate;
import org.codemc.worldguardwrapper.implementation.IWorldGuardImplementation;

public class WorldGuardWrapper implements IWorldGuardImplementation {

    @Delegate
    private IWorldGuardImplementation implementation;

    public WorldGuardWrapper() {
        // TODO: better way to detect version
        try {
            Class.forName("com.sk89q.worldguard.WorldGuard");
            implementation = new org.codemc.worldguardwrapper.implementation.v7.WorldGuardImplementation();
        } catch (ClassNotFoundException e) {
            implementation = new org.codemc.worldguardwrapper.implementation.v6.WorldGuardImplementation();
        }
    }
}
