package org.codemc.worldguardwrapper.utility;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.codemc.worldguardwrapper.selection.ICuboidSelection;
import org.codemc.worldguardwrapper.selection.IPolygonalSelection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class SelectionUtilities {

    /**
     * Creates a static cuboid selection.
     *
     * @param first  the first point of the cuboid
     * @param second the second point of the cuboid
     * @return the selection
     */
    public ICuboidSelection createCuboidSelection(Location first, Location second) {
        Location minimum;
        Location maximum;
        if (first.getBlockY() > second.getBlockY()) {
            maximum = first;
            minimum = second;
        } else {
            maximum = second;
            minimum = first;
        }
        return new ICuboidSelection() {
            @Override
            public Location getMinimumPoint() {
                return minimum;
            }

            @Override
            public Location getMaximumPoint() {
                return maximum;
            }
        };
    }

    /**
     * Creates a static polygonal selection.
     *
     * @param points the points of the selection
     * @param minY   the minimum Y coordinate of the selection
     * @param maxY   the maximum Y coordinate of the selection
     * @return the selection
     */
    public IPolygonalSelection createPolygonalSelection(Collection<Location> points, int minY, int maxY) {
        return new IPolygonalSelection() {
            @Override
            public Set<Location> getPoints() {
                return new HashSet<>(points);
            }

            @Override
            public int getMinimumY() {
                return minY;
            }

            @Override
            public int getMaximumY() {
                return maxY;
            }
        };
    }

}
