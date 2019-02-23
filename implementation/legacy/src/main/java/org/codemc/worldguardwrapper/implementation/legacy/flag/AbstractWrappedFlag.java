package org.codemc.worldguardwrapper.implementation.legacy.flag;

import com.sk89q.worldguard.protection.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;

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

}
