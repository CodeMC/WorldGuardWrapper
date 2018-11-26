package org.codemc.worldguardwrapper.region;

import org.bukkit.Location;
import org.codemc.worldguardwrapper.flag.AbstractWrappedFlag;
import org.codemc.worldguardwrapper.selection.Selection;

import java.util.Map;
import java.util.Optional;

public interface WrappedRegion {

    Selection getSelection();

    String getId();

    Optional<Object> getFlag(String name);

    Map<String, Object> getFlags();

    Map<AbstractWrappedFlag<?>, Object> getWrappedFlags();

    int getPriority();

    WrappedDomain getOwners();

    WrappedDomain getMembers();

    boolean contains(Location location);

}
