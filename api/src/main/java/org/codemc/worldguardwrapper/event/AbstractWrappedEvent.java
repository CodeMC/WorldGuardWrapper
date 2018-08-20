package org.codemc.worldguardwrapper.event;

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

    /**
     * Sets the event result.
     *
     * @param result the new event result
     */
    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * Returns the current event result.
     *
     * @return the event result
     */
    public Result getResult() {
        return result;
    }

}
