package com.impress.Infection;

/**
 * The survivor team for the Infection mode
 * @author 1mpre55
 */
public class Survivors extends Team {
	public Survivors(Game game) {
		this(game, null);
	}
	public Survivors(Game game, String name) {
		super(game, name == null? Survivors.class.getName() : name);
	}
	
	@Override
	void addPlayer(IPlayer player) {
//		if (player == null)
//			return;
		super.addPlayer(player);
	}
	@Override
	void removePlayer(IPlayer player) {
//		if (players.contains(player)) {
//			
//		}
		super.removePlayer(player);
	}
}