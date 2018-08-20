package org.codemc.worldguardwrapper.flags;

import org.bukkit.entity.Player;

/**
 * A flag that stores a string.
 */
public class StringFlag extends AbstractWrappedFlag<String> {

    public StringFlag(String name) {
        this(name, "");
    }

    public StringFlag(String name, String defaultValue) {
        super(name, String.class, defaultValue);
    }

    @Override
    public Object serialize(String value) {
        return value;
    }

    @Override
    public String deserialize(Object serialized) {
        return (String) serialized;
    }

    @Override
    public String parse(Player player, String userInput) {
        return userInput;
    }

}
