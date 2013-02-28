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
import com.impress.Infection.listeners.TagAPIListener;
import com.impress.Infection.utilities.Other;

/**
 * Represents a team
 * @author 1mpre55
 */
public class Team {
	public final String name;
	final Set<IPlayer> players = new HashSet<IPlayer>();
	final Game game;
	Color color;
	ChatColor cColor;
	public boolean colorNametag;
	private Rules rules;
	private Messages messages;
	
	public Team(Game game, String name) {
		if (game == null)
			throw new IllegalArgumentException("Game cannot be null");
		if (name == null)
			throw new IllegalArgumentException("Name cannot be null");
		this.name = name;
		this.game = game;
	}
	public void load(ConfigurationSection config) {
		setColor(config.getString("color"));
		if (config.isString("rules"))
			rules = game.plugin.rulesLoader.getRules(config.getString("rules"));
	}
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
		for (IPlayer pd : players.toArray(new IPlayer[players.size()])) {
			pd.leaveGame(false, false);
			if (broadcast)
				;	// TODO
		}
	}
	
	public void teamChat(IPlayer sender, String message) {
		broadcast('<' + sender.player.getDisplayName() + "> " + message);	// TODO
	}
	public void broadcast(String message) {
		for (IPlayer pd : players.toArray(new IPlayer[players.size()]))
			pd.player.sendMessage(message);
	}
	void updateCompassTarget() {
		Location t = null;	// TODO
		for (IPlayer pd : players.toArray(new IPlayer[players.size()]))
			pd.setCompassTarget(t);
	}
	public void giveKit(Kit kit) {
		if (kit == null) return;
		for (IPlayer pd : players.toArray(new IPlayer[players.size()]))
			kit.giveToPlayer(pd.player, false, false, true);
	}
	
	public Rules getRules() {
		if (rules == null)
			return game.getEvent().rules;
		else
			return rules;
	}
	public Messages getMessages() {
		if (messages == null)
			return game.getEvent().messages;
		else
			return messages;
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