// IPlayer == Infection Player. Not iPlayer. Don't call your lawyers.

package com.impress.Infection;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Contains player-specific data and methods
 */
public class IPlayer {
	/**A map containing all players by their name*/
	final static HashMap<String, IPlayer> instances = new HashMap<String, IPlayer>();
	/**
	 * Returns an {@link IPlayer} object for the given player. If no object was found, a new one will be generated.
	 * This method is guaranteed to return a non-null result
	 * @param player
	 */
	public static IPlayer getPlayerData(Player player) {
		if (instances.containsKey(player.getName()))
			return instances.get(player.getName());
		else
			return new IPlayer(player);
	}
	public IPlayer(Player player) {
		instances.put((this.player = player).getName(), this);
	}
	void remove() {
		if (isPlaying())
			leaveGame(false);
		instances.remove(player.getName());
	}
	
	final Player player;
	Game game;
	Team team;
	
	public int kills;
	public int betrayals;
	public int deaths;
	public int infected;
	
	public int score;
	private Location oldLocation;
//	private SavedPlayerInventory respawnInv;
	
	boolean joinGame(Team team, boolean broadcast) {
		
		return false;
	}
	void leaveGame(boolean broadcast) {
		
	}
	void changeTeam(Team newTeam) {
		if (team == null || this.team.equals(team))
			return;
//		if (this.team.game != team.game) {
//			quitGame(true, true);
//			joinGame(team, true);
//			return;
//		}
		
	}
	
//	void giveKits(Kit[] kits) {
//		if (kits != null)
//			for (Kit kit : kits)
//				if (player.hasPermission(InstantGames.basePerm + "kits") || player.hasPermission(InstantGames.basePerm + "kits." + kit.name))
//					kit.giveToPlayer(player, false, false, true);
//	}
	
	private void putTeamArmor() {
		
	}
	private void takeOffArmor() {
		
	}
	
	void giveKits(Kit[] kits) {
		if (kits != null)
			for (Kit kit : kits)
				if (player.hasPermission(Infection.basePerm + "kits") || player.hasPermission(Infection.basePerm + "kits." + kit.name))
					kit.giveToPlayer(player, false, false, true);
	}
	
	public boolean isPlaying() {
		return game != null;
	}
	public Game getGame() {
		return game;
	}
	public Team getTeam() {
		return team;
	}
	public ChatColor getChatColor() {
		return team.cColor;
	}
	
}