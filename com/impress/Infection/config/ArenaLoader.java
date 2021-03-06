package com.impress.Infection.config;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.impress.Infection.data.Arena;
import com.impress.Infection.exceptions.ConfigurationMismatchException;

public class ArenaLoader extends ConfigManager {
	private final Logger log;
	private final HashMap<String, Arena> arenas = new HashMap<String, Arena>();
	private boolean modified = false;
	
	public ArenaLoader(JavaPlugin plugin, String fileName, Logger logger) {
		super(plugin, fileName);
		log = (logger == null)? Logger.getLogger("Minecraft") : logger;
	}
	
	public Arena getArena(String name) {
		return arenas.get(name);
	}
	void addArena(String name, Arena arena) {
		if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Invalid arena name");
		if (arena == null) throw new IllegalArgumentException("Null arena");
		arenas.put(name, arena);
		modified = true;
	}
	void deleteArena(String name) {
		arenas.remove(name);
		modified = true;
	}
	public HashMap<String, Arena> getAllArenas() {
		return arenas;
	}
	
	@Override
	public void load() {
		FileConfiguration config = getConfig();
		Set<String> arenaNames = config.getKeys(false);
		for (String arena : arenaNames)
			if (!arenas.containsKey(arena))
			try {
				Arena a = new Arena(arena);
				a.load(getConfigurationSection(config, arena));
				arenas.put(arena, a);
			} catch (ConfigurationMismatchException e) {
				log.warning("Error loading arena \"" + arena + "\":" + e.getMessage());
			}
		else log.warning("Duplicate map \"" + arena + "\" in " + fileName + ", new map ignored");
	}
	@Override
	public void save() {
		save(false);
	}
	public void save(boolean saveIfUnchanged) {
		if (!modified && !saveIfUnchanged) return;
		// TODO
		modified = false;
	}
}