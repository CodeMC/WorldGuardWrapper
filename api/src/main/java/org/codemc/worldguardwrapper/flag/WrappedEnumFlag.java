package org.codemc.worldguardwrapper.flag;

/**
 * A flag that stores an enum value.
 */
public class WrappedEnumFlag<T extends Enum<T>> extends AbstractWrappedFlag<T> {

    public WrappedEnumFlag(String name, Class<T> enumClass) {
        this(name, enumClass, null);
    }

    public WrappedEnumFlag(String name, Class<T> enumClass, T defaultValue) {
        super(name, enumClass, defaultValue);
    }
}
