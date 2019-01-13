package org.codemc.worldguardwrapper.implementation.v7fawe.flag;

import com.sk89q.worldguard.protection.flags.Flag;

import java.util.Optional;

public class WrappedPrimitiveFlag<T> extends AbstractWrappedFlag<T> {

    public WrappedPrimitiveFlag(Flag<T> handle) {
        super(handle);
    }

    @Override
    public Optional<T> fromWGValue(Object value) {
        return Optional.ofNullable((T) value);
    }

    @Override
    public Optional<Object> fromWrapperValue(T value) {
        return Optional.ofNullable(value);
    }

}
