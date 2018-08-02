package org.codemc.worldguardwrapper.flags;

import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.implementation.AbstractFlag;

/**
 * A flag that stores an integer.
 */
public class IntegerFlag extends AbstractFlag<Integer> {

	public IntegerFlag(String name) {
		this(name, 0);
	}

    public IntegerFlag(String name, int defaultValue) {
        super(name, int.class, defaultValue);
    }

	@Override
	public Object serialize(Integer value) {
		return value;
	}

	@Override
	public Integer deserialize(Object serialized) {
		if (serialized instanceof Number) {
            return ((Number) serialized).intValue();
        } else {
            return null;
        }
	}

	@Override
	public Integer parse(Player player, String userInput) {
		return Integer.parseInt(userInput);
	}

}