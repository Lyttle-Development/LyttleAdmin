package com.lyttldev.lyttleadmin.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtil {

    /**
     * Converts a Location object to a string representation.
     *
     * @param location the Location object
     * @return the string representation of the location
     */
    public static String locationToString(Location location) {
        if (location == null) {
            return null;
        }
        return location.getWorld().getName() + "," +
                location.getX() + "," +
                location.getY() + "," +
                location.getZ() + "," +
                location.getYaw() + "," +
                location.getPitch();
    }

    /**
     * Converts a string representation of a location to a Bukkit Location object.
     *
     * @param locString the string representation of the location
     * @return the Location object, or null if the string is invalid
     */
    public static Location stringToLocation(String locString) {
        if (locString == null || locString.isEmpty()) {
            return null;
        }

        String[] parts = locString.split(",");
        if (parts.length < 4) {
            return null; // Not enough information to create a Location
        }

        World world = Bukkit.getWorld(parts[0]);
        if (world == null) {
            return null; // Invalid world name
        }

        try {
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);

            // If yaw and pitch are provided
            if (parts.length >= 6) {
                float yaw = Float.parseFloat(parts[4]);
                float pitch = Float.parseFloat(parts[5]);
                return new Location(world, x, y, z, yaw, pitch);
            }

            return new Location(world, x, y, z);
        } catch (NumberFormatException e) {
            return null; // Invalid number format
        }
    }
}
