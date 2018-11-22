package org.codemc.worldguardwrapper.region;

import org.bukkit.Location;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface WrappedRegion {

    Location getMinimumPoint();

    Location getMaximumPoint();

    String getId();

    Optional<Object> getFlag(String name);

    Map<String, Object> getFlags();

    int getPriority();

    boolean contains(Location location);

}
