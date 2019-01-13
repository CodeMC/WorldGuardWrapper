package org.codemc.worldguardwrapper.implementation.v7fawe;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.*;
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
import org.bukkit.util.Vector;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.flag.WrappedState;
import org.codemc.worldguardwrapper.implementation.IWorldGuardImplementation;
import org.codemc.worldguardwrapper.implementation.v7fawe.flag.AbstractWrappedFlag;
import org.codemc.worldguardwrapper.implementation.v7fawe.flag.WrappedPrimitiveFlag;
import org.codemc.worldguardwrapper.implementation.v7fawe.flag.WrappedStatusFlag;
import org.codemc.worldguardwrapper.implementation.v7fawe.region.WrappedRegion;
import org.codemc.worldguardwrapper.region.IWrappedRegion;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
public class WorldGuardImplementation implements IWorldGuardImplementation {

    private final WorldGuard core = WorldGuard.getInstance();
    private final FlagRegistry flagRegistry = core.getFlagRegistry();
    private final WorldGuardPlugin plugin = WorldGuardPlugin.inst();

    public static BlockVector asBlockVector(@NonNull Location location) {
        return new BlockVector(BukkitAdapter.asVector(location));
    }

    private Optional<LocalPlayer> wrapPlayer(Player player) {
        return Optional.ofNullable(player).map(bukkitPlayer -> plugin.wrapPlayer(player));
    }

