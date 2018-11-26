package org.codemc.worldguardwrapper.flag;

/**
 * A flag that stores an integer.
 */
public class WrappedIntegerFlag extends AbstractWrappedFlag<Integer> {

    public WrappedIntegerFlag(String name) {
        this(name, 0);
    }

    public WrappedIntegerFlag(String name, int defaultValue) {
        super(name, int.class, defaultValue);
    }
}
