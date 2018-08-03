package org.codemc.worldguardwrapper.implementation.v6;

import java.util.List;

import com.sk89q.worldguard.bukkit.event.block.UseBlockEvent;
import com.sk89q.worldguard.bukkit.event.entity.DamageEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.UseEntityEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.codemc.worldguardwrapper.implementation.AbstractWrappedEvent;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EventListener implements Listener {

    private Class<? extends AbstractWrappedEvent> useBlockEventClass;
    private Class<? extends AbstractWrappedEvent> useEntityEventClass;
    private Class<? extends AbstractWrappedEvent> damageEntityEventClass;

    @EventHandler
    public void onUseBlock(UseBlockEvent e) {
        Player player = e.getCause().getFirstPlayer();
        if (player == null) {
            // Only forward player events for now
            return;
        }

        try {
            AbstractWrappedEvent event = useBlockEventClass
                    .getConstructor(Event.class, Player.class, World.class, List.class, Material.class)
                    .newInstance(e.getOriginalEvent(), player, e.getWorld(), e.getBlocks(), e.getEffectiveMaterial());

            Bukkit.getServer().getPluginManager().callEvent(event);
            e.setResult(event.getResult());
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace(); // TODO: Handle differently
        }
    }

    @EventHandler
    public void onUseEntity(UseEntityEvent e) {
        Player player = e.getCause().getFirstPlayer();
        if (player == null) {
            // Only forward player events for now
            return;
        }

        try {
            AbstractWrappedEvent event = useEntityEventClass
                    .getConstructor(Event.class, Player.class, Location.class, Entity.class)
                    .newInstance(e.getOriginalEvent(), player, e.getTarget(), e.getEntity());

            Bukkit.getServer().getPluginManager().callEvent(event);
            e.setResult(event.getResult());
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace(); // TODO: Handle differently
        }
    }

    @EventHandler
    public void onDamageEntity(DamageEntityEvent e) {
        Player player = e.getCause().getFirstPlayer();
        if (player == null) {
            // Only forward player events for now
            return;
        }

        try {
            AbstractWrappedEvent event = damageEntityEventClass
                    .getConstructor(Event.class, Player.class, Location.class, Entity.class)
                    .newInstance(e.getOriginalEvent(), player, e.getTarget(), e.getEntity());

            Bukkit.getServer().getPluginManager().callEvent(event);
            e.setResult(event.getResult());
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace(); // TODO: Handle differently
        }
    }
}