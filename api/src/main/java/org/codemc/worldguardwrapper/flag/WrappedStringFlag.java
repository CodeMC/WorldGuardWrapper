package org.codemc.worldguardwrapper.flag;

/**
 * A flag that stores a string.
 */
public class WrappedStringFlag extends AbstractWrappedFlag<String> {

    public WrappedStringFlag(String name) {
        this(name, "");
    }

    public WrappedStringFlag(String name, String defaultValue) {
        super(name, String.class, defaultValue);
    }
}
