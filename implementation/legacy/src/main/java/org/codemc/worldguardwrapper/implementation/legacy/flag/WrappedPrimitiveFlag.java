package org.codemc.worldguardwrapper.implementation.legacy.flag;

import com.sk89q.worldguard.protection.flags.Flag;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.codemc.worldguardwrapper.implementation.legacy.utility.WorldGuardFlagUtilities;

import java.util.Optional;

public class WrappedPrimitiveFlag<T> extends AbstractWrappedFlag<T> {

    public WrappedPrimitiveFlag(Flag<T> handle) {
        super(handle);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<T> fromWGValue(Object value) {
        if (value instanceof com.sk89q.worldedit.Location) {
            return Optional.of((T) WorldGuardFlagUtilities.adaptLocation((com.sk89q.worldedit.Location) value));
        } else if (value instanceof com.sk89q.worldedit.Vector) {
            return Optional.of((T) WorldGuardFlagUtilities.adaptVector((com.sk89q.worldedit.Vector) value));
        }
        return Optional.ofNullable((T) value);
    }

    @Override
    public Optional<Object> fromWrapperValue(T value) {
        if (value instanceof Location) {
            return Optional.of(WorldGuardFlagUtilities.adaptLocation((Location) value));
        } else if (value instanceof Vector) {
            return Optional.of(WorldGuardFlagUtilities.adaptVector((Vector) value));
        }
        return Optional.ofNullable(value);
    }

}
