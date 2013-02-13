package com.impress.Infection.utilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import com.impress.Infection.exceptions.ConfigurationMissingKeysException;

/**
 * A few {@link Location}-related tools 
 * @author 1mpre55
 */
public class LocationTools {
	/**
	 * Parses the CSV string for x, y and z, and additional (optional) yaw and pitch values and returns specified location.
	 * @param string - the CSV string 
	 * @param world - the world that the location should be in
	 * @return the Location specified by the CSV string in the given world
	 * @throws IllegalArgumentException if string is null or if it contains too few (< 3) or too many (> 5) values or an invalid number.
	 */
	public static Location locationFromCSVString(String string, World world) throws IllegalArgumentException {
		if (string == null) throw new IllegalArgumentException("Null string");
		String[] values = string.split(",");
		if (values.length < 3) throw new IllegalArgumentException("Not enough values");
		if (values.length > 5) throw new IllegalArgumentException("Too many values");
		double x, y, z;
		float ya, p = 90;
		try {
			x = Double.parseDouble(values[0].trim());
			y = Double.parseDouble(values[1].trim());
			z = Double.parseDouble(values[2].trim());
			if (values.length > 3) {
				ya = Float.parseFloat(values[3]);
				if (values.length > 4)
					p = Float.parseFloat(values[4]);
				return new Location(world, x, y, z, ya, p);
			}
			else return new Location(world, x, y, z);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid number");
		}
	}
	/**
	 * Loads a location from a ConfigurationSection. Uses "world", "x", "y", "z", "yaw" and "pitch" keys.
	 * @param config - ConfigurationSection to get the location information from 
	 * @param defaultWorld - world to be used if it's missing in the config
	 * @return Location specified in the given ConfigurationSection
	 * @throws ConfigurationMissingKeysException if config is missing "x", "y" and/or "z" keys values
	 * @throws IllegalArgumentException if config is null
	 */
	public static Location loadFromConfigurationSection(ConfigurationSection config, World defaultWorld) throws ConfigurationMissingKeysException {
		if (config == null) throw new IllegalArgumentException("null config");
		World world = null;
		double x, y, z, yaw, pitch;
		
		if ((!config.isDouble("x") && !config.isInt("x")) || (!config.isDouble("y") && !config.isInt("y")) || (!config.isDouble("z") && !config.isInt("z")))
			throw new ConfigurationMissingKeysException("missing xyz coordinates"); 
		x = config.getDouble("x", config.getInt("x"));
		y = config.getDouble("y", config.getInt("y"));
		z = config.getDouble("z", config.getInt("z"));
		
		if (config.isString("world"))
			world = Bukkit.getWorld(config.getString("world"));
		if (world == null) world = defaultWorld;
		
		yaw = config.getDouble("yaw", config.getInt("yaw", 0));
		pitch = config.getDouble("pitch", config.getInt("pitch", 90)); // DEBUG figure out whether to use 90, 0 or whatever the default should be
		
		return new Location(world, x, y, z, (float)yaw, (float)pitch);
	}
	/**
	 * Generates a location from the smallest x, y and z values found in <b>l1</b> and <b>l2</b> and returns it. Yaw and pitch are ignored.
	 * @param l1 - one location
	 * @param l2 - another location
	 * @return the new location or <b>null</b> if the given locations are in different worlds
	 */
	public static Location getMin(Location l1, Location l2) {
		if (l1.getWorld() != l2.getWorld())
			return null;
		if (l1.getX() <= l2.getX() && l1.getY() <= l2.getY() && l1.getZ() <= l2.getZ())
			return l1.clone();
		if (l2.getX() <= l1.getX() && l2.getY() <= l1.getY() && l2.getZ() <= l1.getZ())
			return l2.clone();
		return new Location(l1.getWorld(), Math.min(l1.getX(), l2.getX()), Math.min(l1.getY(), l2.getY()), Math.min(l1.getZ(), l2.getZ()));
	}
	/**
	 * Generates a location from the largest x, y and z values found in <b>l1</b> and <b>l2</b> and returns it. Yaw and pitch are ignored.
	 * @param l1 - one location
	 * @param l2 - another location
	 * @return the new location or <b>null</b> if the given locations are in different worlds
	 */
	public static Location getMax(Location l1, Location l2) {
		if (l1.getWorld() != l2.getWorld()) return null;
		if (l1.getX() <= l2.getX() && l1.getY() <= l2.getY() && l1.getZ() <= l2.getZ())return l2.clone();
		if (l2.getX() <= l1.getX() && l2.getY() <= l1.getY() && l2.getZ() <= l1.getZ())return l1.clone();
		return new Location(l1.getWorld(), Math.max(l1.getX(), l2.getX()), Math.max(l1.getY(), l2.getY()), Math.max(l1.getZ(), l2.getZ()));
	}
}