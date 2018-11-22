package org.codemc.worldguardwrapper.region;

import org.bukkit.Location;
import org.codemc.worldguardwrapper.selection.Selection;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface WrappedRegion {

    Selection getSelection();

    String getId();

    Optional<Object> getFlag(String name);

    Map<String, Object> getFlags();

    int getPriority();

    Set<UUID> getOwners();

    Set<UUID> getMembers();

    boolean contains(Location location);

}
