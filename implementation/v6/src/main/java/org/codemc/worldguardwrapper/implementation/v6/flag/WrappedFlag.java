package org.codemc.worldguardwrapper.implementation.v6.flag;

import com.sk89q.worldguard.protection.flags.Flag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;

import java.util.Optional;

@AllArgsConstructor
@Getter
public class WrappedFlag<T> implements IWrappedFlag<T> {

    private final Flag<T> handle;

    @Override
    public String getName() {
        return handle.getName();
    }

    @Override
    public Optional<T> getDefaultValue() {
        return Optional.ofNullable(handle.getDefault());
    }

}
