package org.codemc.worldguardwrapper.flags;

import org.bukkit.entity.Player;

/**
 * A flag that stores an enum value.
 */
public class EnumFlag<T extends Enum<T>> extends AbstractWrappedFlag<T> {

    public EnumFlag(String name, Class<T> enumClass) {
        this(name, enumClass, null);
    }

    public EnumFlag(String name, Class<T> enumClass, T defaultValue) {
        super(name, enumClass, defaultValue);
    }

    /**
     * Get the enum class.
     * TODO: really needed? we already have getType() -Gab
     *
     * @return The enum class
     */
    public Class<T> getEnumClass() {
        return getType();
    }

    @Override
    public Object serialize(T value) {
        return value.name();
    }

    @Override
    public T deserialize(Object serialized) {
        if (serialized instanceof String) {
            return Enum.valueOf(getEnumClass(), (String) serialized);
        } else {
            return null;
        }
    }

    @Override
    public T parse(Player player, String userInput) {
        return Enum.valueOf(getEnumClass(), userInput);
    }

}
