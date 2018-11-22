package org.codemc.worldguardwrapper.region;

import org.bukkit.Location;
import org.codemc.worldguardwrapper.selection.Selection;

import java.util.Map;
import java.util.Optional;

public interface WrappedRegion {

    Selection getSelection();

    String getId();

    Optional<Object> getFlag(String name);

    Map<String, Object> getFlags();

    int getPriority();

    PlayerDomain getOwners();

    PlayerDomain getMembers();

    boolean contains(Location location);

}
