package org.codemc.worldguardwrapper.region;

import java.util.Set;
import java.util.UUID;

public interface IWrappedDomain {

    Set<UUID> getPlayers();

    void addPlayer(UUID uuid);

    void removePlayer(UUID uuid);

    Set<String> getGroups();

    void addGroup(String name);

    void removeGroup(String name);

}
