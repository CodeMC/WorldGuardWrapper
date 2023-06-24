package org.codemc.worldguardwrapper.selection;

import org.bukkit.Location;

import java.util.Set;

@SuppressWarnings("unused")
public interface IPolygonalSelection extends ISelection {

    Set<Location> getPoints();

    int getMinimumY();

    int getMaximumY();

}
