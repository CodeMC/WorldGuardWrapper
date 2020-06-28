package org.codemc.worldguardwrapper.flag;

import java.util.Optional;

public interface IWrappedFlag<T> {

    String getName();

    Optional<T> getDefaultValue();

    IWrappedRegionGroupFlag getRegionGroupFlag();

}
