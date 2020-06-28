package org.codemc.worldguardwrapper.implementation.v6.flag;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.flag.IWrappedRegionGroupFlag;
import org.codemc.worldguardwrapper.flag.IWrappedStatusFlag;
import org.codemc.worldguardwrapper.flag.WrappedState;
import org.codemc.worldguardwrapper.implementation.v6.utility.WorldGuardFlagUtilities;

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

    @Override
    public IWrappedFlag<IWrappedRegionGroupFlag> getRegionGroupFlag() {
        return WorldGuardFlagUtilities.wrap(getHandle().getRegionGroupFlag(), IWrappedRegionGroupFlag.class);
    }
}
