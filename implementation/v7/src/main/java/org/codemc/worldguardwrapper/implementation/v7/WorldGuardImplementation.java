package org.codemc.worldguardwrapper.implementation.v7;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
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
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.codemc.worldguardwrapper.flag.AbstractWrappedFlag;
import org.codemc.worldguardwrapper.implementation.IWorldGuardImplementation;
import org.codemc.worldguardwrapper.region.WrappedDomain;
import org.codemc.worldguardwrapper.region.WrappedRegion;
import org.codemc.worldguardwrapper.selection.CuboidSelection;
import org.codemc.worldguardwrapper.selection.PolygonalSelection;
import org.codemc.worldguardwrapper.selection.Selection;

import java.util.*;
import java.util.stream.Collectors;

public class WorldGuardImplementation implements IWorldGuardImplementation {

    private final WorldGuard core;
    private final FlagRegistry flagRegistry;
    private final WorldGuardPlugin plugin;

    public WorldGuardImplementation() {
        core = WorldGuard.getInstance();
        flagRegistry = core.getFlagRegistry();
        plugin = WorldGuardPlugin.inst();
    }

    private Optional<LocalPlayer> wrapPlayer(Player player) {
        return Optional.ofNullable(player).map(bukkitPlayer -> plugin.wrapPlayer(player));
    }

    private Optional<Player> getPlayer(LocalPlayer player) {
        return Optional.ofNullable(Bukkit.getPlayer(player.getUniqueId()));
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

    private Optional<StateFlag.State> queryState(Player player, @NonNull Location location, @NonNull StateFlag... stateFlags) {
        return getApplicableRegions(location).map(applicableRegions -> applicableRegions.queryState(wrapPlayer(player)
                .orElse(null), stateFlags));
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
                                    .map(BlockVector2::toBlockVector3)
                                    .map(vector -> BukkitAdapter.adapt(world, vector))
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
                        return BukkitAdapter.adapt(world, region.getMinimumPoint());
                    }

                    @Override
                    public Location getMaximumPoint() {
                        return BukkitAdapter.adapt(world, region.getMaximumPoint());
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
                return region.contains(BukkitAdapter.asBlockVector(location));
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
                return flag.parse(getPlayer(context.getPlayerSender()).orElse(null), context.getUserInput());
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
        return getWorldManager(world)
                .map(regionManager -> regionManager.getRegion(id))
                .map(region -> toRegion(world, region));
    }

    @Override
    public Map<String, WrappedRegion> getRegions(World world) {
        RegionManager regionManager = core.getPlatform().getRegionContainer().get(new BukkitWorld(world));
        if (regionManager == null) {
            return Collections.emptyMap();
        }

        Map<String, ProtectedRegion> regions = regionManager.getRegions();
        Map<String, WrappedRegion> map = new HashMap<>();
        regions.forEach((name, region) -> map.put(name, toRegion(world, region)));
        return map;
    }

    @Override
    public Set<WrappedRegion> getRegions(Location location) {
        ApplicableRegionSet regionSet = getApplicableRegions(location).orElse(null);
        if (regionSet == null) {
            return Collections.emptySet();
        }

        return regionSet.getRegions().stream()
                .map(region -> toRegion(location.getWorld(), region))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<WrappedRegion> getRegions(Location minimum, Location maximum) {
        ApplicableRegionSet regionSet = getApplicableRegions(minimum, maximum).orElse(null);
        if (regionSet == null) {
            return Collections.emptySet();
        }

        return regionSet.getRegions().stream()
                .map(region -> toRegion(minimum.getWorld(), region))
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<WrappedRegion> addRegion(String id, List<Location> points, int minY, int maxY) {
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
            return Optional.of(toRegion(world, region));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Set<WrappedRegion>> removeRegion(World world, String id) {
        Optional<Set<ProtectedRegion>> set = getWorldManager(world).map(manager -> manager.removeRegion(id));
        return set.map(protectedRegions -> protectedRegions.stream()
                .map(region -> toRegion(world, region)).collect(Collectors.toSet()));
    }
}
