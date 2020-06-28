package org.codemc.worldguardwrapper.implementation.v7;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
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
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.flag.WrappedState;
import org.codemc.worldguardwrapper.implementation.IWorldGuardImplementation;
import org.codemc.worldguardwrapper.implementation.v7.flag.AbstractWrappedFlag;
import org.codemc.worldguardwrapper.implementation.v7.region.WrappedRegion;
import org.codemc.worldguardwrapper.implementation.v7.utility.WorldGuardFlagUtilities;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import org.codemc.worldguardwrapper.selection.ICuboidSelection;
import org.codemc.worldguardwrapper.selection.IPolygonalSelection;
import org.codemc.worldguardwrapper.selection.ISelection;

import java.util.*;
import java.util.stream.Collectors;

public class WorldGuardImplementation implements IWorldGuardImplementation {

    private final WorldGuard core;
    private final FlagRegistry flagRegistry;
    private final WorldGuardPlugin worldGuardPlugin;
    private final WorldEditPlugin worldEditPlugin;

    public WorldGuardImplementation() {
        core = WorldGuard.getInstance();
        flagRegistry = core.getFlagRegistry();
        worldGuardPlugin = WorldGuardPlugin.inst();
        try {
            worldEditPlugin = worldGuardPlugin.getWorldEdit();
        } catch (CommandException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<LocalPlayer> wrapPlayer(Player player) {
        return Optional.ofNullable(player).map(bukkitPlayer -> worldGuardPlugin.wrapPlayer(player));
    }

    private Optional<RegionManager> getWorldManager(@NonNull World world) {
        return Optional.ofNullable(core.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)));
    }

    private Optional<ApplicableRegionSet> getApplicableRegions(@NonNull Location location) {
        return getWorldManager(location.getWorld()).map(manager -> manager.getApplicableRegions(BukkitAdapter.asBlockVector(location)));
    }

    private Optional<ApplicableRegionSet> getApplicableRegions(@NonNull Location minimum, @NonNull Location maximum) {
        return getWorldManager(minimum.getWorld()).map(manager -> manager.getApplicableRegions(
                new ProtectedCuboidRegion("temp", BukkitAdapter.asBlockVector(minimum), BukkitAdapter.asBlockVector(maximum))));
    }

    private <V> Optional<V> queryValue(Player player, @NonNull Location location, @NonNull Flag<V> flag) {
        return getApplicableRegions(location).map(applicableRegions -> applicableRegions.queryValue(wrapPlayer(player)
                .orElse(null), flag));
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
                .map(flag -> WorldGuardFlagUtilities.wrap(flag, type));
    }

    @Override
    public <T> Optional<T> queryFlag(Player player, Location location, IWrappedFlag<T> flag) {
        AbstractWrappedFlag<T> wrappedFlag = (AbstractWrappedFlag<T>) flag;
        return queryValue(player, location, wrappedFlag.getHandle()).flatMap(wrappedFlag::fromWGValue);
    }

    @Override
    public Map<IWrappedFlag<?>, Object> queryApplicableFlags(Player player, Location location) {
        ApplicableRegionSet applicableSet = getApplicableRegions(location).orElse(null);
        if (applicableSet == null) {
            return Collections.emptyMap();
        }

        LocalPlayer localPlayer = wrapPlayer(player).orElse(null);
        Map<IWrappedFlag<?>, Object> flags = new HashMap<>();
        Set<String> seen = new HashSet<>();

        for (ProtectedRegion region : applicableSet.getRegions()) {
            for (Flag<?> flag : region.getFlags().keySet()) {
                if (seen.add(flag.getName())) {
                    Object value = applicableSet.queryValue(localPlayer, flag);
                    if (value == null) {
                        continue;
                    }

                    try {
                        Map.Entry<IWrappedFlag<?>, Object> wrapped = WorldGuardFlagUtilities.wrap(flag, value);
                        flags.put(wrapped.getKey(), wrapped.getValue());
                    } catch (IllegalArgumentException ignored) {
                        // Unsupported flag type
                    }
                }
            }
        }

        return flags;
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
            return Optional.of(WorldGuardFlagUtilities.wrap(flag, type));
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
            region = new ProtectedCuboidRegion(id, BukkitAdapter.asBlockVector(points.get(0)),
                    BukkitAdapter.asBlockVector(points.get(1)));
        } else {
            List<BlockVector2> vectorPoints = points.stream()
                    .map(location -> BukkitAdapter.asBlockVector(location).toBlockVector2())
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

    @Override
    public Optional<ISelection> getPlayerSelection(@NonNull Player player) {
        Region region;
        try {
            region = worldEditPlugin.getSession(player).getSelection(BukkitAdapter.adapt(player.getWorld()));
        } catch (IncompleteRegionException e) {
            region = null;
        }
        return Optional.ofNullable(region)
                .map(selection -> {
                    World world = Optional.ofNullable(selection.getWorld()).map(BukkitAdapter::adapt).orElse(null);
                    if (world == null) {
                        return null;
                    }
                    if (selection instanceof CuboidRegion) {
                        return new ICuboidSelection() {
                            @Override
                            public Location getMinimumPoint() {
                                return BukkitAdapter.adapt(world, selection.getMinimumPoint());
                            }

                            @Override
                            public Location getMaximumPoint() {
                                return BukkitAdapter.adapt(world, selection.getMaximumPoint());
                            }
                        };
                    } else if (selection instanceof Polygonal2DRegion) {
                        return new IPolygonalSelection() {
                            @Override
                            public Set<Location> getPoints() {
                                return ((Polygonal2DRegion) selection).getPoints().stream()
                                        .map(BlockVector2::toBlockVector3)
                                        .map(vector -> BukkitAdapter.adapt(world, vector))
                                        .collect(Collectors.toSet());
                            }

                            @Override
                            public int getMinimumY() {
                                return selection.getMinimumPoint().getBlockY();
                            }

                            @Override
                            public int getMaximumY() {
                                return selection.getMaximumPoint().getBlockY();
                            }
                        };
                    } else {
                        throw new UnsupportedOperationException("Unsupported " + selection.getClass().getSimpleName() + " selection!");
                    }
                });
    }
}
