package org.codemc.worldguardwrapper.flags;

import org.bukkit.entity.Player;

/**
 * A flag that stores a double.
 */
public class DoubleFlag extends AbstractWrappedFlag<Double> {

    public DoubleFlag(String name) {
        this(name, 0d);
    }

    public DoubleFlag(String name, double defaultValue) {
        super(name, double.class, defaultValue);
    }

    @Override
    public Object serialize(Double value) {
        return value;
    }

    @Override
    public Double deserialize(Object serialized) {
        if (serialized instanceof Number) {
            return ((Number) serialized).doubleValue();
        } else {
            return null;
        }
    }

    @Override
    public Double parse(Player player, String userInput) {
        return Double.parseDouble(userInput);
    }

}
