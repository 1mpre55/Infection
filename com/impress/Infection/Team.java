package com.impress.Infection;

import java.util.HashSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;

/**
 * Team-related methods and data
 * @author 1mpre55
 */
public class Team {
	public final String name;
	final HashSet<IPlayer> players = new HashSet<IPlayer>();
	//final Game game;
	Color color;
	ChatColor cColor;
	List<Location> staticSpawns;
	
	public Team(String name) {
		this.name = name;
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
			pd.leaveGame(false);
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
			pd.player.setCompassTarget(t);
	}
	
	public void giveKit(Kit kit) {
		if (kit == null) return;
		for (IPlayer pd : players.toArray(new IPlayer[players.size()]))
			kit.giveToPlayer(pd.player, false, false, true);
	}
}