package org.codemc.worldguardwrapper.flags;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.implementation.AbstractFlag;

/**
 * A flag that stores a bukkit location.
 */
public class LocationFlag extends AbstractFlag<Location> {

    public LocationFlag(String name) {
        this(name, null);
    }

    public LocationFlag(String name, Location defaultValue) {
        super(name, Location.class, defaultValue);
    }

    @Override
    public Object serialize(Location value) {
        Map<String, Object> map = new HashMap<>();

        map.put("world", value.getWorld().getName());
        map.put("x", value.getX());
        map.put("y", value.getY());
        map.put("z", value.getZ());
        map.put("yaw", value.getYaw());
        map.put("pitch", value.getPitch());

        return map;
    }

    @Override
    public Location deserialize(Object serialized) {
        if (serialized instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) serialized;

            Object worldName = map.get("world");
            if (worldName == null) return null;
            
            Object x = map.get("x");
            if (x == null) return null;
            
            Object y = map.get("y");
            if (y == null) return null;
            
            Object z = map.get("z");
            if (z == null) return null;
            
            Object yaw = map.get("yaw");
            if (yaw == null) return null;
            
            Object pitch = map.get("pitch");
            if (pitch == null) return null;
            
            World world = Bukkit.getWorld(String.valueOf(worldName));
            if (world == null) return null;

            return new Location(world, toNumber(x), toNumber(y), toNumber(z),
                    (float) toNumber(yaw), (float) toNumber(pitch));
        }
        return null;
    }

    @Override
    public Location parse(Player player, String userInput) {
        if ("here".equalsIgnoreCase(userInput)) {
            return player.getLocation();
        } else if ("none".equalsIgnoreCase(userInput)) {
            return null;
        } else {
            String[] split = userInput.split(",");
            if (split.length >= 3) {
                final World world = player.getWorld();
                final double x = Double.parseDouble(split[0]);
                final double y = Double.parseDouble(split[1]);
                final double z = Double.parseDouble(split[2]);
                final float yaw = split.length < 4 ? 0 : Float.parseFloat(split[3]);
                final float pitch = split.length < 5 ? 0 : Float.parseFloat(split[4]);

                return new Location(world, x, y, z, yaw, pitch);
            }
        }
        return null;
    }

    private double toNumber(Object o) {
        if (o instanceof Number) {
            return ((Number) o).doubleValue();
        } else {
            return 0;
        }
    }
    
}