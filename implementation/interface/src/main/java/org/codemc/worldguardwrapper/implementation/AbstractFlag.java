package org.codemc.worldguardwrapper.implementation;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
public abstract class AbstractFlag<T> {
    private @NonNull String name;
    private @NonNull Class<T> type;
    private T defaultValue;

    /**
     * Get the name of this flag.
     * 
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the type of this flag's value.
     * 
     * @return The type
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Get the default value of this flag.
     * 
     * @return The default value (may be {@code null})
     */
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Convert the value stored in this flag into a type that can be
     * serialized into YAML.
     * 
     * @param value The value
     * @return The serialized type
     */
    public abstract Object serialize(T value);

    /**
     * Convert a raw object that was loaded (from a YAML file, for example) into the
     * type that this flag uses.
     * 
     * @param serialized The raw object
     * @return The deserialized type
     */
    public abstract T deserialize(Object serialized);

    /**
     * Parse a given input to force it to a type compatible with the flag.
     * 
     * @param player Player who entered the string.
     * @param userInput Input string (e.g. a player input)
     * @return A type compatible with the flag
     */
    public abstract T parse(Player player, String userInput);

}