package org.codemc.worldguardwrapper.implementation;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.NonNull;

public interface IWorldGuardImplementation {

    JavaPlugin getWorldGuardPlugin();

    int getApiVersion();
    
    Optional<Boolean> queryStateFlag(Player player, @NonNull Location location, @NonNull String flagId);

    boolean registerStateFlag(@NonNull String flagId, @NonNull Boolean defaultValue);

    <T> Optional<T> queryFlag(Player player, @NonNull Location location, @NonNull AbstractFlag<T> flag);

    <T> boolean registerFlag(@NonNull AbstractFlag<T> flag);
}
