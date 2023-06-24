package org.codemc.worldguardwrapper.selection;

import org.bukkit.Location;

@SuppressWarnings("unused")
public interface ICuboidSelection extends ISelection {

    Location getMinimumPoint();

    Location getMaximumPoint();

}
