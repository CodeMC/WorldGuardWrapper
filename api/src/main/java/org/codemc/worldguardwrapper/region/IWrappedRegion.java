package org.codemc.worldguardwrapper.region;

import org.bukkit.Location;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.selection.ISelection;

import java.util.Map;
import java.util.Optional;

public interface IWrappedRegion {

    ISelection getSelection();

    String getId();

    Map<IWrappedFlag<?>, Object> getFlags();

    <T> Optional<T> getFlag(IWrappedFlag<T> flag);

    <T> void setFlag(IWrappedFlag<T> flag, T value);

    int getPriority();

    IWrappedDomain getOwners();

    IWrappedDomain getMembers();

    boolean contains(Location location);

}
