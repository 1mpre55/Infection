package com.impress.Infection.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.impress.Infection.Game;
import com.impress.Infection.exceptions.ConfigurationMismatchException;

public class Arena {
	private final static String worldO = "world",
								spawnsO = "spawns";
	
	public final String name;
	
	public World world;
	
	public int[][] dimensions;
	
	public HashMap<String, Spawns> tSpawns;
	
	public List<Location> flags;
	
	//public Location respawnDelayLocation;
	
	//public boolean protect;
	
	public Game currentGame;
	
	//public String backup;
	
	class Team {
		
	}
	
	public void load(ConfigurationSection config) throws ConfigurationMismatchException {
		if (config.isString(worldO))
			if ((world = Bukkit.getServer().getWorld(config.getString(worldO))) == null)
				throw new ConfigurationMismatchException("World " + config.getString(worldO) + " was not found");
		if (world == null)
			world = Bukkit.getWorlds().get(0);
		
		// TODO load dimensions
		
		tSpawns = new HashMap<String, Spawns>();
		if (config.isConfigurationSection(spawnsO)) {
			ConfigurationSection c = config.getConfigurationSection(spawnsO);
			for (String team : c.getKeys(false))
				if (c.isConfigurationSection(team))
					tSpawns.put(team, new RandomSpawns(c.getConfigurationSection(team), world));
		}
		flags = new ArrayList<Location>();
	}
	
	/**
	 * Checks if this arena can support a game with specified options and verifies that all required data is valid.
	 * @param teams - the number of teams the game has
	 * @param flags - whether of not the game uses flag locations
	 * @return true if the arena can support a game with given parameters, false otherwise
	 */
	public boolean canSupport(int teams, boolean flags) {
		if (world == null)
			return false;
		if (tSpawns == null || tSpawns.size() < teams)
			return false;
		if (flags && (this.flags == null || this.flags.size() < teams))
			return false;
		for (int i = 0; i < teams; i++) {
			if (tSpawns.get(i) == null || !tSpawns.get(i).hasNext())
				return false;
			if (flags && this.flags.get(i) == null)
				return false;
		}
		return true;
	}
	/**
	 * Calculates the max number of teams that this arena can support.
	 * @param flags - whether or not the teams must have a flag location.
	 * @return the number of teams this arena can support, -1 if arena wasn't loaded or -2 if <b>flags</b> is true but the arena does not support flags 
	 */
	public int maxTeams(boolean flags) {
		if (tSpawns == null)
			return -1;
		if (flags)
			if (this.flags == null)
				return -1;
			else
				return Math.min(tSpawns.size(), this.flags.size());
		else
			return tSpawns.size();
	}
	
	/**
	 * Creates a new Arena and puts it in the plugin's arena list
	 */
	public Arena(String name) {
		this.name = name;
		instances.add(this);
	}
	/**
	 * Removes this arena from plugin's arena list
	 */
	public void remove() {
		instances.remove(this);
	}
	
	/**
	 * Checks if the given location is within the arena's area. Returns true if the arena does not have a specified area
	 * @param loc - location to check.
	 * @return - true if the location is in arena's area, false otherwise.
	 */
	boolean isInBounds(Location loc) {
		if (dimensions == null) return true;
		return (loc.getX() > dimensions[0][0] && loc.getX() < dimensions[0][1] &&
				loc.getY() > dimensions[1][0] && loc.getY() < dimensions[1][1] &&
				loc.getZ() > dimensions[2][0] && loc.getZ() < dimensions[2][1]);
	}
	/**
	 * If the given coordinates (or player's location if <b>from</b> is null) is outside the arena, it will be moved to the closest point within the arena
	 * and the given player will be teleported to the new location. Either argument can be null. This will not do anything if both arguments are null or if
	 * the source location is already within the arena's bounds.
	 * <br><i>Note: this will modify the given <b>from</b> location if it's outside the arena</i>
	 * @param from - the coordinates that may be outside the arena. This will be modified moving it to the closest location inside the arena.
	 * @param player - the player to be teleported to the resulting location. This may be null. If <b>from</b> is null player's location will be used instead.
	 */
	public void getInBounds(Location from, Player player) {
		if (from == null)
			if (player == null)
				return;
			else from = player.getLocation();
		if (isInBounds(from)) return;
		if (from.getX() < dimensions[0][0]) from.setX(dimensions[0][0]);
		else if (from.getX() > dimensions[0][1]) from.setX(dimensions[0][1]);
		if (from.getY() < dimensions[1][0]) from.setY(dimensions[1][0]);
		else if (from.getX() > dimensions[1][1]) from.setY(dimensions[1][1]);
		if (from.getZ() < dimensions[2][0]) from.setZ(dimensions[2][0]);
		else if (from.getX() > dimensions[2][1]) from.setZ(dimensions[2][1]);
		if (player != null)
			player.teleport(from);
	}
	
	/**
	 * Plugin's list of arenas
	 */
	public static final HashSet<Arena> instances = new HashSet<Arena>(5);
	/**
	 * Returns an arena that's responsible for the given location. If there are multiple arenas sharing that location, it will return one of them.
	 * @param loc - the location
	 * @return the arena in that location
	 */
	public static Arena getFromLocation(Location loc) {
		for (Arena arena : instances.toArray(new Arena[0]))
			if (arena.dimensions != null && arena.isInBounds(loc))
				return arena;
		return null;
	}
}