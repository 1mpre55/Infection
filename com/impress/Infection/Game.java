package com.impress.Infection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import com.impress.Infection.data.Arena;
import com.impress.Infection.data.Messages;
import com.impress.Infection.data.Rules;
import com.impress.Infection.exceptions.AlreadyPlayingException;
import com.impress.Infection.exceptions.ArenaNotFoundException;
import com.impress.Infection.exceptions.ConfigurationMismatchException;
import com.impress.Infection.exceptions.GameAlreadyStartedException;
import com.impress.Infection.exceptions.GameException;
import com.impress.Infection.exceptions.NoPermissionException;
import com.impress.Infection.exceptions.TeamNotFoundException;
import com.impress.Infection.utilities.Other;

public class Game {
	
	Infection plugin;
	
	String name;
	Team spectators;
	final Map<String, Team> teams;
	final List<Event> events;
	boolean sequential; //TODO change to enum?
	boolean loop;
	
	Event event;
	
	int players;
	boolean active;
	
	/**
	 * Creates an empty Game object with no teams and no arenas.
	 * @param plugin - the plugin that this Game is hosted by
	 * @param name - name of the game
	 */
	public Game(Infection plugin, String name) {
		if (plugin == null)
			throw new IllegalArgumentException("Plugin can't be null");
		if (name == null)
			name = "a game";
		this.plugin = plugin;
		this.name = name;
		
		spectators = new Spectators(this);
		teams = new HashMap<String, Team>();
		events = new ArrayList<Event>();
	}
	/**
	 * Creates a Game object and loads its options from {@link loadFrom}
	 * @param plugin - the plugin that this Game is hosted by
	 * @param name - the name of the game
	 * @param loadFrom - {@link ConfigurationSection} to load game's options from
	 * @throws ConfigurationMismatchException TODO
	 */
	public Game(Infection plugin, String name, ConfigurationSection loadFrom) throws GameException {
		this(plugin, name);
		load(loadFrom);
	}
	/**
	 * Loads options from <b>config</b> and returns itself
	 * @param config - {@link ConfigurationSection} to load game's options from
	 * @return this Game
	 * @throws ConfigurationMismatchException TODO 
	 */
	void load(ConfigurationSection config) throws GameException {
		List<String> events = config.getStringList("arenas");
		if (events != null)
			for (String event : events) {
				String[] s = event.split(":", 4);
				if (s.length < 2)
					throw new ConfigurationMismatchException("Invalid arena listing format: " + event);
				Event e = new Event();
				e.arena = plugin.arenaLoader.getArena(s[0].trim());
				if (e.arena == null)
					throw new ArenaNotFoundException(s[0].trim(), "Arena " + s[0].trim() + " was not found");
				e.rules = plugin.rulesLoader.getRules(s[1].trim());
				if (e.rules == null)
					throw new GameException("Rules " + s[1].trim() + "were not found");
				if (s.length > 2)
					e.messages = plugin.messagesLoader.getMessages(s[2]);
				if (e.messages == null)
					e.messages = plugin.defaultMessages;
				if (e.messages == null)
					throw new GameException("Messages were not found.");
				this.events.add(e);
			}
		sequential = "SEQUENTIAL".equalsIgnoreCase(config.getString("arena-order"));
		loop = config.getBoolean("loop", false);
		if (config.isConfigurationSection("teams"))
			for (String teamName : config.getConfigurationSection("teams").getKeys(false).toArray(new String[0])) {
				ConfigurationSection cs = config.getConfigurationSection("teams." + teamName);
				Team team = Team.createByType(cs.getString("type"), this, cs.getString("name", teamName));
				team.load(cs);
				teams.put(team.name.toLowerCase(), team);
			}
	}
	class Event {
		Arena arena;
		Rules rules;
		Messages messages;
	}
	
	/**
	 * Called when a player uses a join command. This methods checks if the player is allowed to join.
	 * To bypass the checks simply call player.joinGame()
	 * @param player - player that wants to join
	 * @param team - the team that this player wants to join or null to auto-select one
	 * @throws GameException if the player is not allowed to join. Possible reasons: no join permission, no event was chosen yet,
	 * team not found, player is already playing a game, no permission to ignore team balance or unable to join after the game started.
	 * Use exception.getMessage() for a player-friendly reason that they're unable to join.
	 */

