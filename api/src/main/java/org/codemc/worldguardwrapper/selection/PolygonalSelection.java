package org.codemc.worldguardwrapper.selection;

import org.bukkit.Location;

import java.util.Set;

public interface PolygonalSelection extends Selection {

    Set<Location> getPoints();

    int getMinimumY();

    int getMaximumY();

}
