package org.codemc.worldguardwrapper.flags;

import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.implementation.AbstractFlag;

/**
 * A flag that stores an enum value.
 */
public class EnumFlag<T extends Enum<T>> extends AbstractFlag<T> {

    private Class<T> enumClass;

    public EnumFlag(String name, Class<T> enumClass) {
        this(name, enumClass, null);
    }

    public EnumFlag(String name, Class<T> enumClass, T defaultValue) {
        super(name, enumClass, defaultValue);
    }

    /**
     * Get the enum class.
     * 
     * @return The enum class
     */
    public Class<T> getEnumClass() {
        return enumClass;
    }

    @Override
    public Object serialize(T value) {
        return value.name();
    }

    @Override
    public T deserialize(Object serialized) {
        if (serialized instanceof String) {
            return Enum.valueOf(enumClass, (String) serialized);
        } else {
            return null;
        }
    }

    @Override
    public T parse(Player player, String userInput) {
        return Enum.valueOf(enumClass, userInput);
    }
    
}