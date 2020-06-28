package org.codemc.worldguardwrapper.implementation.v6.flag;

import com.sk89q.worldguard.protection.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.flag.WrappedRegionGroup;
import org.codemc.worldguardwrapper.implementation.v6.utility.WorldGuardFlagUtilities;

import java.util.Optional;

@AllArgsConstructor
@Getter
public abstract class AbstractWrappedFlag<T> implements IWrappedFlag<T> {

    private final Flag<?> handle;

    @Override
    public String getName() {
        return handle.getName();
    }

    public abstract Optional<T> fromWGValue(Object value);

    public abstract Optional<Object> fromWrapperValue(T value);

    @Override
    public Optional<T> getDefaultValue() {
        return fromWGValue(handle.getDefault());
    }

    @Override
    public IWrappedFlag<WrappedRegionGroup> getRegionGroupFlag() {
        return WorldGuardFlagUtilities.wrap(handle.getRegionGroupFlag(), WrappedRegionGroup.class);
    }
}
