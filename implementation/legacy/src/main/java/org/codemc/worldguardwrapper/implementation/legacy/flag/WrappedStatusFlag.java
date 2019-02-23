package org.codemc.worldguardwrapper.implementation.legacy.flag;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.codemc.worldguardwrapper.flag.IWrappedStatusFlag;
import org.codemc.worldguardwrapper.flag.WrappedState;

import java.util.Optional;

public class WrappedStatusFlag extends AbstractWrappedFlag<WrappedState> implements IWrappedStatusFlag {

    public WrappedStatusFlag(Flag<StateFlag.State> handle) {
        super(handle);
    }

    @Override
    public Optional<WrappedState> fromWGValue(Object value) {
        return Optional.ofNullable(value)
                .map(state -> state == StateFlag.State.ALLOW ? WrappedState.ALLOW : WrappedState.DENY);
    }

    @Override
    public Optional<Object> fromWrapperValue(WrappedState value) {
        return Optional.ofNullable(value)
                .map(state -> state == WrappedState.ALLOW ? StateFlag.State.ALLOW : StateFlag.State.DENY);
    }

}
