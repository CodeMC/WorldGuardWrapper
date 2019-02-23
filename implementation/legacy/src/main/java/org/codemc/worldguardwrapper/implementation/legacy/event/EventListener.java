package org.codemc.worldguardwrapper.implementation.legacy.event;

import com.sk89q.worldguard.bukkit.event.block.UseBlockEvent;
import com.sk89q.worldguard.bukkit.event.entity.DamageEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.UseEntityEvent;
import com.sk89q.worldguard.protection.events.DisallowedPVPEvent;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.codemc.worldguardwrapper.event.*;

@NoArgsConstructor
public class EventListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onUseBlock(UseBlockEvent worldGuardEvent) {
        Player player = worldGuardEvent.getCause().getFirstPlayer();
        if (player == null) {
            // Only forward player events for now
            return;
        }

        AbstractWrappedEvent event = new WrappedUseBlockEvent(
                worldGuardEvent.getOriginalEvent(),
                player, worldGuardEvent.getWorld(),
                worldGuardEvent.getBlocks(),
                worldGuardEvent.getEffectiveMaterial());
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.getResult() != Result.DEFAULT) {
            // DEFAULT = Result probably has not been touched by the handler,
            //           so don't touch the original result either.
            worldGuardEvent.setResult(event.getResult());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onUseEntity(UseEntityEvent worldGuardEvent) {
        Player player = worldGuardEvent.getCause().getFirstPlayer();
        if (player == null) {
            // Only forward player events for now
            return;
        }

        AbstractWrappedEvent event = new WrappedUseEntityEvent(
                worldGuardEvent.getOriginalEvent(),
                player,
                worldGuardEvent.getTarget(),
                worldGuardEvent.getEntity());
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.getResult() != Result.DEFAULT) {
            // DEFAULT = Result probably has not been touched by the handler,
            //           so don't touch the original result either.
            worldGuardEvent.setResult(event.getResult());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDamageEntity(DamageEntityEvent worldGuardEvent) {
        Player player = worldGuardEvent.getCause().getFirstPlayer();
        if (player == null) {
            // Only forward player events for now
            return;
        }

        AbstractWrappedEvent event = new WrappedDamageEntityEvent(
                worldGuardEvent.getOriginalEvent(),
                player,
                worldGuardEvent.getTarget(),
                worldGuardEvent.getEntity());
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.getResult() != Result.DEFAULT) {
            // DEFAULT = Result probably has not been touched by the handler,
            //           so don't touch the original result either.
            worldGuardEvent.setResult(event.getResult());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDisallowedPVP(DisallowedPVPEvent worldGuardEvent) {
        AbstractWrappedEvent event = new WrappedDisallowedPVPEvent(
                worldGuardEvent.getAttacker(),
                worldGuardEvent.getDefender(),
                worldGuardEvent.getCause());
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.getResult() != Result.DEFAULT) {
            // DEFAULT = Result probably has not been touched by the handler,
            //           so don't touch the original result either.
            worldGuardEvent.setCancelled(event.getResult() == Result.DENY);
        }
    }

}
