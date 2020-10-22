package org.codemc.worldguardwrapper.implementation.v6;

import com.google.common.collect.Iterators;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import javassist.util.proxy.ProxyFactory;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.flag.WrappedState;
import org.codemc.worldguardwrapper.handler.IHandler;
import org.codemc.worldguardwrapper.implementation.IWorldGuardImplementation;
import org.codemc.worldguardwrapper.implementation.v6.flag.AbstractWrappedFlag;
import org.codemc.worldguardwrapper.implementation.v6.handler.ProxyHandler;
import org.codemc.worldguardwrapper.implementation.v6.region.WrappedRegion;
import org.codemc.worldguardwrapper.implementation.v6.utility.WorldGuardFlagUtilities;
import org.codemc.worldguardwrapper.implementation.v6.utility.WorldGuardVectorUtilities;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import org.codemc.worldguardwrapper.region.IWrappedRegionSet;
import org.codemc.worldguardwrapper.selection.ICuboidSelection;
import org.codemc.worldguardwrapper.selection.IPolygonalSelection;
import org.codemc.worldguardwrapper.selection.ISelection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class WorldGuardImplementation implements IWorldGuardImplementation {

    private final WorldGuardPlugin worldGuardPlugin;
    private final WorldEditPlugin worldEditPlugin;
    private final FlagRegistry flagRegistry;

    public WorldGuardImplementation() {
        worldGuardPlugin = WorldGuardPlugin.inst();
        try {
            worldEditPlugin = worldGuardPlugin.getWorldEdit();
        } catch (CommandException e) {
            throw new RuntimeException(e);
        }
        flagRegistry = worldGuardPlugin.getFlagRegistry();
    }

    private Optional<LocalPlayer> wrapPlayer(OfflinePlayer player) {
        return Optional.ofNullable(player).map(bukkitPlayer -> bukkitPlayer.isOnline() ?
                worldGuardPlugin.wrapPlayer((Player) bukkitPlayer) : worldGuardPlugin.wrapOfflinePlayer(bukkitPlayer));
    }

    private Optional<RegionManager> getWorldManager(@NonNull World world) {
        return Optional.ofNullable(worldGuardPlugin.getRegionManager(world));
    }

    private Optional<ApplicableRegionSet> getApplicableRegions(@NonNull Location location) {
        return getWorldManager(location.getWorld()).map(manager -> manager.getApplicableRegions(location));
    }

    private Optional<ApplicableRegionSet> getApplicableRegions(@NonNull Location minimum, @NonNull Location maximum) {
        return getWorldManager(minimum.getWorld()).map(manager -> manager.getApplicableRegions(
                new ProtectedCuboidRegion("temp", WorldGuardVectorUtilities.toBlockVector(minimum), WorldGuardVectorUtilities.toBlockVector(maximum))));
    }

    private <V> Optional<V> queryValue(Player player, @NonNull Location location, @NonNull Flag<V> flag) {
        return getApplicableRegions(location).map(applicableRegions -> applicableRegions.queryValue(wrapPlayer(player)
                .orElse(null), flag));
    }

    public IWrappedRegionSet wrapRegionSet(@NonNull World world, @NonNull ApplicableRegionSet regionSet) {
        return new IWrappedRegionSet() {

            @SuppressWarnings("NullableProblems")
            @Override
            public Iterator<IWrappedRegion> iterator() {
                return Iterators.transform(regionSet.iterator(), region -> new WrappedRegion(world, region));
            }

            @Override
            public boolean isVirtual() {
                return regionSet.isVirtual();
            }

            @Override
            public <V> Optional<V> queryValue(OfflinePlayer subject, IWrappedFlag<V> flag) {
                LocalPlayer subjectHandle = wrapPlayer(subject).orElse(null);
                AbstractWrappedFlag<V> wrappedFlag = (AbstractWrappedFlag<V>) flag;
                return Optional.ofNullable(regionSet.queryValue(subjectHandle, wrappedFlag.getHandle()))
                        .flatMap(wrappedFlag::fromWGValue);
            }

            @Override
            public <V> Collection<V> queryAllValues(OfflinePlayer subject, IWrappedFlag<V> flag) {
                LocalPlayer subjectHandle = wrapPlayer(subject).orElse(null);
                AbstractWrappedFlag<V> wrappedFlag = (AbstractWrappedFlag<V>) flag;
                return regionSet.queryAllValues(subjectHandle, wrappedFlag.getHandle()).stream()
                        .map(wrappedFlag::fromWGValue)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());
            }

            @Override
            public boolean isOwnerOfAll(OfflinePlayer player) {
                LocalPlayer playerHandle = wrapPlayer(player).orElse(null);
                return regionSet.isOwnerOfAll(playerHandle);
            }

            @Override
            public boolean isMemberOfAll(OfflinePlayer player) {
                LocalPlayer playerHandle = wrapPlayer(player).orElse(null);
                return regionSet.isMemberOfAll(playerHandle);
            }

            @Override
            public int size() {
                return regionSet.size();
            }

            @Override
            public Set<IWrappedRegion> getRegions() {
                return regionSet.getRegions().stream()
                        .map(region -> new WrappedRegion(world, region)).collect(Collectors.toSet());
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
    public void registerHandler(Supplier<IHandler> factory) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setUseCache(false);
        proxyFactory.setSuperclass(ProxyHandler.class);

        Class<? extends ProxyHandler> handlerClass;
        Constructor<? extends ProxyHandler> handlerConstructor;
        try {
            //noinspection unchecked
            handlerClass = (Class<? extends ProxyHandler>) proxyFactory.createClass();
            handlerConstructor = handlerClass.getDeclaredConstructor(WorldGuardImplementation.class, IHandler.class, Session.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        worldGuardPlugin.getSessionManager().registerHandler(new Handler.Factory<Handler>() {
            @Override
            public Handler create(Session session) {
                IHandler handler = factory.get();
                try {
                    return handlerConstructor.newInstance(WorldGuardImplementation.this, handler, session);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }, null);
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
                    } catch (IllegalArgumentException e) {
                        // Unsupported flag type
                    }
                }
            }
        }

        return flags;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
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
        RegionManager regionManager = worldGuardPlugin.getRegionManager(world);
        Map<String, ProtectedRegion> regions = regionManager.getRegions();

        Map<String, IWrappedRegion> map = new HashMap<>();
        regions.forEach((name, region) -> map.put(name, new WrappedRegion(world, region)));

        return map;
    }

    @Override
    public Set<IWrappedRegion> getRegions(Location location) {
        ApplicableRegionSet regionSet = getApplicableRegions(location).orElse(null);
        Set<IWrappedRegion> set = new HashSet<>();

        if (regionSet == null) {
            return set;
        }

        regionSet.forEach(region -> set.add(new WrappedRegion(location.getWorld(), region)));
        return set;
    }

    @Override
    public Set<IWrappedRegion> getRegions(Location minimum, Location maximum) {
        ApplicableRegionSet regionSet = getApplicableRegions(minimum, maximum).orElse(null);
        Set<IWrappedRegion> set = new HashSet<>();

        if (regionSet == null) {
            return set;
        }

        regionSet.forEach(region -> set.add(new WrappedRegion(minimum.getWorld(), region)));
        return set;
    }

    @Override
    public Optional<IWrappedRegionSet> getRegionSet(@NonNull Location location) {
        return getApplicableRegions(location).map(regionSet -> wrapRegionSet(location.getWorld(), regionSet));
    }

    @Override
    public Optional<IWrappedRegion> addRegion(String id, List<Location> points, int minY, int maxY) {
        ProtectedRegion region;
        World world = points.get(0).getWorld();
        if (points.size() == 2) {
            region = new ProtectedCuboidRegion(id, WorldGuardVectorUtilities.toBlockVector(points.get(0)), WorldGuardVectorUtilities.toBlockVector(points.get(1)));
        } else {
            region = new ProtectedPolygonalRegion(id, WorldGuardVectorUtilities.toBlockVector2DList(points), minY, maxY);
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
        return set.map(protectedRegions -> protectedRegions.stream().map(region -> new WrappedRegion(world, region))
                .collect(Collectors.toSet()));
    }

    @Override
    public Optional<ISelection> getPlayerSelection(@NonNull Player player) {
        return Optional.ofNullable(worldEditPlugin.getSelection(player))
                .map(selection -> {
                    if (selection instanceof CuboidSelection) {
                        return new ICuboidSelection() {
                            @Override
                            public Location getMinimumPoint() {
                                return selection.getMinimumPoint();
                            }

                            @Override
                            public Location getMaximumPoint() {
                                return selection.getMaximumPoint();
                            }
                        };
                    } else if (selection instanceof Polygonal2DSelection) {
                        return new IPolygonalSelection() {
                            @Override
                            public Set<Location> getPoints() {
                                return ((Polygonal2DSelection) selection).getNativePoints().stream()
                                        .map(vector -> new BlockVector(vector.toVector()))
                                        .map(vector -> WorldGuardVectorUtilities.fromBlockVector(selection.getWorld(), vector))
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
