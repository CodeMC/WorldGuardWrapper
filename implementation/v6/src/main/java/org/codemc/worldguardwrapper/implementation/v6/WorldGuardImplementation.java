package org.codemc.worldguardwrapper.implementation.v6;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.codemc.worldguardwrapper.flag.AbstractWrappedFlag;
import org.codemc.worldguardwrapper.flag.WrappedBooleanFlag;
import org.codemc.worldguardwrapper.implementation.IWorldGuardImplementation;
import org.codemc.worldguardwrapper.implementation.wrapper.WrapperAdapter;
import org.codemc.worldguardwrapper.implementation.wrapper.WrapperAdapterRegister;
import org.codemc.worldguardwrapper.region.WrappedDomain;
import org.codemc.worldguardwrapper.region.WrappedRegion;
import org.codemc.worldguardwrapper.selection.CuboidSelection;
import org.codemc.worldguardwrapper.selection.PolygonalSelection;
import org.codemc.worldguardwrapper.selection.Selection;

import java.util.*;
import java.util.stream.Collectors;

public class WorldGuardImplementation implements IWorldGuardImplementation {

    private final WorldGuardPlugin plugin = WorldGuardPlugin.inst();
    private final FlagRegistry flagRegistry = plugin.getFlagRegistry();

    private final WrapperAdapterRegister<AbstractWrappedFlag<?>, Flag<?>> flagAdapter = new WrapperAdapterRegister<>();

    @SuppressWarnings("unchecked")
    public WorldGuardImplementation() {
        // Register the flag adapters
        flagAdapter.register(new WrapperAdapter<>(WrappedBooleanFlag.class, BooleanFlag.class,
                flag -> new WrappedBooleanFlag(flag.getName()), flag -> new BooleanFlag(flag.getName())));
    }

    private Optional<LocalPlayer> wrapPlayer(Player player) {
        return Optional.ofNullable(player).map(bukkitPlayer -> plugin.wrapPlayer(player));
    }

    private Optional<RegionManager> getWorldManager(@NonNull World world) {
        return Optional.ofNullable(plugin.getRegionManager(world));
    }

    private Optional<ApplicableRegionSet> getApplicableRegions(@NonNull Location location) {
        return getWorldManager(location.getWorld()).map(manager -> manager.getApplicableRegions(location));
    }

    private Optional<ApplicableRegionSet> getApplicableRegions(@NonNull Location minimum, @NonNull Location maximum) {
        return getWorldManager(minimum.getWorld()).map(manager -> manager.getApplicableRegions(
                new ProtectedCuboidRegion("temp", toBlockVector(minimum), toBlockVector(maximum))));
    }

    private <V> Optional<V> queryValue(Player player, @NonNull Location location, @NonNull Flag<V> flag) {
        return getApplicableRegions(location).map(applicableRegions -> applicableRegions.queryValue(wrapPlayer(player)
                .orElse(null), flag));
    }

    private Optional<StateFlag.State> queryState(Player player, @NonNull Location location, @NonNull StateFlag... stateFlags) {
        return getApplicableRegions(location).map(applicableRegions -> applicableRegions.queryState(wrapPlayer(player)
                .orElse(null), stateFlags));
    }

    private BlockVector toBlockVector(Location location) {
        return new BlockVector(location.getX(), location.getY(), location.getZ());
    }

    private Location fromBlockVector(World world, BlockVector vector) {
        return new Location(world, vector.getX(), vector.getY(), vector.getZ());
    }

    private List<BlockVector2D> toBlockVector2DList(List<Location> locations) {
        return locations.stream().map(location -> new BlockVector2D(location.getX(), location.getZ())).collect(Collectors.toList());
    }

