package org.codemc.worldguardwrapper.handler;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.flag.WrappedState;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import org.codemc.worldguardwrapper.region.IWrappedRegionSet;

import java.util.Set;

public interface IHandler {

    default void initialize(Player player, Location current, IWrappedRegionSet regionSet) {
    }

    default boolean testMoveTo(Player player, Location from, Location to, IWrappedRegionSet regionSet, String moveType) {
        return true;
    }

    default boolean onCrossBoundary(Player player, Location from, Location to, IWrappedRegionSet toSet,
                                    Set<IWrappedRegion> entered, Set<IWrappedRegion> exited, String moveType) {
        return true;
    }

    default void tick(Player player, IWrappedRegionSet regionSet) {
    }

    default WrappedState getInvincibility(Player player) {
        return null;
    }

}
