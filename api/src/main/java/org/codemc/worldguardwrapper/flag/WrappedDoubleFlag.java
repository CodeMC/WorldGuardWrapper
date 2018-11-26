package org.codemc.worldguardwrapper.flag;

/**
 * A flag that stores a double.
 */
public class WrappedDoubleFlag extends AbstractWrappedFlag<Double> {

    public WrappedDoubleFlag(String name) {
        super(name, double.class, null);
    }
}