    private WrappedRegion toRegion(World world, ProtectedRegion region) {
        return new WrappedRegion() {

            @Override
            public Selection getSelection() {
                if (region instanceof PolygonalSelection) {
                    return new PolygonalSelection() {

                        @Override
                        public Set<Location> getPoints() {
                            return region.getPoints().stream()
                                    .map(vector -> new BlockVector(vector.toVector()))
                                    .map(vector -> fromBlockVector(world, vector))
                                    .collect(Collectors.toSet());
                        }

                        @Override
                        public int getMinimumY() {
                            return ((PolygonalSelection) region).getMinimumY();
                        }

                        @Override
                        public int getMaximumY() {
                            return ((PolygonalSelection) region).getMaximumY();
                        }
                    };
                }
                return new CuboidSelection() {

                    @Override
                    public Location getMinimumPoint() {
                        return fromBlockVector(world, region.getMinimumPoint());
                    }

                    @Override
                    public Location getMaximumPoint() {
                        return fromBlockVector(world, region.getMaximumPoint());
                    }
                };
            }

            @Override
            public String getId() {
                return region.getId();
            }

            @Override
            public Map<String, Object> getFlags() {
                Map<String, Object> map = new HashMap<>();
                region.getFlags().forEach((flag, value) -> map.put(flag.getName(), value));
                return map;
            }

            @Override
            public Map<AbstractWrappedFlag<?>, Object> getWrappedFlags() {
                Map<AbstractWrappedFlag<?>, Object> map = new HashMap<>();
                region.getFlags().forEach((flag, value) ->
                        flagAdapter.wrap(flag).ifPresent(wrapped -> map.put(wrapped, value)));
                return map;
            }

            @Override
            public Optional<Object> getFlag(String name) {
                return Optional.ofNullable(flagRegistry.get(name))
                        .map(region::getFlag);
            }

            @Override
            public int getPriority() {
                return region.getPriority();
            }

            @Override
            public WrappedDomain getOwners() {
                return new WrappedDomain() {
                    @Override
                    public Set<UUID> getPlayers() {
                        return region.getOwners().getUniqueIds();
                    }

                    @Override
                    public void addPlayer(UUID uuid) {
                        region.getOwners().addPlayer(uuid);
                    }

                    @Override
                    public void removePlayer(UUID uuid) {
                        region.getOwners().removePlayer(uuid);
                    }

                    @Override
                    public Set<String> getGroups() {
                        return region.getOwners().getGroups();
                    }

                    @Override
                    public void addGroup(String name) {
                        region.getOwners().addGroup(name);
                    }

                    @Override
                    public void removeGroup(String name) {
                        region.getOwners().removeGroup(name);
                    }
                };
            }

            @Override
            public WrappedDomain getMembers() {
                return new WrappedDomain() {
                    @Override
                    public Set<UUID> getPlayers() {
                        return region.getMembers().getUniqueIds();
                    }

                    @Override
                    public void addPlayer(UUID uuid) {
                        region.getMembers().addPlayer(uuid);
                    }

                    @Override
                    public void removePlayer(UUID uuid) {
                        region.getMembers().removePlayer(uuid);
                    }

                    @Override
                    public Set<String> getGroups() {
                        return region.getMembers().getGroups();
                    }

                    @Override
                    public void addGroup(String name) {
                        region.getMembers().addGroup(name);
                    }

                    @Override
                    public void removeGroup(String name) {
                        region.getMembers().removeGroup(name);
                    }
                };
            }

            @Override
            public boolean contains(Location location) {
                return region.contains(toBlockVector(location));
            }
        };
    }

    @Override
    public JavaPlugin getWorldGuardPlugin() {
        return WorldGuardPlugin.inst();
    }

    @Override
    public int getApiVersion() {
        return 6;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> queryFlag(Player player, Location location, String flagName, Class<T> type) {
        Flag<?> flag = flagRegistry.get(flagName);
        Object value = queryValue(player, location, flag).orElse(null);
        if (type.isInstance(value)) {
            return Optional.of((T) value);
        }
        return Optional.empty();
    }

    @Override
    public boolean registerFlag(AbstractWrappedFlag<?> flag) {
        Optional<Flag<?>> unwrapped = flagAdapter.unwrap(flag);
        if (unwrapped.isPresent()) {
            try {
                flagRegistry.register(unwrapped.get());
                return true;
            } catch (FlagConflictException ignored) {
            }
        }
        return false;
    }

    @Override
    public Optional<WrappedRegion> getRegion(World world, String id) {
        return getWorldManager(world).map(regionManager -> toRegion(world, regionManager.getRegion(id)));
    }

    @Override
    public Map<String, WrappedRegion> getRegions(World world) {
        RegionManager regionManager = plugin.getRegionManager(world);
        Map<String, ProtectedRegion> regions = regionManager.getRegions();

        Map<String, WrappedRegion> map = new HashMap<>();
        regions.forEach((name, region) -> map.put(name, toRegion(world, region)));

        return map;
    }

    @Override
    public Set<WrappedRegion> getRegions(Location location) {
        ApplicableRegionSet regionSet = getApplicableRegions(location).orElse(null);
        Set<WrappedRegion> set = new HashSet<>();

        if (regionSet == null) {
            return set;
        }

        regionSet.forEach(region -> set.add(toRegion(location.getWorld(), region)));
        return set;
    }

    @Override
    public Set<WrappedRegion> getRegions(Location minimum, Location maximum) {
        ApplicableRegionSet regionSet = getApplicableRegions(minimum, maximum).orElse(null);
        Set<WrappedRegion> set = new HashSet<>();

        if (regionSet == null) {
            return set;
        }

        regionSet.forEach(region -> set.add(toRegion(minimum.getWorld(), region)));
        return set;
    }

    @Override
    public Optional<WrappedRegion> addRegion(String id, List<Location> points, int minY, int maxY) {
        ProtectedRegion region;
        World world = points.get(0).getWorld();
        if (points.size() == 2) {
            region = new ProtectedCuboidRegion(id, toBlockVector(points.get(0)), toBlockVector(points.get(1)));
        } else {
            region = new ProtectedPolygonalRegion(id, toBlockVector2DList(points), minY, maxY);
        }

        Optional<RegionManager> manager = getWorldManager(world);
        if (manager.isPresent()) {
            manager.get().addRegion(region);
            return Optional.of(toRegion(world, region));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Set<WrappedRegion>> removeRegion(World world, String id) {
        Optional<Set<ProtectedRegion>> set = getWorldManager(world).map(manager -> manager.removeRegion(id));
        return set.map(protectedRegions -> protectedRegions.stream().map(region -> toRegion(world, region))
                .collect(Collectors.toSet()));
    }

}
