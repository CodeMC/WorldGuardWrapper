package org.codemc.worldguardwrapper.implementation.v7.handler;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
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
import org.codemc.worldguardwrapper.implementation.v7.WorldGuardImplementation;
import org.codemc.worldguardwrapper.implementation.v7.region.WrappedRegion;
import org.codemc.worldguardwrapper.region.IWrappedRegion;

import javax.annotation.Nullable;
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
    public void initialize(LocalPlayer player, com.sk89q.worldedit.util.Location current, ApplicableRegionSet set) {
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        Location bukkitLocation = BukkitAdapter.adapt(current);
        handler.initialize(bukkitPlayer, bukkitLocation, implementation.wrapRegionSet(bukkitLocation.getWorld(), set));
    }

    @Override
    public boolean testMoveTo(LocalPlayer player, com.sk89q.worldedit.util.Location from, com.sk89q.worldedit.util.Location to, ApplicableRegionSet toSet, MoveType moveType) {
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        Location bukkitFrom = BukkitAdapter.adapt(from);
        Location bukkitTo = BukkitAdapter.adapt(to);
        return handler.testMoveTo(bukkitPlayer, bukkitFrom, bukkitTo, implementation.wrapRegionSet(bukkitTo.getWorld(), toSet), moveType.name());
    }

    @Override
    public boolean onCrossBoundary(LocalPlayer player, com.sk89q.worldedit.util.Location from, com.sk89q.worldedit.util.Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        Location bukkitFrom = BukkitAdapter.adapt(from);
        Location bukkitTo = BukkitAdapter.adapt(to);
        Set<IWrappedRegion> mappedEntered = ImmutableSet.copyOf(Collections2.transform(entered, region -> new WrappedRegion(bukkitTo.getWorld(), region)));
        Set<IWrappedRegion> mappedExited = ImmutableSet.copyOf(Collections2.transform(exited, region -> new WrappedRegion(bukkitFrom.getWorld(), region)));
        return handler.onCrossBoundary(bukkitPlayer, bukkitFrom, bukkitTo, implementation.wrapRegionSet(bukkitTo.getWorld(), toSet), mappedEntered, mappedExited, moveType.name());
    }

    @Override
    public void tick(LocalPlayer player, ApplicableRegionSet set) {
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        handler.tick(bukkitPlayer, implementation.wrapRegionSet(bukkitPlayer.getWorld(), set));
    }

    @Nullable
    @Override
    public StateFlag.State getInvincibility(LocalPlayer player) {
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        WrappedState state = handler.getInvincibility(bukkitPlayer);
        if (state == null) {
            return null;
        }
        return state == WrappedState.ALLOW ? StateFlag.State.ALLOW : StateFlag.State.DENY;
    }
}
