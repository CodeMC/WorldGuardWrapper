package org.codemc.worldguardwrapper.flag;

/**
 * A flag that stores a boolean.
 */
public class WrappedBooleanFlag extends AbstractWrappedFlag<Boolean> {

    public WrappedBooleanFlag(String name) {
        super(name, boolean.class, null);
    }
}
