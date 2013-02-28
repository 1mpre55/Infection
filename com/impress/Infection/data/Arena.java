package com.impress.Infection.data;

import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.impress.Infection.Game;

public class Arena {
	public String name;
	
	public World world;
	
	public int[][] dimentions;
	
	public List<Spawns> tSpawns;
	
	public List<Location> flags;
	
	//public Location respawnDelayLocation;
	
	//public boolean protect;
	
	public Game currentGame;
	
	//public String backup;
	
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
	
	public Arena() {
		instances.add(this);
	}
	public void remove() {
		instances.remove(this);
	}
	
	private boolean isInBounds(Location loc) {
		if (dimentions == null) return true;
		return (loc.getX() > dimentions[0][0] && loc.getX() < dimentions[0][1] &&
				loc.getY() > dimentions[1][0] && loc.getY() < dimentions[1][1] &&
				loc.getZ() > dimentions[2][0] && loc.getZ() < dimentions[2][1]);
	}
	public void getInBounds(Location from, Player player) {
		if (from == null)
			if (player == null)
				return;
			else from = player.getLocation();
		if (isInBounds(from)) return;
		if (from.getX() < dimentions[0][0]) from.setX(dimentions[0][0]);
		else if (from.getX() > dimentions[0][1]) from.setX(dimentions[0][1]);
		if (from.getY() < dimentions[1][0]) from.setY(dimentions[1][0]);
		else if (from.getX() > dimentions[1][1]) from.setY(dimentions[1][1]);
		if (from.getZ() < dimentions[2][0]) from.setZ(dimentions[2][0]);
		else if (from.getX() > dimentions[2][1]) from.setZ(dimentions[2][1]);
		if (player != null)
			player.teleport(from);
	}
	
	public static final HashSet<Arena> instances = new HashSet<Arena>(5);
	public static Arena getFromLocation(Location loc) {
		for (Arena arena : instances.toArray(new Arena[0]))
			if (arena.dimentions != null && arena.isInBounds(loc))
				return arena;
		return null;
	}
}