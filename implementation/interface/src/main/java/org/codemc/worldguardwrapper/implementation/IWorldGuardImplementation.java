package org.codemc.worldguardwrapper.implementation;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public interface IWorldGuardImplementation {

    JavaPlugin getWorldGuardPlugin();

    int getApiVersion();

    // String flag

    Optional<String> queryStringFlag(Player player, @NonNull Location location, @NonNull String flagId);

    boolean registerStringFlag(@NonNull String flagId, @NonNull String defaultValue);

    // State flag

    Optional<Boolean> queryStateFlag(Player player, @NonNull Location location, @NonNull String flagId);

    boolean registerStateFlag(@NonNull String flagId, @NonNull Boolean defaultValue);
}
