package org.codemc.worldguardwrapper.implementation.v7;

import com.google.common.collect.Iterators;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.EnumFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.LocationFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.VectorFlag;
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
import org.codemc.worldguardwrapper.implementation.v7.flag.AbstractWrappedFlag;
import org.codemc.worldguardwrapper.implementation.v7.handler.ProxyHandler;
import org.codemc.worldguardwrapper.implementation.v7.region.WrappedRegion;
import org.codemc.worldguardwrapper.implementation.v7.utility.WorldGuardFlagUtilities;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import org.codemc.worldguardwrapper.region.IWrappedRegionSet;
import org.codemc.worldguardwrapper.selection.ICuboidSelection;
import org.codemc.worldguardwrapper.selection.IPolygonalSelection;
import org.codemc.worldguardwrapper.selection.ISelection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class WorldGuardImplementation implements IWorldGuardImplementation {

    private final WorldGuard core;
    private final FlagRegistry flagRegistry;

    public WorldGuardImplementation() {
        core = WorldGuard.getInstance();
        flagRegistry = core.getFlagRegistry();
    }

    private Optional<LocalPlayer> wrapPlayer(OfflinePlayer player) {
        return Optional.ofNullable(player).map(bukkitPlayer -> bukkitPlayer.isOnline() ?
                WorldGuardPlugin.inst().wrapPlayer((Player) bukkitPlayer) : WorldGuardPlugin.inst().wrapOfflinePlayer(bukkitPlayer));
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
        return 7;
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

        core.getPlatform().getSessionManager().registerHandler(new Handler.Factory<Handler>() {
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
    public Optional<IWrappedRegionSet> getRegionSet(@NonNull Location location) {
        return Optional.empty();
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
            region = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player)).getSelection(BukkitAdapter.adapt(player.getWorld()));
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
