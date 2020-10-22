package org.codemc.worldguardwrapper.implementation.v6.utility;

import com.google.common.collect.Maps;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.flag.WrappedState;
import org.codemc.worldguardwrapper.implementation.v6.flag.AbstractWrappedFlag;
import org.codemc.worldguardwrapper.implementation.v6.flag.WrappedPrimitiveFlag;
import org.codemc.worldguardwrapper.implementation.v6.flag.WrappedStatusFlag;

import java.util.Map;

@UtilityClass
public class WorldGuardFlagUtilities {

    // TODO: find a better way to define wrapper mappings and register mappings
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> IWrappedFlag<T> wrap(Flag<?> flag, Class<T> type) {
        final IWrappedFlag<T> wrappedFlag;
        if (type.equals(WrappedState.class)) {
            wrappedFlag = (IWrappedFlag<T>) new WrappedStatusFlag((Flag<StateFlag.State>) flag);
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            wrappedFlag = new WrappedPrimitiveFlag(flag);
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            wrappedFlag = new WrappedPrimitiveFlag(flag);
        } else if (type.equals(Enum.class)) {
            wrappedFlag = new WrappedPrimitiveFlag(flag);
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            wrappedFlag = new WrappedPrimitiveFlag(flag);
        } else if (type.equals(Location.class)) {
            wrappedFlag = new WrappedPrimitiveFlag(flag);
        } else if (type.equals(String.class)) {
            wrappedFlag = new WrappedPrimitiveFlag(flag);
        } else if (type.equals(Vector.class)) {
            wrappedFlag = new WrappedPrimitiveFlag(flag);
        } else {
            throw new IllegalArgumentException("Unsupported flag type " + type.getName());
        }
        return wrappedFlag;
    }

    // Used when the flag's type is not known, so it has to be derived from a sample value's class
    public IWrappedFlag<?> wrapFixType(Flag<?> flag, Class<?> type) {
        if (StateFlag.State.class.isAssignableFrom(type)) {
            // StateFlag
            type = WrappedState.class;
        } else if (com.sk89q.worldedit.util.Location.class.isAssignableFrom(type)) {
            // LocationFlag
            type = org.bukkit.Location.class;
        } else if (com.sk89q.worldedit.Vector.class.isAssignableFrom(type)) {
            // VectorFlag
            type = Vector.class;
        }

        return wrap(flag, type);
    }

    public Map.Entry<IWrappedFlag<?>, Object> wrap(Flag<?> flag, Object value) {
        IWrappedFlag<?> wrappedFlag = wrapFixType(flag, value.getClass());
        //noinspection OptionalGetWithoutIsPresent
        Object wrappedValue = ((AbstractWrappedFlag<?>) wrappedFlag).fromWGValue(value).get(); // value is non-null
        return Maps.immutableEntry(wrappedFlag, wrappedValue);
    }

    public Vector adaptVector(com.sk89q.worldedit.Vector vector) {
        return new Vector(vector.getX(), vector.getY(), vector.getZ());
    }

    public com.sk89q.worldedit.Vector adaptVector(Vector vector) {
        return new com.sk89q.worldedit.Vector(vector.getX(), vector.getY(), vector.getZ());
    }

    public Location adaptLocation(com.sk89q.worldedit.util.Location location) {
        World world = location.getExtent() instanceof BukkitWorld
                ? ((BukkitWorld) location.getExtent()).getWorld() : null;

        return new Location(world, location.getX(), location.getY(), location.getZ());
    }

    public com.sk89q.worldedit.util.Location adaptLocation(Location location) {
        return new com.sk89q.worldedit.util.Location(new BukkitWorld(location.getWorld()),
                location.getX(), location.getY(), location.getZ());
    }

}