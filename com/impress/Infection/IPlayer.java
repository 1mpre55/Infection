// IPlayer == Infection Player. Not iPlayer. Don't call your lawyers.

package com.impress.Infection;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.impress.Infection.data.Kit;
import com.impress.Infection.listeners.TagAPIListener;
import com.impress.Infection.utilities.InvTools;

/**
 * Contains player-specific data and methods
 */
public class IPlayer {
	/**
	 * A map containing all players by their name
	 */
	private final static HashMap<String, IPlayer> instances = new HashMap<String, IPlayer>();
	/**
	 * Returns an {@link IPlayer} object for the given player. If the object was found in the plugin's player list it will generate a new one.
	 * This method is guaranteed to return a non-null result
	 * @param player - the player
	 * @return IPlayer associated with the given player
	 */
	public static IPlayer getIPlayer(Player player) {
		if (instances.containsKey(player.getName()))
			return instances.get(player.getName());
		else
			return new IPlayer(player);
	}
	private IPlayer(Player player) {
		instances.put((this.player = player).getName(), this);
	}
	/**
	 * Makes the player leave their current game, saves all their data to disk and removes them from plugin's player list.
	 * <b>This should only be called when the player leaves the server.</b> 
	 */
	public void remove() {
		if (isPlaying())
			leaveGame(false, true);
		resetCompassTarget();
		instances.remove(player.getName());
	}
	
	final Player player;
	Game game;
	Team team;
	boolean spectating;
	
	/**
	 * Player's kill count in the current game
	 */
	public int kills;
	/**
	 * Player's betrayal counter in the current game
	 */
	public int betrayals;
	/**
	 * Player's death counter in the current game
	 */
	public int deaths;
	/**
	 * The number of players that this player infected in the current Infection game
	 */
	public int infected;
	/**
	 * Player's score in the current game
	 */
	public int score;
	
	private Location oldLocation;
	private Location oldCompassTarget;
//	private SavedPlayerInventory respawnInv;
	
	boolean joinGame(Team team, boolean broadcast) {
		if (team == null) return false;
		this.team = team;
		game = team.game;
		
//		if (team.getRules().joinFilterMode != FilterMode.FILTER_DISABLED)
//			if (InvTools.itemFilter(player.getInventory(), rules.joinFilter, rules.joinFilterMode == FilterMode.FILTER_BLACKLIST,
//					!team.getRules().startFilterJoinDeny) && team.getRules().startFilterJoinDeny)
//				return false;
		
//		if (team.getRules().enchantmentFilterMode != FilterMode.FILTER_DISABLED)
//			if (InvTools.enchFilter(player.getInventory(), team.getRules().enchantmentFilter, team.getRules().enchantmentFilterMode == FilterMode.FILTER_BLACKLIST,
//					!team.getRules().startFilterJoinDeny) && team.getRules().startFilterJoinDeny)
//				return false;
		
		oldLocation = player.getLocation();
		
		// If the game is not active right now or if we don't clear inventories on respawn
//		if (!game.active || team.getRules().keepInventory) {
//			if (team.getRules().clearInvOnJoin)
//				player.getInventory().clear();
//			giveKits(team.getRules().joinKits);
//		}
//		if (game.active) {
//			respawn(null, false);
//		}
		
		team.addPlayer(this);
		putTeamArmor();
		
		//team.getRules().useCompass
		
		if (broadcast) {
			if (team.getMessages().pJoin != null)
				player.sendMessage(team.getMessages().pJoin
						.replaceAll("<TCOLOR>", getChatColor().toString())
						.replaceAll("<TCOLOUR>", getChatColor().toString())
						.replaceAll("<TEAM>", team.name));
			if (team.getMessages().bJoin != null)
				Bukkit.broadcastMessage(team.getMessages().bJoin
						.replaceAll("<TCOLOR>", getChatColor().toString())
						.replaceAll("<TCOLOUR>", getChatColor().toString())
						.replaceAll("<PCOLOR>", getChatColor().toString())
						.replaceAll("<PCOLOUR>", getChatColor().toString())
						.replaceAll("<TEAM>", team.name)
						.replaceAll("<PLAYER>", player.getName()));
		}
		
//		if (Infection.tagAPI && team.getRules().teamColorNametag)
//			TagAPIListener.refreshPlayer(player);
		
		return true;
	}
	void leaveGame(boolean broadcast, boolean dropItems) {	// TODO add dropItems options in rules
		if (team != null)
			team.removePlayer(this);
		
		if (game != null) {
			takeOffTeamArmor();
			if (team.getRules().clearInvOnLeave) {
				if (dropItems)
					InvTools.dropItems(player.getInventory(), player.getLocation());
				player.getInventory().clear();
			}
			//giveKits(team.getRules().quitKits);
			if (oldLocation != null) {
				player.teleport(oldLocation);
				oldLocation = null;
			}
			
			resetCompassTarget();
			
			if (broadcast) {
				if (team.getMessages().pLeave != null)
					player.sendMessage(team.getMessages().pLeave
							.replaceAll("<TCOLOR>", getChatColor().toString())
							.replaceAll("<TEAM>", team.name));
				if (team.getMessages().bLeave != null)
					Bukkit.broadcastMessage(team.getMessages().bLeave
							.replaceAll("<TCOLOR>", getChatColor().toString())
							.replaceAll("<PCOLOR>", getChatColor().toString())
							.replaceAll("<PLAYER>", player.getName())
							.replaceAll("<TEAM>", team.name));
			}
		}
		game = null;
		team = null;
//		deathInv = null;
		
//		if (kills > 0 || deaths > 0 || flags > 0) {
//			PlayerStats stats;
//			if (Infection.stats.containsKey(player.getName()))
//				stats = Infection.stats.get(player.getName());
//			else
//				Infection.stats.put(player.getName(), stats = new PlayerStats());
//			stats.flags += flags;
//			stats.kills += kills;
//			stats.deaths += deaths;
//			flags = kills = deaths = 0;
//		}
	}
	void setTeam(Team team) {
		if (team == null || this.team == team)
			return;
		if (this.team.game != team.game) {
			leaveGame(true, true);
			joinGame(team, true);
			return;
		}
		
		takeOffTeamArmor();
		this.team.removePlayer(this);
		
//		if (this.team.getRules().pTeamSwitchMessage != null)
//			player.sendMessage(this.team.getRules().pTeamSwitchMessage
//					.replaceAll("<TCOLOR>", team.cColor.toString())
//					.replaceAll("<TEAM>", team.name));
//		if (this.team.getRules().bTeamSwitchMessage != null)
//			Bukkit.broadcastMessage(this.team.getRules().bTeamSwitchMessage
//					.replaceAll("<TCOLOR>", team.cColor.toString())
//					.replaceAll("<TEAM>", team.name)
//					.replaceAll("<PLAYER>", player.getName())
//					.replaceAll("<PCOLOR>", this.getChatColor().toString()));
		
		this.team = team;
		team.addPlayer(this);
		
		putTeamArmor();
		
		if (Infection.tagAPI && this.team.getRules().teamColorNametags)
			TagAPIListener.refreshPlayer(player);
	}
	
