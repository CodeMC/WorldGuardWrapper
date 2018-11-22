package org.codemc.worldguardwrapper.selection;

import org.bukkit.Location;

public interface CuboidSelection extends Selection {

    Location getMinimumPoint();

    Location getMaximumPoint();

}
