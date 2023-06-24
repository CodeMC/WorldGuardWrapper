package org.codemc.worldguardwrapper.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter
public class WrappedUseEntityEvent extends AbstractWrappedEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Event originalEvent;
    private final Player player;
    private final Location target;
    private final Entity entity;

    @Override
    @NonNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