	public void playerJoin(IPlayer player, String team) throws GameException {
		if (player == null)
			throw new IllegalArgumentException("Null player");
		if (event == null)
			throw new TeamNotFoundException("Can't join right now");
		if (!player.player.hasPermission(Infection.basePerm + "join") && !player.player.hasPermission(Infection.basePerm + "join." + name))
			throw new NoPermissionException(Infection.basePerm + "join", "You don't have permission to join");
//		if (players.size() >= rules.maxPlayers && !player.player.hasPermission(Infection.basePerm + "ignore.maxplayers"))
//			throw new GameFullException(rules.maxPlayers);
		
		if (player.isPlaying())
			throw new AlreadyPlayingException("You can't join since you're already playing");
		
		Team join;
		if (team == null)
			join = smallestTeam(true);
		else {
			if (!player.player.hasPermission(Infection.basePerm + "jointeam") && !player.player.hasPermission(Infection.basePerm + "jointeam." + name))
				throw new NoPermissionException(Infection.basePerm + "jointeam", "You don't have permission to choose a team");
			if (teams.containsKey(team.toLowerCase()))
				join = teams.get(team.toLowerCase());
			else throw new TeamNotFoundException("Team " + team + " was not found");
			if (!isSmallestTeam(join) && !player.player.hasPermission(Infection.basePerm + "ignore.teambalance")
					&& !player.player.hasPermission(Infection.basePerm + "ignore.teambalance." + name))
				throw new NoPermissionException(Infection.basePerm + "ignore.teambalance", "You can only join the team with the least players");
		}
		if (active && !join.getRules().joinAfterStart && !player.player.hasPermission(Infection.basePerm + "joinafterstart")
				&& !player.player.hasPermission(Infection.basePerm + "joinafterstart." + name))
			throw new GameAlreadyStartedException();
		if (player.joinGame(join, true)) {
			if (Infection.debug)
				System.out.println("[DEBUG] Player " + player.player.getName() + " joined the game");
		}
	}
	/**
	 * Called when player uses the leave command. There is currently no situation when a player is not allowed to leave a game,
	 * so calling this will guarantee that the player is no longer playing this game. To make the player leave ANY game call player.leaveGame()
	 * @param player - player that wants to leave
	 * @return true if player was playing this game, false otherwise
	 */
	public boolean playerLeave(IPlayer player) {
		if (Infection.debug)
			System.out.println("[DEBUG] Player '" + player + "' is quitting the game");
		if (player == null)
			throw new IllegalArgumentException("Null player");
		if (player.getGame() == this) {
			player.leaveGame(true, true);
			if (Infection.debug)
				System.out.println("[DEBUG] Player " + player.player.getName() + " quit the game");
			return true;
		} else {
			if (Infection.debug)
				System.out.println("[DEBUG] Player " + player.player.getName() + " wasn't playing");
			return false;
		}
	}
	/**
	 * Called when a player uses the team change command. This methods checks if the player is allowed to change team.
	 * To bypass the checks call player.setTeam()
	 * @param player - the player that wants to change team
	 * @param team - the new team
	 * @throws TeamNotFoundException if the game does not contain a team with that name
	 * @throws NoPermissionException if the player does not have permission to perform this operation.
	 * Possible reasons: no permission to change teams, no permission to ignore team balance.
	 */
	public void playerChangeTeam(IPlayer player, String team) throws TeamNotFoundException, NoPermissionException {
		if (player == null)
			throw new IllegalArgumentException("Null player");
		if (!player.team.getRules().allowTeamChange && !player.player.hasPermission(Infection.basePerm + "changeteam")
				&& !player.player.hasPermission(Infection.basePerm + "changeteam." + name))
			throw new NoPermissionException(Infection.basePerm + "changeteam", "You don't have permission to change your team");
		
		Team join;
		if (team == null)
			join = smallestTeam(true);
		else {
			if (teams.containsKey(team.toLowerCase()))
				join = teams.get(team.toLowerCase());
			else throw new TeamNotFoundException("Team " + team + " was not found");
			if (!isSmallestTeam(join) && !player.player.hasPermission(Infection.basePerm + "ignore.teambalance")
					&& !player.player.hasPermission(Infection.basePerm + "ignore.teambalance." + name))
				throw new NoPermissionException(Infection.basePerm + "ignore.teambalance", "You can only join the team with the least players");
		}
		player.setTeam(join);
		if (Infection.debug)
			System.out.println("[DEBUG] Player " + player.player.getName() + " switched to " + join.name + " team");
	}
	
	void chooseEvent(boolean random) {
		if (random)
			setEvent(Other.getRandomFromList(events));
		else {
			int index = events.indexOf(event) + 1;
			if (index >= events.size())
				index = 0;
			setEvent(events.get(index));
		}
	}
	private void setEvent(Event event) {
		if (event == null)
			return;
		this.event = event;
	}
	public Event getEvent() {
		return event;
	}
	
	/**
	 * Finds the smallest team.
	 * @param random - determines what team will be returned if multiple smallest teams were found.
	 * @return the smallest team.
	 */
	public Team smallestTeam(boolean random) {
		if (teams.size() < 1)
			return null;
		int size = smallestTeamSize();
		ArrayList<Team> results = random? new ArrayList<Team>(teams.size()) : null;
		for (Team team : teams.values()) {
			if (size < team.size())
				continue;
			if (random)
				results.add(team);
			else
				return team;
		}
		return Other.getRandomFromList(results);
	}
	/**
	 * @param team - the team to check
	 * @return whether or not the specified team is the smallest team.
	 */
	public boolean isSmallestTeam(Team team) {
		if (team == null || !teams.containsValue(team))
			return false;
		else
			return team.size() <= smallestTeamSize();
	}
	/**
	 * @return the size of the smallest team.
	 */
	public int smallestTeamSize() {
		if (teams.size() < 1)
			return -1;
		int result = Integer.MAX_VALUE;
		Team[] tms = teams.values().toArray(new Team[teams.size()]);
		for (int i = 0; i < tms.length; i++)
			result = Math.min(result, tms[i].size());
		return result;
	}
	void recalculatePlayers() {
		players = 0;
		for (Team team : teams.values()) {
			players += team.size();
		}
	}
}