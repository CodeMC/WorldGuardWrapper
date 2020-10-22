package org.codemc.worldguardwrapper.implementation.v7.flag;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldguard.protection.flags.Flag;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.codemc.worldguardwrapper.implementation.v7.utility.WorldGuardFlagUtilities;

import java.util.Optional;

public class WrappedPrimitiveFlag<T> extends AbstractWrappedFlag<T> {

    public WrappedPrimitiveFlag(Flag<T> handle) {
        super(handle);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<T> fromWGValue(Object value) {
        if (value instanceof com.sk89q.worldedit.util.Location) {
            return Optional.of((T) BukkitAdapter.adapt((com.sk89q.worldedit.util.Location) value));
        } else if (value instanceof Vector3) {
            return Optional.of((T) WorldGuardFlagUtilities.adaptVector((Vector3) value));
        }
        return Optional.ofNullable((T) value);
    }

    @Override
    public Optional<Object> fromWrapperValue(T value) {
        if (value instanceof Location) {
            return Optional.of(BukkitAdapter.adapt((Location) value));
        } else if (value instanceof Vector) {
            return Optional.of(WorldGuardFlagUtilities.adaptVector((Vector) value));
        }
        return Optional.ofNullable(value);
    }

}
