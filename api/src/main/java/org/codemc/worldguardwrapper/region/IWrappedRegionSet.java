package org.codemc.worldguardwrapper.region;

import org.bukkit.OfflinePlayer;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface IWrappedRegionSet extends Iterable<IWrappedRegion> {

    boolean isVirtual();

    <V> Optional<V> queryValue(OfflinePlayer subject, IWrappedFlag<V> flag);

    <V> Collection<V> queryAllValues(OfflinePlayer subject, IWrappedFlag<V> flag);

    boolean isOwnerOfAll(OfflinePlayer player);

    boolean isMemberOfAll(OfflinePlayer player);

    int size();

    Set<IWrappedRegion> getRegions();

}
