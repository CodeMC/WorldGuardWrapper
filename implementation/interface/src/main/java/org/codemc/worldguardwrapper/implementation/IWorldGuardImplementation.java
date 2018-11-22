package org.codemc.worldguardwrapper.implementation;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.codemc.worldguardwrapper.flags.AbstractWrappedFlag;
import org.codemc.worldguardwrapper.region.WrappedRegion;

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
     * Query a StateFlag's value for a given player at a given location.
     *
     * @param player   The player
     * @param location The location
     * @param flagName The flag's name
     * @return The flag's value
     */
    Optional<Boolean> queryStateFlag(Player player, @NonNull Location location, @NonNull String flagName);

    /**
     * Register a {@code StateFlag}.
     *
     * @param flagName     The name of the flag
     * @param defaultValue The flag's default value
     * @return Whether the flag has been registered
     */
    boolean registerStateFlag(@NonNull String flagName, @NonNull Boolean defaultValue);

    /**
     * Query a flag's value for a given player at a given location.
     *
     * @param player   The player
     * @param location The location
     * @param flagName The name of the flag
     * @param type     The type of the flag's value
     * @return The flag's value
     */
    <T> Optional<T> queryFlag(Player player, @NonNull Location location, @NonNull String flagName, Class<T> type);

    /**
     * Query a flag's value for a given player at a given location.
     *
     * @param player   The player
     * @param location The location
     * @param flag     The flag
     * @return The flag's value
     */
    default <T> Optional<T> queryFlag(Player player, @NonNull Location location, @NonNull AbstractWrappedFlag<T> flag) {
        return queryFlag(player, location, flag.getName(), flag.getType());
    }

    /**
     * Register a flag to WorldGuard's flag registry.
     *
     * @param flag The flag to register
     * @return Whether the flag has been registered
     */
    <T> boolean registerFlag(@NonNull AbstractWrappedFlag<T> flag);

    /**
     * Get a region by its ID.
     *
     * @param world The world
     * @param id    ID of the region
     * @return The region
     */
    Optional<WrappedRegion> getRegion(@NonNull World world, @NonNull String id);

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
    Map<String, WrappedRegion> getRegions(@NonNull World world);

    /**
     * Get a set of regions at the given location.
     *
     * @param location The location
     * @return A set of regions
     */
    Set<WrappedRegion> getRegions(@NonNull Location location);


    /**
     * Get a set of regions in the given cuboid area.
     *
     * @param minimum The minimum location of the area
     * @param maximum The maximum location of the area
     * @return A set of regions
     */
    Set<WrappedRegion> getRegions(@NonNull Location minimum, @NonNull Location maximum);

    /**
     * Add a region. If only two points are given, a cuboid region will be created.
     *
     * @param id     The region ID
     * @param points A {@link List} of points that the region should contain
     * @param minY   The minimum y coordinate
     * @param maxY   The maximum y coordinate
     * @return The added region
     */
    Optional<WrappedRegion> addRegion(@NonNull String id, @NonNull List<Location> points, int minY, int maxY);

    /**
     * Add a cuboid region.
     *
     * @param id     The region ID
     * @param point1 The first point of the region
     * @param point2 The second point of the region
     * @return The added region
     */
    default Optional<WrappedRegion> addCuboidRegion(@NonNull String id, @NonNull Location point1, @NonNull Location point2) {
        return addRegion(id, Arrays.asList(point1, point2), 0, 0);
    }

    /**
     * Remove a region, including inheriting children.
     *
     * @param world The world
     * @param id    The region ID
     * @return A list of removed regions where the first entry is the region specified by {@code id}
     */
    Optional<Set<WrappedRegion>> removeRegion(@NonNull World world, @NonNull String id);

}
