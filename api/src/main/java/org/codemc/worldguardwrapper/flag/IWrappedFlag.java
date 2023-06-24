package org.codemc.worldguardwrapper.flag;

import java.util.Optional;

@SuppressWarnings("unused")
public interface IWrappedFlag<T> {

    String getName();

    Optional<T> getDefaultValue();

}
