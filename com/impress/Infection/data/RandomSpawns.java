package com.impress.Infection.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import com.impress.Infection.Team;
import com.impress.Infection.utilities.LocationTools;
import com.impress.Infection.utilities.Other;

/**
 * Implementation of Spawns that chooses a random spawn location when one is requested.
 * @author 1mpre55
 */
public class RandomSpawns implements Spawns {
	private static final String spawnsO = "locations";
	private List<Location> spawns;
	private final World world;
	Team currentTeam = null;
	/**
	 * Creates a new RandomSpawns and loads spawns from the config.
	 * @param config - ConfigurationSection containing the spawns.
	 * @param world - the world that the spawns 
	 * @throws IllegalArgumentException if either config or world is null
	 */
	public RandomSpawns(ConfigurationSection config, World world) throws IllegalArgumentException {
		if (config == null)
			throw new IllegalArgumentException("Null config");
		if (world == null)
			throw new IllegalArgumentException("Null world");
		this.world = world;
		load(config);
	}
	@Override
	public void load(ConfigurationSection config) {
		spawns = new ArrayList<Location>();
		if (config.isList(spawnsO))
			for (String spawn : config.getStringList(spawnsO))
				// TODO surround with try-catch
				spawns.add(LocationTools.locationFromCSVString(spawn, world));
	}
	
	@Override
	public Location getSpawn() {
		return Other.getRandomFromList(spawns);
	}
	@Override
	public Location[] getUniqueSpawns(int n) {
		if (spawns.isEmpty())
			return null;
		Location[] result = new Location[n];
		List<Location> s = new ArrayList<Location>(spawns.size());
		for (int i = 0; i < spawns.size(); i++)
			s.add(null);
		Collections.copy(s, spawns);
		
		for (int i = 0, c = -1; i < n; i++, c--) {
			if (c < 0) {
				Collections.shuffle(s);
				c = s.size() - 1;
			}
			result[i] = s.get(c);
		}
		return result;
	}
	@Override
	public boolean hasNext() {
		return !spawns.isEmpty();
	}
	@Override
	public Team getCurrentTeam() {
		return currentTeam;
	}
	@Override
	public void setCurrentTeam(Team team) {
		currentTeam = team;
	}
}