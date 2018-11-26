package org.codemc.worldguardwrapper.flag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public abstract class AbstractWrappedFlag<T> {
    @NonNull
    private String name;
    @NonNull
    private Class<T> type;
    private T defaultValue;
}
