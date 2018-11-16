package org.codemc.worldguardwrapper.implementation.v6;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.codemc.worldguardwrapper.flags.AbstractWrappedFlag;
import org.codemc.worldguardwrapper.implementation.IWorldGuardImplementation;
import org.codemc.worldguardwrapper.region.WrappedRegion;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
public class WorldGuardImplementation implements IWorldGuardImplementation {

    private final WorldGuardPlugin plugin = WorldGuardPlugin.inst();
    private final FlagRegistry flagRegistry = plugin.getFlagRegistry();

    private Optional<LocalPlayer> wrapPlayer(Player player) {
        return Optional.ofNullable(player).map(bukkitPlayer -> plugin.wrapPlayer(player));
    }

    private Optional<RegionManager> getWorldManager(@NonNull World world) {
        return Optional.ofNullable(plugin.getRegionManager(world));
    }

    private Optional<ApplicableRegionSet> getApplicableRegions(@NonNull Location location) {
        return getWorldManager(location.getWorld()).map(manager -> manager.getApplicableRegions(location));
    }

    private <V> Optional<V> queryValue(Player player, @NonNull Location location, @NonNull Flag<V> flag) {
        return getApplicableRegions(location).map(applicableRegions -> applicableRegions.queryValue(wrapPlayer(player).orElse(null), flag));
    }

    private Optional<StateFlag.State> queryState(Player player, @NonNull Location location, @NonNull StateFlag... stateFlags) {
        return getApplicableRegions(location).map(applicableRegions -> applicableRegions.queryState(wrapPlayer(player).orElse(null), stateFlags));
    }

    private BlockVector toBlockVector(Location location) {
        return new BlockVector(location.getX(), location.getY(), location.getZ());
    }

    private List<BlockVector2D> toBlockVector2DList(List<Location> locations) {
        return locations.stream().map(location -> new BlockVector2D(location.getX(), location.getZ())).collect(Collectors.toList());
    }

    private WrappedRegion toRegion(ProtectedRegion region) {
        return new WrappedRegion() {

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
            public Optional<Object> getFlag(String name) {
                return Optional.ofNullable(flagRegistry.get(name))
                        .map(region::getFlag);
            }

            @Override
            public int getPriority() {
                return region.getPriority();
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
    public Optional<Boolean> queryStateFlag(Player player, @NonNull Location location, @NonNull String flagId) {
        Flag<?> flag = flagRegistry.get(flagId);
        if (!(flag instanceof StateFlag)) {
            return Optional.empty();
        }
        return queryState(player, location, (StateFlag) flag).map(state -> state == StateFlag.State.ALLOW);
    }

    @Override
    public boolean registerStateFlag(@NonNull String flagId, @NonNull Boolean defaultValue) {
        try {
            flagRegistry.register(new StateFlag(flagId, defaultValue));
            return true;
        } catch (FlagConflictException ignored) {
        }
        return false;
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
    public <T> boolean registerFlag(AbstractWrappedFlag<T> flag) {
        Flag<T> wgFlag = new Flag<T>(flag.getName()) {
            @Override
            public T getDefault() {
                return flag.getDefaultValue();
            }

            @Override
            public Object marshal(T o) {
                return flag.serialize(o);
            }

            @Override
            public T unmarshal(Object o) {
                return flag.deserialize(o);
            }

            @Override
            public T parseInput(FlagContext context) throws InvalidFlagFormat {
                return flag.parse(context.getPlayerSender(), context.getUserInput());
            }
        };

        try {
            flagRegistry.register(wgFlag);
            return true;
        } catch (FlagConflictException ignored) {
        }
        return false;
    }

    @Override
    public Optional<WrappedRegion> getRegion(World world, String id) {
        return getWorldManager(world).map(regionManager -> toRegion(regionManager.getRegion(id)));
    }

    @Override
    public Map<String, WrappedRegion> getRegions(World world) {
        RegionManager regionManager = plugin.getRegionManager(world);
        Map<String, ProtectedRegion> regions = regionManager.getRegions();

        Map<String, WrappedRegion> map = new HashMap<>();
        regions.forEach((name, region) -> map.put(name, toRegion(region)));

        return map;
    }

    @Override
    public Set<WrappedRegion> getRegions(Location location) {
        ApplicableRegionSet regionSet = getApplicableRegions(location).orElse(null);
        Set<WrappedRegion> set = new HashSet<>();

        if (regionSet == null) {
            return set;
        }

        regionSet.forEach(region -> set.add(toRegion(region)));
        return set;
    }

    @Override
    public Optional<WrappedRegion> addRegion(String id, List<Location> points, int minY, int maxY) {
        ProtectedRegion region;
        if (points.size() == 2) {
            region = new ProtectedCuboidRegion(id, toBlockVector(points.get(0)), toBlockVector(points.get(1)));
        } else {
            region = new ProtectedPolygonalRegion(id, toBlockVector2DList(points), minY, maxY);
        }

        Optional<RegionManager> manager = getWorldManager(points.get(0).getWorld());
        if (manager.isPresent()) {
            manager.get().addRegion(region);
            return Optional.of(toRegion(region));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Set<WrappedRegion>> removeRegion(World world, String id) {
        Optional<Set<ProtectedRegion>> set = getWorldManager(world).map(manager -> manager.removeRegion(id));
        if (set.isPresent()) {
            return Optional.of(set.get().stream().map(region -> toRegion(region)).collect(Collectors.toSet()));
        } else {
            return Optional.empty();
        }
    }

}
