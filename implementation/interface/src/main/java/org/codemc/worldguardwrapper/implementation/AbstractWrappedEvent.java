package org.codemc.worldguardwrapper.implementation;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class AbstractWrappedEvent extends Event implements Cancellable {
    private Result result = Result.DEFAULT;

    @Override
    public boolean isCancelled() {
        return result == Result.DENY;
    }

    @Override
    public void setCancelled(boolean cancel) {
        if (cancel) {
            setResult(Result.DENY);
        }
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return result;
    }
}