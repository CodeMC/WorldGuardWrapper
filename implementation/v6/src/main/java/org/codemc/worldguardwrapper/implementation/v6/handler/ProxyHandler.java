package org.codemc.worldguardwrapper.implementation.v6.handler;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.flag.WrappedState;
import org.codemc.worldguardwrapper.handler.IHandler;
import org.codemc.worldguardwrapper.implementation.v6.WorldGuardImplementation;
import org.codemc.worldguardwrapper.implementation.v6.region.WrappedRegion;
import org.codemc.worldguardwrapper.region.IWrappedRegion;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;

public class ProxyHandler extends Handler {

    private final WorldGuardImplementation implementation;
    private final IHandler handler;

    public ProxyHandler(WorldGuardImplementation implementation, IHandler handler, Session session) {
        super(session);
        this.implementation = implementation;
        this.handler = handler;
    }

    @Override
    public void initialize(Player player, Location current, ApplicableRegionSet set) {
        handler.initialize(
                player,
                current,
                implementation.wrapRegionSet(Objects.requireNonNull(current.getWorld()), set)
        );
    }

    @Override
    public boolean testMoveTo(Player player, Location from, Location to, ApplicableRegionSet toSet, MoveType moveType) {
        return handler.testMoveTo(player,
                from,
                to,
                implementation.wrapRegionSet(Objects.requireNonNull(to.getWorld()), toSet),
                moveType.name()
        );
    }

    @Override
    public boolean onCrossBoundary(
            Player player,
            Location from,
            Location to,
            ApplicableRegionSet toSet,
            Set<ProtectedRegion> entered,
            Set<ProtectedRegion> exited,
            MoveType moveType
    ) {
        Set<IWrappedRegion> mappedEntered = ImmutableSet.copyOf(
                Collections2.transform(entered, region -> new WrappedRegion(to.getWorld(), region))
        );
        Set<IWrappedRegion> mappedExited = ImmutableSet.copyOf(
                Collections2.transform(exited, region -> new WrappedRegion(from.getWorld(), region))
        );
        return handler.onCrossBoundary(
                player,
                from,
                to,
                implementation.wrapRegionSet(Objects.requireNonNull(to.getWorld()), toSet),
                mappedEntered,
                mappedExited,
                moveType.name()
        );
    }

    @Override
    public void tick(Player player, ApplicableRegionSet set) {
        handler.tick(player, implementation.wrapRegionSet(player.getWorld(), set));
    }

    @Nullable
    @Override
    public StateFlag.State getInvincibility(Player player) {
        WrappedState state = handler.getInvincibility(player);
        if (state == null) {
            return null;
        }
        return state == WrappedState.ALLOW ? StateFlag.State.ALLOW : StateFlag.State.DENY;
    }
}
