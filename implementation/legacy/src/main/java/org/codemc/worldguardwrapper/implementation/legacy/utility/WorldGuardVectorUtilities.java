package org.codemc.worldguardwrapper.implementation.legacy.utility;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class WorldGuardVectorUtilities {

    public BlockVector toBlockVector(Location location) {
        return new BlockVector(location.getX(), location.getY(), location.getZ());
    }

    public Location fromBlockVector(World world, BlockVector vector) {
        return new Location(world, vector.getX(), vector.getY(), vector.getZ());
    }

    public List<BlockVector2D> toBlockVector2DList(List<Location> locations) {
        return locations.stream().map(location -> new BlockVector2D(location.getX(), location.getZ())).collect(Collectors.toList());
    }

}
