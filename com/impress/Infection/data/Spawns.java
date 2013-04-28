package com.impress.Infection.data;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.impress.Infection.Team;

public interface Spawns {
	/**
	 * Loads spawns from config
	 * @param config - {@link ConfigurationSection} to load spawns data from
	 */
	public void load(ConfigurationSection config);
	/**
	 * @return next spawn location
	 */
	public Location getSpawn();
	/**
	 * Returns an array of <b>n</b> spawn locations with as few duplicate locations as possible.
	 * @param n - the number of spawns to return.
	 * @return an array of spawn locations.
	 */
	public Location[] getUniqueSpawns(int n);
	/**
	 * @return whether or not this can return any spawn locations
	 */
	public boolean hasNext();
	/**
	 * Sets the current team. Use this to make a team "claim" this spawn.
	 */
	public void setCurrentTeam(Team team);
	/**
	 * Removes current team. Non-null result indicates that this spawn is "claimed" by that team.
	 */
	public Team getCurrentTeam();
}