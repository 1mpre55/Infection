package com.impress.Infection;

import org.bukkit.configuration.ConfigurationSection;

import com.impress.Infection.utilities.DisguiseCraftBridge;

/**
 * The zombie team for the Infection mode
 * @author 1mpre55
 */
public class Zombies extends Team {
	private boolean disguise;
	
	public Zombies(Game game) {
		this(game, null);
	}
	public Zombies(Game game, String name) {
		super(game, name == null? Zombies.class.getName() : name);
	}
	@Override
	public void load(ConfigurationSection config) {
		super.load(config);
		disguise = config.getBoolean("disguise", false);
	}
	
	@Override
	void addPlayer(IPlayer player) {
		if (player == null)
			return;
		super.addPlayer(player);
		
		if (disguise)
			if (Infection.disguiseCraft)
				DisguiseCraftBridge.infect(player.player);
			else if (Infection.mobDisguise)
				;	// TODO ... maybe?
	}
	@Override
	void removePlayer(IPlayer player) {
		if (players.contains(player)) {
			if (disguise)
				if (Infection.disguiseCraft)
					DisguiseCraftBridge.undisguise(player.player);
				else if (Infection.mobDisguise)
					;	// TODO
		}
		super.removePlayer(player);
	}
}