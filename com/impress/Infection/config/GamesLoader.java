package com.impress.Infection.config;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.impress.Infection.Game;
import com.impress.Infection.Infection;
import com.impress.Infection.exceptions.GameException;

public class GamesLoader extends ConfigManager {
	private final Logger log;
	private final HashMap<String, Game> games = new HashMap<String, Game>();
	private boolean modified = false;
	
	public GamesLoader(JavaPlugin plugin, String fileName, Logger logger) {
		super(plugin, fileName);
		log = (logger == null)? Logger.getLogger("Minecraft") : logger;
	}
	
	public Game getGame(String name) {
		return games.get(name);
	}
	void addGame(String name, Game game) {
		if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Invalid game name");
		if (game == null) throw new IllegalArgumentException("Null game");
		games.put(name, game);
		modified = true;
	}
	void deleteGame(String name) {
		games.remove(name);
		modified = true;
	}
	public HashMap<String, Game> getAllGames() {
		return games;
	}
	
	@Override
	public void load() {
		FileConfiguration config = getConfig();
		for (String gameName : config.getKeys(false).toArray(new String[0]))
			try {
				games.put(gameName, new Game((Infection)plugin, gameName, getConfigurationSection(config, gameName)));
			} catch (GameException e) {
				log.warning("Failed to load game " + gameName + ": " + e.getMessage());
			}
		modified = false;
		loaded = true;
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