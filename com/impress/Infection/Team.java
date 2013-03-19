package com.impress.Infection;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.impress.Infection.data.Kit;
import com.impress.Infection.data.Messages;
import com.impress.Infection.data.Rules;
import com.impress.Infection.data.Spawns;
import com.impress.Infection.listeners.TagAPIListener;
import com.impress.Infection.utilities.Other;

/**
 * Represents a team
 * @author 1mpre55
 */
public class Team {
	/**
	 * The name of the team.
	 */
	public final String name;
	final Set<IPlayer> players = new HashSet<IPlayer>();
	final Game game;
	Spawns spawns;
	Color color;
	ChatColor cColor;
	public boolean colorNametag;
	private Rules rules;
	private Messages messages;
	
	/**
	 * Creates a new team.
	 * @param game - the game that this team is part of
	 * @param name - name of the team
	 */
	public Team(Game game, String name) {
		if (game == null)
			throw new IllegalArgumentException("Game cannot be null");
		if (name == null)
			throw new IllegalArgumentException("Name cannot be null");
		this.name = name;
		this.game = game;
		spawns = Other.getRandomFromArray(game.event.arena.tSpawns.values().toArray(new Spawns[0]));
	}
	/**
	 * Creates a new team and loads it's options from config
	 * @param game - the game that this team is part of
	 * @param name - name of the team
	 * @param config - team's options
	 */
	public Team(Game game, String name, ConfigurationSection config) {
		this(game, name);
		load(config);
	}
	/**
	 * Loads team's options
	 * @param config - ConfigurationSection containing the options
	 */
	public void load(ConfigurationSection config) {
		setColor(config.getString("color"));
		if (config.isString("rules"))
			rules = game.plugin.rulesLoader.getRules(config.getString("rules"));
		if (config.isString("messages"))
			messages = game.plugin.messagesLoader.getMessages(config.getString("messages"));
	}
	/**
	 * Sets team color
	 * @param color - new color or null to remove color
	 */
	public void setColor(Color color) {
		this.color = color;
		if (color == null)
			cColor = ChatColor.RESET;
		else
			cColor = Other.colorToChatColor(color);
		
		if (Infection.tagAPI)
			for (IPlayer player : players.toArray(new IPlayer[players.size()]))
				TagAPIListener.refreshPlayer(player.player);
	}
	/**
	 * Sets team color
	 * @param color - name of the new color or null to remove color
	 */
	public void setColor(String color) {
		setColor(Other.colorFromString(color));
	}
	
	void addPlayer(IPlayer player) {
		if (player != null)
			players.add(player);
	}
	void removePlayer(IPlayer player) {
		players.remove(player);
	}
	int size() {
		return players.size();
	}
	
	void kickAll(boolean broadcast) {
		for (IPlayer p : players.toArray(new IPlayer[players.size()])) {
			p.leaveGame(false, false);
			if (broadcast)
				;	// TODO
		}
	}
	void respawnAll() {
		if (game.active) {
			Location[] s = spawns.getUniqueSpawns(players.size());
			int c = 0;
			for (IPlayer p : players)
				p.respawn(s[c]);
		} else
			for (IPlayer p : players)
				p.respawn();
	}
	
	/**
	 * Sends a team chat message from a given player
	 * @param sender - the sender of the message
	 * @param message - the message
	 */
	public void teamChat(IPlayer sender, String message) {
		broadcast('<' + sender.player.getDisplayName() + "> " + message);	// TODO
	}
	/**
	 * Broadcasts a message to all players on the team
	 * @param message - the message
	 */
	public void broadcast(String message) {
		for (IPlayer pd : players.toArray(new IPlayer[players.size()]))
			pd.player.sendMessage(message);
	}
	void updateCompassTarget() {
		Location t = null;	// TODO
		for (IPlayer pd : players.toArray(new IPlayer[players.size()]))
			pd.setCompassTarget(t);
	}
	/**
	 * Gives a kit to all player on the team.
	 * @param kit - the kit
	 */
	public void giveKit(Kit kit) {
		if (kit == null) return;
		for (IPlayer pd : players.toArray(new IPlayer[players.size()]))
			kit.giveToPlayer(pd.player, false, false, true);
	}
	
	/**
	 * Returns {@link Rules} that apply to this team. May return null if the team
	 * does not have it's own rules and if the game did not choose an event yet
	 * @return team's rules
	 */
	public Rules getRules() {
		if (rules == null)
			return game.getEvent().rules;
		else
			return rules;
	}
	/**
	 * Returns {@link Messages} associated with this team. May return null if the team
	 * does not have it's own {@link Messages} and if the game did not choose an event yet
	 * @return
	 */
	public Messages getMessages() {
		if (messages == null)
			return game.getEvent().messages;
		else
			return messages;
	}
	
	/**
	 * Returns the next spawn location for a player on the team.
	 * @return a (re)spawn location
	 */
	public Location getSpawn() {
		return spawns.getSpawn();
	}
	
	/**
	 * Creates a new team of specified type.
	 * @param type - type of the team you want to create. Use null to create a default team.
	 * @param game - the game that this team should be attached to.
	 * @param name - name of the team. Some teams may allow null names.
	 * @return the newly generated team or null if type is unknown.
	 */
	static Team createByType(String type, Game game, String name) {
		if (type == null || type.equalsIgnoreCase("default") || type.equalsIgnoreCase("regular"))
			return new Team(game, name);
		if (type.equalsIgnoreCase("spectators"))
			return new Spectators(game, name);
		if (type.equalsIgnoreCase("zombies"))
			return new Zombies(game, name);
		if (type.equalsIgnoreCase("survivors"))
			return new Survivors(game, name);
		return null;
	}
}