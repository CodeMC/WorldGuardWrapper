package org.codemc.worldguardwrapper.flag;

import java.util.HashSet;
import java.util.Set;

/**
 * A flag that stores a set of values of the sub flag's type.
 */
public class WrappedSetFlag<T> extends AbstractWrappedFlag<Set<T>> {

    private AbstractWrappedFlag<T> subFlag;

    public WrappedSetFlag(String name, AbstractWrappedFlag<T> subFlag) {
        this(name, new HashSet<>(), subFlag);
    }

    @SuppressWarnings("unchecked")
    public WrappedSetFlag(String name, Set<T> defaultValue, AbstractWrappedFlag<T> subFlag) {
        super(name, (Class<Set<T>>) defaultValue.getClass(), defaultValue);
        this.subFlag = subFlag;
    }

    /**
     * Get the type of values stored in this flag.
     *
     * @return The stored flag type.
     */
    public AbstractWrappedFlag<T> getSubFlag() {
        return subFlag;
    }
}
