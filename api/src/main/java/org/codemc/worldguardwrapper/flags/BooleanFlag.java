package org.codemc.worldguardwrapper.flags;

import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.implementation.AbstractFlag;

/**
 * A flag that stores a boolean.
 */
public class BooleanFlag extends AbstractFlag<Boolean> {

    public BooleanFlag(String name) {
        this(name, false);
    }

    public BooleanFlag(String name, boolean defaultValue) {
        super(name, boolean.class, defaultValue);
    }

    @Override
    public Object serialize(Boolean value) {
        return value;
    }

    @Override
    public Boolean deserialize(Object serialized) {
        return (Boolean) serialized;
    }

    @Override
    public Boolean parse(Player player, String userInput) {        
        if (userInput.equalsIgnoreCase("true") || userInput.equalsIgnoreCase("yes")
                || userInput.equalsIgnoreCase("on")
                || userInput.equalsIgnoreCase("1")) {
            return true;
        } else if (userInput.equalsIgnoreCase("false") || userInput.equalsIgnoreCase("no")
                || userInput.equalsIgnoreCase("off")
                || userInput.equalsIgnoreCase("0")) {
            return false;
        } else {
            return null;
        }
    }

}