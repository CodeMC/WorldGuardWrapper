package org.codemc.worldguardwrapper.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter
public class WrappedDisallowedPVPEvent extends AbstractWrappedEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Player attacker;
    private final Player defender;
    private final Event cause;

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
