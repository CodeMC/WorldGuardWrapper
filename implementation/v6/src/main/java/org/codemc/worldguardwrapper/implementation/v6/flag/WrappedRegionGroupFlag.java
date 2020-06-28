package org.codemc.worldguardwrapper.implementation.v6.flag;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import org.codemc.worldguardwrapper.flag.IWrappedRegionGroupFlag;
import org.codemc.worldguardwrapper.flag.WrappedRegionGroup;

import java.util.Optional;

public class WrappedRegionGroupFlag extends AbstractWrappedFlag<WrappedRegionGroup> implements IWrappedRegionGroupFlag {

    public WrappedRegionGroupFlag(Flag<RegionGroup> handle) {
        super(handle);
    }

    @Override
    public Optional<WrappedRegionGroup> fromWGValue(Object value) {
        return Optional.of(Enum.valueOf(WrappedRegionGroup.class, value.toString()));
    }

    @Override
    public Optional<Object> fromWrapperValue(WrappedRegionGroup value) {
        return Optional.of(Enum.valueOf(RegionGroup.class, value.toString()));
    }
}
