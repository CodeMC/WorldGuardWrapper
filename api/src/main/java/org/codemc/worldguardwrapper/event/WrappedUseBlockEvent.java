package org.codemc.worldguardwrapper.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class WrappedUseBlockEvent extends AbstractWrappedEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Event originalEvent;
    private final Player player;
    private final World world;
    private final List<Block> blocks;
    private final Material effectiveMaterial;

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
