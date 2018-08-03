package org.codemc.worldguardwrapper.implementation;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.NonNull;

public interface IWorldGuardImplementation {

    /**
     * Get an instance of the WorldGuardPlugin class
     * 
     * @return The WorldGuard plugin
     */
    JavaPlugin getWorldGuardPlugin();

    /**
     * Get the API version of the loaded WorldGuard plugin (e.g. 6 or 7).
     * 
     * @return The API version
     */
    int getApiVersion();

    /**
     * Query a StateFlag's value for a given player at a given location.
     * 
     * @param player   The player
     * @param location The location
     * @param flag     The flag's name 
     * @return The flag's value
     */
    Optional<Boolean> queryStateFlag(Player player, @NonNull Location location, @NonNull String flagName);

    /**
     * Register a {@code StateFlag}.
     * 
     * @param flagName     The name of the flag
     * @param defaultValue The flag's default value
     * @return Whether the flag has been registered
     */
    boolean registerStateFlag(@NonNull String flagName, @NonNull Boolean defaultValue);

    /**
     * Query a flag's value for a given player at a given location.
     * 
     * @param player   The player
     * @param location The location
     * @param flagName The name of the flag
     * @param type     The type of the flag's value
     * @return The flag's value
     */
    <T> Optional<T> queryFlag(Player player, @NonNull Location location, @NonNull String flagName, Class<T> type);

    /**
     * Query a flag's value for a given player at a given location.
     * 
     * @param player   The player
     * @param location The location
     * @param flag     The flag
     * @return The flag's value
     */
    default <T> Optional<T> queryFlag(Player player, @NonNull Location location, @NonNull AbstractFlag<T> flag) {
        return queryFlag(player, location, flag.getName(), flag.getType());
    }

    /**
     * Register a flag to WorldGuard's flag registry.
     * 
     * @param flag The flag to register
     * @return Whether the flag has been registered
     */
    <T> boolean registerFlag(@NonNull AbstractFlag<T> flag);
}
