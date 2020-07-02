package org.codemc.worldguardwrapper.implementation.v7.region;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.implementation.v7.flag.AbstractWrappedFlag;
import org.codemc.worldguardwrapper.implementation.v7.utility.WorldGuardFlagUtilities;
import org.codemc.worldguardwrapper.region.IWrappedDomain;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import org.codemc.worldguardwrapper.selection.ICuboidSelection;
import org.codemc.worldguardwrapper.selection.IPolygonalSelection;
import org.codemc.worldguardwrapper.selection.ISelection;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class WrappedRegion implements IWrappedRegion {

    private final World world;
    private final ProtectedRegion handle;

    @Override
    public ISelection getSelection() {
        if (handle instanceof ProtectedCuboidRegion) {
            return new ICuboidSelection() {
                @Override
                public Location getMinimumPoint() {
                    return BukkitAdapter.adapt(world, handle.getMinimumPoint());
                }

                @Override
                public Location getMaximumPoint() {
                    return BukkitAdapter.adapt(world, handle.getMaximumPoint());
                }
            };
        } else if (handle instanceof ProtectedPolygonalRegion) {
            return new IPolygonalSelection() {
                @Override
                public Set<Location> getPoints() {
                    return handle.getPoints().stream()
                            .map(BlockVector2::toBlockVector3)
                            .map(vector -> BukkitAdapter.adapt(world, vector))
                            .collect(Collectors.toSet());
                }

                @Override
                public int getMinimumY() {
                    return handle.getMinimumPoint().getBlockY();
                }

                @Override
                public int getMaximumY() {
                    return handle.getMaximumPoint().getBlockY();
                }
            };
        } else {
            throw new UnsupportedOperationException("Unsupported " + handle.getClass().getSimpleName() + " region!");
        }
    }

    @Override
    public String getId() {
        return handle.getId();
    }

    @Override
    public Map<IWrappedFlag<?>, Object> getFlags() {
        Map<IWrappedFlag<?>, Object> result = new HashMap<>();
        handle.getFlags().forEach((flag, value) -> {
            if (value != null) {
                try {
                    Map.Entry<IWrappedFlag<?>, Object> wrapped = WorldGuardFlagUtilities.wrap(flag, value);
                    result.put(wrapped.getKey(), wrapped.getValue());
                } catch (IllegalArgumentException ignored) {/* Unsupported flag type */}
            }
        });
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> getFlag(IWrappedFlag<T> flag) {
        AbstractWrappedFlag<T> wrappedFlag = (AbstractWrappedFlag<T>) flag;
        return Optional.ofNullable(handle.getFlag(wrappedFlag.getHandle()))
                .map(value -> (T) wrappedFlag.fromWGValue(value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void setFlag(IWrappedFlag<T> flag, T value) {
        AbstractWrappedFlag<T> wrappedFlag = (AbstractWrappedFlag<T>) flag;
        handle.setFlag((Flag<Object>) wrappedFlag.getHandle(), wrappedFlag.fromWrapperValue(value).orElse(null));
    }

    @Override
    public int getPriority() {
        return handle.getPriority();
    }

    @Override
    public void setPriority(int priority) {
        handle.setPriority(priority);
    }

    @Override
    public IWrappedDomain getOwners() {
        return new IWrappedDomain() {
            @Override
            public Set<UUID> getPlayers() {
                return handle.getOwners().getUniqueIds();
            }

            @Override
            public void addPlayer(UUID uuid) {
                handle.getOwners().addPlayer(uuid);
            }

            @Override
            public void removePlayer(UUID uuid) {
                handle.getOwners().removePlayer(uuid);
            }

            @Override
            public Set<String> getGroups() {
                return handle.getOwners().getGroups();
            }

            @Override
            public void addGroup(String name) {
                handle.getOwners().addGroup(name);
            }

            @Override
            public void removeGroup(String name) {
                handle.getOwners().removeGroup(name);
            }
        };
    }

    @Override
    public IWrappedDomain getMembers() {
        return new IWrappedDomain() {
            @Override
            public Set<UUID> getPlayers() {
                return handle.getMembers().getUniqueIds();
            }

            @Override
            public void addPlayer(UUID uuid) {
                handle.getMembers().addPlayer(uuid);
            }

            @Override
            public void removePlayer(UUID uuid) {
                handle.getMembers().removePlayer(uuid);
            }

            @Override
            public Set<String> getGroups() {
                return handle.getMembers().getGroups();
            }

            @Override
            public void addGroup(String name) {
                handle.getMembers().addGroup(name);
            }

            @Override
            public void removeGroup(String name) {
                handle.getMembers().removeGroup(name);
            }
        };
    }

    @Override
    public boolean contains(Location location) {
        return handle.contains(BukkitAdapter.asBlockVector(location));
    }

}