	void respawn() {
		if (game.active)
			respawn(team.getSpawn());
		else
			respawn(null);
	}
	void respawn(Location l) {
		if (l == null) {
			// TODO
			player.teleport(oldLocation);
		} else {
			player.teleport(l);
		}
	}
	
	private void putTeamArmor() {
		if (team.getRules().teamArmor) {
			// TODO
		}
	}
	private void takeOffTeamArmor() {
		if (team.getRules().teamArmor) {
			// TODO
		}
	}
	/**
	 * Returns true if the player is in the spectators team, false otherwise
	 * @return whether or not the player is in the spectators team
	 */
	
	public boolean isSpectator() {
		return team instanceof Spectators;
	}
	/**
	 * Forces the player onto a spectators team
	 * @param spectate
	 */
	public void setSpectator(boolean spectate) {
		if (spectate == isSpectator())
			return;
		
		// TODO
		if (spectate)
			//game.
		
		refreshVisibility();
	}
	void refreshVisibility() {
		for (Team team : game.teams.values().toArray(new Team[game.teams.size()]))
			for (IPlayer player : team.players.toArray(new IPlayer[team.players.size()])) {
				if (isSpectator()) {
					this.player.showPlayer(player.player);
					if (player.isSpectator())
						player.player.showPlayer(this.player);
					else
						player.player.hidePlayer(this.player);
				}
				else {
					player.player.showPlayer(this.player);
					if (player.isSpectator())
						this.player.hidePlayer(player.player);
					else
						this.player.showPlayer(player.player);
				}
			}
	}
	
	void giveKits(Kit[] kits) {
		if (kits != null)
			for (Kit kit : kits)
				if (player.hasPermission(Infection.basePerm + "kits") || player.hasPermission(Infection.basePerm + "kits." + kit.name))
					kit.giveToPlayer(player, false, false, true);
	}
	
	/**
	 * Returns whether the player is currently playing a game or not
	 * @return true if the player is playing, false otherwise
	 */
	public boolean isPlaying() {
		return game != null;
	}
	/**
	 * Returns the {@link Game} that this player is playing or null if the player is not playing
	 * @return player's current game
	 */
	public Game getGame() {
		return game;
	}
	/**
	 * Returns player's current {@link Team} or null if they're not playing
	 * @return the team that this player is part of
	 */
	public Team getTeam() {
		return team;
	}
	/**
	 * Returns the color of player's team
	 * @return chat color
	 */
	public ChatColor getChatColor() {
		return team.cColor;
	}
	/**
	 * Sets player's compass target to a given location
	 * @param t - the new target location
	 */
	public void setCompassTarget(Location t) {
		if (t != null) {
			if (oldCompassTarget == null)
				oldCompassTarget = player.getCompassTarget();
			player.setCompassTarget(t);
		}
	}
	/**
	 * Resets player's compass target to the original
	 */
	public void resetCompassTarget() {
		if (oldCompassTarget != null)
			player.setCompassTarget(oldCompassTarget);
	}
}