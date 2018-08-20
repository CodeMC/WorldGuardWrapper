package org.codemc.worldguardwrapper.implementation.v7;

import com.sk89q.worldguard.bukkit.event.block.UseBlockEvent;
import com.sk89q.worldguard.bukkit.event.entity.DamageEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.UseEntityEvent;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.codemc.worldguardwrapper.event.AbstractWrappedEvent;

@NoArgsConstructor
public class EventListener implements Listener {

    @EventHandler
    public void onUseBlock(UseBlockEvent worldGuardEvent) {
        Player player = worldGuardEvent.getCause().getFirstPlayer();
        if (player == null) {
            // Only forward player events for now
            return;
        }

        AbstractWrappedEvent event = new org.codemc.worldguardwrapper.event.UseBlockEvent(
                worldGuardEvent.getOriginalEvent(),
                player, worldGuardEvent.getWorld(),
                worldGuardEvent.getBlocks(),
                worldGuardEvent.getEffectiveMaterial());
        Bukkit.getServer().getPluginManager().callEvent(event);
        worldGuardEvent.setResult(event.getResult());
    }

    @EventHandler
    public void onUseEntity(UseEntityEvent worldGuardEvent) {
        Player player = worldGuardEvent.getCause().getFirstPlayer();
        if (player == null) {
            // Only forward player events for now
            return;
        }

        AbstractWrappedEvent event = new org.codemc.worldguardwrapper.event.UseEntityEvent(
                worldGuardEvent.getOriginalEvent(),
                player,
                worldGuardEvent.getTarget(),
                worldGuardEvent.getEntity());
        Bukkit.getServer().getPluginManager().callEvent(event);
        worldGuardEvent.setResult(event.getResult());
    }

    @EventHandler
    public void onDamageEntity(DamageEntityEvent worldGuardEvent) {
        Player player = worldGuardEvent.getCause().getFirstPlayer();
        if (player == null) {
            // Only forward player events for now
            return;
        }

        AbstractWrappedEvent event = new org.codemc.worldguardwrapper.event.DamageEntityEvent(
                worldGuardEvent.getOriginalEvent(),
                player,
                worldGuardEvent.getTarget(),
                worldGuardEvent.getEntity());
        Bukkit.getServer().getPluginManager().callEvent(event);
        worldGuardEvent.setResult(event.getResult());
    }

}