    private Optional<RegionManager> getWorldManager(@NonNull World world) {
        return Optional.ofNullable(core.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)));
    }

    private Optional<ApplicableRegionSet> getApplicableRegions(@NonNull Location location) {
        return getWorldManager(location.getWorld()).map(manager -> manager.getApplicableRegions(asBlockVector(location)));
    }

    private Optional<ApplicableRegionSet> getApplicableRegions(@NonNull Location minimum, @NonNull Location maximum) {
        return getWorldManager(minimum.getWorld()).map(manager -> manager.getApplicableRegions(
                new ProtectedCuboidRegion("temp", asBlockVector(minimum), asBlockVector(maximum))));
    }

    private <V> Optional<V> queryValue(Player player, @NonNull Location location, @NonNull Flag<V> flag) {
        return getApplicableRegions(location).map(applicableRegions -> applicableRegions.queryValue(wrapPlayer(player)
                .orElse(null), flag));
    }

    // TODO: find a better way to define wrapper mappings and register mappings
    @SuppressWarnings("unchecked")
    private <T> IWrappedFlag<T> wrap(Flag<?> flag, Class<T> type) {
        final IWrappedFlag<T> wrappedFlag;
        if (type.equals(WrappedState.class)) {
            wrappedFlag = (IWrappedFlag<T>) new WrappedStatusFlag((Flag<StateFlag.State>) flag);
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            wrappedFlag = new WrappedPrimitiveFlag(flag);
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            wrappedFlag = new WrappedPrimitiveFlag(flag);
        } else if (type.equals(Enum.class)) {
            wrappedFlag = new WrappedPrimitiveFlag(flag);
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            wrappedFlag = new WrappedPrimitiveFlag(flag);
        } else if (type.equals(Location.class)) {
            wrappedFlag = new WrappedPrimitiveFlag(flag);
        } else if (type.equals(String.class)) {
            wrappedFlag = new WrappedPrimitiveFlag(flag);
        } else if (type.equals(Vector.class)) {
            wrappedFlag = new WrappedPrimitiveFlag(flag);
        } else {
            throw new IllegalArgumentException("Unsupported flag type " + type.getName());
        }
        return wrappedFlag;
    }

    @Override
    public JavaPlugin getWorldGuardPlugin() {
        return WorldGuardPlugin.inst();
    }

    @Override
    public int getApiVersion() {
        return 7;
    }

    @Override
    public <T> Optional<IWrappedFlag<T>> getFlag(String name, Class<T> type) {
        return Optional.ofNullable(flagRegistry.get(name))
                .map(flag -> wrap(flag, type));
    }

    @Override
    public <T> Optional<T> queryFlag(Player player, Location location, IWrappedFlag<T> flag) {
        AbstractWrappedFlag<T> wrappedFlag = (AbstractWrappedFlag<T>) flag;
        return wrappedFlag.fromWGValue(queryValue(player, location, wrappedFlag.getHandle()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<IWrappedFlag<T>> registerFlag(String name, Class<T> type, T defaultValue) {
        final Flag<?> flag;
        if (type.equals(WrappedState.class)) {
            flag = new StateFlag(name, defaultValue == WrappedState.ALLOW);
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            flag = new BooleanFlag(name);
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            flag = new DoubleFlag(name);
        } else if (type.equals(Enum.class)) {
            flag = new EnumFlag(name, type);
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            flag = new IntegerFlag(name);
        } else if (type.equals(Location.class)) {
            flag = new LocationFlag(name);
        } else if (type.equals(String.class)) {
            flag = new StringFlag(name, (String) defaultValue);
        } else if (type.equals(Vector.class)) {
            flag = new VectorFlag(name);
        } else {
            throw new IllegalArgumentException("Unsupported flag type " + type.getName());
        }
        try {
            flagRegistry.register(flag);
            return Optional.of(wrap(flag, type));
        } catch (FlagConflictException ignored) {
        }
        return Optional.empty();
    }

    @Override
    public Optional<IWrappedRegion> getRegion(World world, String id) {
        return getWorldManager(world)
                .map(regionManager -> regionManager.getRegion(id))
                .map(region -> new WrappedRegion(world, region));
    }

    @Override
    public Map<String, IWrappedRegion> getRegions(World world) {
        RegionManager regionManager = core.getPlatform().getRegionContainer().get(new BukkitWorld(world));
        if (regionManager == null) {
            return Collections.emptyMap();
        }

        Map<String, ProtectedRegion> regions = regionManager.getRegions();
        Map<String, IWrappedRegion> map = new HashMap<>();
        regions.forEach((name, region) -> map.put(name, new WrappedRegion(world, region)));
        return map;
    }

    @Override
    public Set<IWrappedRegion> getRegions(Location location) {
        ApplicableRegionSet regionSet = getApplicableRegions(location).orElse(null);
        if (regionSet == null) {
            return Collections.emptySet();
        }

        return regionSet.getRegions().stream()
                .map(region -> new WrappedRegion(location.getWorld(), region))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<IWrappedRegion> getRegions(Location minimum, Location maximum) {
        ApplicableRegionSet regionSet = getApplicableRegions(minimum, maximum).orElse(null);
        if (regionSet == null) {
            return Collections.emptySet();
        }

        return regionSet.getRegions().stream()
                .map(region -> new WrappedRegion(minimum.getWorld(), region))
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<IWrappedRegion> addRegion(String id, List<Location> points, int minY, int maxY) {
        ProtectedRegion region;
        World world = points.get(0).getWorld();
        if (points.size() == 2) {
            region = new ProtectedCuboidRegion(id, asBlockVector(points.get(0)),
                    asBlockVector(points.get(1)));
        } else {
            List<BlockVector2D> vectorPoints = points.stream()
                    .map(location -> asBlockVector(location).toBlockVector2D())
                    .collect(Collectors.toList());

            region = new ProtectedPolygonalRegion(id, vectorPoints, minY, maxY);
        }

        Optional<RegionManager> manager = getWorldManager(world);
        if (manager.isPresent()) {
            manager.get().addRegion(region);
            return Optional.of(new WrappedRegion(world, region));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Set<IWrappedRegion>> removeRegion(World world, String id) {
        Optional<Set<ProtectedRegion>> set = getWorldManager(world).map(manager -> manager.removeRegion(id));
        return set.map(protectedRegions -> protectedRegions.stream()
                .map(region -> new WrappedRegion(world, region)).collect(Collectors.toSet()));
    }
}
