package org.codemc.worldguardwrapper.implementation;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import org.codemc.worldguardwrapper.selection.ICuboidSelection;
import org.codemc.worldguardwrapper.selection.IPolygonalSelection;
import org.codemc.worldguardwrapper.selection.ISelection;

import java.util.*;

public interface IWorldGuardImplementation {

    /**
     * Get an instance of the WorldGuardPlugin class
     *
     * @return The WorldGuard plugin
     */
    JavaPlugin getWorldGuardPlugin();

    /**
     * Get the API version of the loaded WorldGuard plugin (e.g. 6 or 7).
     *
     * @return The API version
     */
    int getApiVersion();

    /**
     * Query a flag's value for a given player at a given location.
     *
     * @param player   The player
     * @param location The location
     * @param flag     The flag
     * @return The flag's value
     */
    <T> Optional<T> queryFlag(Player player, @NonNull Location location, @NonNull IWrappedFlag<T> flag);

    /**
     * Queries all applicable flags for a given player at a given location.
     *
     * @param player   The player
     * @param location The location
     * @return The flags
     */
    Map<IWrappedFlag<?>, Object> queryApplicableFlags(Player player, Location location);

    /**
     * Returns the flag with the given name.
     *
     * @param name The flag name
     * @param type The flag type
     * @return The flag, empty if it doesn't exists
     */
    <T> Optional<IWrappedFlag<T>> getFlag(String name, Class<T> type);

    /**
     * Registers a flag to WorldGuard's flag registry.
     *
     * @param name         The flag name
     * @param type         The flag type
     * @param defaultValue the flag default value (if supported by the type), can be null
     * @return The created flag, empty if a name conflict occurred
     */
    <T> Optional<IWrappedFlag<T>> registerFlag(@NonNull String name, @NonNull Class<T> type, T defaultValue);

    /**
     * Registers a flag to WorldGuard's flag registry.
     *
     * @param name The flag name
     * @param type The flag type
     * @return The created flag, empty if a name conflict occurred
     */
    default <T> Optional<IWrappedFlag<T>> registerFlag(@NonNull String name, @NonNull Class<T> type) {
        return registerFlag(name, type, null);
    }

    /**
     * Get a region by its ID.
     *
     * @param world The world
     * @param id    ID of the region
     * @return The region
     */
    Optional<IWrappedRegion> getRegion(@NonNull World world, @NonNull String id);

    /**
     * Get an unmodifiable map of regions containing the state of the
     * index at the time of call.
     *
     * <p>This call is relatively heavy (and may block other threads),
     * so refrain from calling it frequently.</p>
     *
     * @param world The world
     * @return A map of regions
     */
    Map<String, IWrappedRegion> getRegions(@NonNull World world);

    /**
     * Get a set of regions at the given location.
     *
     * @param location The location
     * @return A set of regions
     */
    Set<IWrappedRegion> getRegions(@NonNull Location location);


    /**
     * Get a set of regions in the given cuboid area.
     *
     * @param minimum The minimum location of the area
     * @param maximum The maximum location of the area
     * @return A set of regions
     */
    Set<IWrappedRegion> getRegions(@NonNull Location minimum, @NonNull Location maximum);

    /**
     * Add a region. If only two points are given, a cuboid region will be created.
     *
     * @param id     The region ID
     * @param points A {@link List} of points that the region should contain
     * @param minY   The minimum y coordinate
     * @param maxY   The maximum y coordinate
     * @return The added region
     */
    Optional<IWrappedRegion> addRegion(@NonNull String id, @NonNull List<Location> points, int minY, int maxY);

    /**
     * Add a cuboid region.
     *
     * @param id     The region ID
     * @param point1 The first point of the region
     * @param point2 The second point of the region
     * @return The added region
     */
    default Optional<IWrappedRegion> addCuboidRegion(@NonNull String id, @NonNull Location point1, @NonNull Location point2) {
        return addRegion(id, Arrays.asList(point1, point2), 0, 0);
    }

    /**
     * Add a region for the given selection.
     * 
     * @param id        The region ID
     * @param selection The selection for the region's volume
     * @return The added region
     */
    default Optional<IWrappedRegion> addRegion(@NonNull String id, @NonNull ISelection selection) {
        if (selection instanceof ICuboidSelection) {
            ICuboidSelection sel = (ICuboidSelection) selection;
            return addCuboidRegion(id, sel.getMinimumPoint(), sel.getMaximumPoint());
        } else if (selection instanceof IPolygonalSelection) {
            IPolygonalSelection sel = (IPolygonalSelection) selection;
            return addRegion(id, new ArrayList<>(sel.getPoints()), sel.getMinimumY(), sel.getMaximumY());
        }
        return Optional.empty();
    }

    /**
     * Remove a region, including inheriting children.
     *
     * @param world The world
     * @param id    The region ID
     * @return A list of removed regions where the first entry is the region specified by {@code id}
     */
    Optional<Set<IWrappedRegion>> removeRegion(@NonNull World world, @NonNull String id);

}
