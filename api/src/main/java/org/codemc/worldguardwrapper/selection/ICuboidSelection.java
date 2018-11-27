package org.codemc.worldguardwrapper.selection;

import org.bukkit.Location;

public interface ICuboidSelection extends ISelection {

    Location getMinimumPoint();

    Location getMaximumPoint();

}
