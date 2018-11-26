package org.codemc.worldguardwrapper.flag;

import org.bukkit.Location;

/**
 * A flag that stores a bukkit location.
 */
public class WrappedLocationFlag extends AbstractWrappedFlag<Location> {

    public WrappedLocationFlag(String name) {
        this(name, null);
    }

    public WrappedLocationFlag(String name, Location defaultValue) {
        super(name, Location.class, defaultValue);
    }
}
