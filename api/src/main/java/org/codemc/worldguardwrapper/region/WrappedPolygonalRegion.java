package org.codemc.worldguardwrapper.region;

import org.bukkit.Location;

import java.util.Set;

public interface WrappedPolygonalRegion extends WrappedRegion {

    Set<Location> getPoints();

}
