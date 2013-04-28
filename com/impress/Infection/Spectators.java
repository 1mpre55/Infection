package com.impress.Infection;

import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;

import com.impress.Infection.Team.Options.Data;

/**
 * A special team
 * @author 1mpre55
 */
public class Spectators extends Team {
	enum Booleans implements Data<Boolean> {
		VANISH("vanish", true),
		INVINCIBLE("invincible", true),
		PREVENT_DAMAGE("prevent-damage", true),
		PREVENT_DROPS("prevent-drops", true),
		PREVENT_PICKUP("prevent-pickup", true),
		PREVENT_WORLD_MODIFY("prevent-world-modification", true);
		private String key;
		private Boolean def;
		private Booleans(String key, boolean def) {
			this.key = key;
			this.def = def;
		}
		@Override
		public String getKey() {
			return key;
		}
		@Override
		public Boolean getDef() {
			return def;
		}
	}
	public boolean invincible;
	public boolean preventDamage;
	public boolean preventDrops;
	public boolean preventPickup;
	public boolean preventWorldModify;
	
	public Spectators(Game game) {
		this(game, null);
	}
	public Spectators(Game game, String name) {
		super(game, name == null? Spectators.class.getName() : name);
		if (options != null)
			setColor((Color)null);
	}
	
	@Override
	public Options loadOptions(Options options, ConfigurationSection config) {
		options = super.loadOptions(options, config);
		for (Booleans b : Booleans.values())
			if (config.isBoolean(b.key))
				options.setBoolean(b, config.getBoolean(b.key));
		return options;
	}
	
	@Override
	public void setCurrentOptions(Options options) {
		super.setCurrentOptions(options);
		invincible = getOptions().getBoolean(Booleans.INVINCIBLE);
		preventDamage = getOptions().getBoolean(Booleans.PREVENT_DAMAGE);
		preventDrops = getOptions().getBoolean(Booleans.PREVENT_DROPS);
		preventPickup = getOptions().getBoolean(Booleans.PREVENT_PICKUP);
		preventWorldModify = getOptions().getBoolean(Booleans.PREVENT_WORLD_MODIFY);
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