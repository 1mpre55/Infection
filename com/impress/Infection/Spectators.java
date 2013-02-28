package com.impress.Infection;

import org.bukkit.Color;

/**
 * A special team
 * @author 1mpre55
 */
public class Spectators extends Team {
	public Spectators(Game game) {
		this(game, null);
	}
	public Spectators(Game game, String name) {
		super(game, name == null? Spectators.class.getName() : name);
		setColor((Color)null);
	}
	
	@Override
	void addPlayer(IPlayer player) {
		addSpectator(player, false);
	}
	void addSpectator(IPlayer player, boolean hidden) {
		if (player == null)
			return;
		super.addPlayer(player);
		
		player.player.setAllowFlight(true);
		for (Team team : game.teams.values().toArray(new Team[game.teams.size()]))
			for (IPlayer pl : team.players.toArray(new IPlayer[team.players.size()]))
				pl.player.hidePlayer(player.player);
		if (hidden)
			for (IPlayer pl : players.toArray(new IPlayer[players.size()]))
				pl.player.hidePlayer(player.player);
	}
	@Override
	void removePlayer(IPlayer player) {
		if (players.contains(player)) {
			player.player.setAllowFlight(false);
			for (Team team : game.teams.values().toArray(new Team[game.teams.size()]))
				for (IPlayer pl : team.players.toArray(new IPlayer[team.players.size()]))
					pl.player.showPlayer(player.player);
			for (IPlayer pl : players.toArray(new IPlayer[players.size()]))
				pl.player.showPlayer(player.player);
		}
		
		super.removePlayer(player);
	}
}