package org.codemc.worldguardwrapper.region;

import java.util.Map;
import java.util.Optional;

import org.bukkit.Location;

public interface WrappedRegion {

    String getId();

    Optional<Object> getFlag(String name);

    Map<String, Object> getFlags();

    int getPriority();

    boolean contains(Location location);

}
