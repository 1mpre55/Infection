package com.impress.Infection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import com.impress.Infection.data.Arena;
import com.impress.Infection.data.Messages;
import com.impress.Infection.data.Rules;
import com.impress.Infection.data.Rules.Booleans;
import com.impress.Infection.data.Spawns;
import com.impress.Infection.exceptions.AlreadyPlayingException;
import com.impress.Infection.exceptions.ArenaNotFoundException;
import com.impress.Infection.exceptions.ConfigurationMismatchException;
import com.impress.Infection.exceptions.ConfigurationMissingKeysException;
import com.impress.Infection.exceptions.GameAlreadyStartedException;
import com.impress.Infection.exceptions.GameException;
import com.impress.Infection.exceptions.NoEventsException;
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
	
	boolean chooseNewEvent;
	Event event;
	
	int players;
	boolean active;
	
	Rules defRules;
	Messages defMessages;
	
	/**
	 * Creates an empty Game object with no teams and no arenas.
	 * @param plugin - the plugin that this Game is hosted by
	 * @param name - name of the game
	 */
	public Game(Infection plugin, String name) {
		if (plugin == null)
			throw new IllegalArgumentException("null plugin");
		if (name == null)
			throw new IllegalArgumentException("null name");
		this.plugin = plugin;
		this.name = name;
		
		spectators = new Spectators(this);
		teams = new HashMap<String, Team>();
		events = new ArrayList<Event>();
		event = null;
		chooseNewEvent = true;
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
		sequential = "SEQUENTIAL".equalsIgnoreCase(config.getString("event-order"));
		loop = config.getBoolean("loop", false);
		if (config.isConfigurationSection("teams"))
			for (String teamName : config.getConfigurationSection("teams").getKeys(false).toArray(new String[0])) {
				ConfigurationSection cs = config.getConfigurationSection("teams." + teamName);
				Team team = Team.createByType(cs.getString("type"), this, cs.getString("name", teamName), true);
				team.configName = teamName;
				team.load(cs);
				teams.put(team.name.toLowerCase(), team);
			}
		if (config.isConfigurationSection("events")) {
			for (String s : config.getConfigurationSection("events").getKeys(false)) {
				ConfigurationSection c = config.getConfigurationSection("events." + s);
				if (c == null)
					continue;
				Event e = new Event();
				try {
					e.load(c, this, null, plugin.defaultMessages, -2);
					this.events.add(e);
				} catch (GameException ex) {
					plugin.getLogger().warning("event " + s + " failed to load: " + ex.getMessage());
				}
			}
		}
		defRules = plugin.rulesLoader.getRules(config.getString("rules"));
		if (defRules == null) {
			defRules = new Rules("default");
			defRules.load(null, true);
		}
		defMessages = plugin.messagesLoader.getMessages(config.getString("messages"));
		if (defMessages == null) {
			// TODO create defMessages similarly to defRules above
			try {
				defMessages = new Messages(config);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	class Event {
		Arena arena;
		Rules rules;
		Messages messages;
		long time;
		Map<Team, Team.Options> teams;
		
		private void load(ConfigurationSection config, Game game, Rules defRules, Messages defMessages, long defTime) throws GameException {
			arena = plugin.arenaLoader.getArena(config.getString("arena"));
			if (arena == null)
				if (config.isString("arena"))
					throw new ArenaNotFoundException(config.getString("arena"));
				else
					throw new ConfigurationMissingKeysException("Events must have an arena");
			time = config.getLong("time", defTime);
			if (time < -1)
				throw new GameException("Time missing or invalid");
			rules = plugin.rulesLoader.getRules(config.getString("rules"));
			if (rules == null)
				if (defRules != null)
					rules = defRules;
				else if (config.isString("rules"))
					throw new GameException("Rules " + config.getString("rules") + "were not found");
				else
					throw new ConfigurationMissingKeysException("Missing rules");
			messages = plugin.messagesLoader.getMessages(config.getString("messages"));
			if (messages == null)
				if (defMessages != null)
					messages = defMessages;
				else if (config.isString("messages"))
					throw new GameException("Messages " + config.getString("messages") + "were not found");
				else
					throw new ConfigurationMissingKeysException("Missing messages");
			teams = new HashMap<Team, Team.Options>();
			if (config.isConfigurationSection("teams"))
				for (String s : config.getConfigurationSection("teams").getKeys(false))
					if (config.isConfigurationSection("teams." + s)) {
						ConfigurationSection cs = config.getConfigurationSection("teams." + s);
						Team team = game.teams.get(s);
						if (team == null || !s.equals(team.configName)) {
							team = null;
							for (Team t : game.teams.values())
								if (s.equals(t.configName)) {
									team = t;
									break;
								}
						}
						if (team == null) {
							team = Team.createByType(cs.getString("type"), game, cs.getString("name", s), true);
							team.configName = s;
							team.load(cs);
							teams.put(team, null);
						}
						else
							teams.put(team, team.loadOptions(team.rootOptions.getChild(), cs));
					}
		}
	}
	
	public void startEvent() throws NoEventsException {
		if (active)
			return;
		
		if (event == null || chooseNewEvent)
			chooseEvent(!sequential);
		active = true;
		plugin.activeGames.add(this);
		Bukkit.broadcastMessage("[DEBUG] Game " + name + ": next event started");
		
		for (Team team : teams.values())
			team.eventStarted();
	}
	
	public void endEvent() {
		if (!active)
			return;
		
		active = false;
		plugin.activeGames.remove(this);
		Bukkit.broadcastMessage("[DEBUG] Game " + name + ": event ended");
		chooseNewEvent = true;
		
		for (Team team : teams.values())
			team.eventEnded();
	}
	public void closeGame() {
		for (Team team : teams.values())
			team.unload();
	}
	
	public boolean isActive() {
		return active;
	}
	
	public Rules getRules() {
		if (event == null || event.rules == null)
			return defRules;
		else
			return event.rules;
	}
	public Messages getMessages() {
		if (event == null || event.messages == null)
			return defMessages;
		else
			return event.messages;
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
		if (active && !join.getRules().getBoolean(Booleans.JOIN_AFTER_START) && !player.player.hasPermission(Infection.basePerm + "joinafterstart")
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
		if (!player.team.getRules().getBoolean(Booleans.ALLOW_TEAM_CHANGE) && !player.player.hasPermission(Infection.basePerm + "changeteam")
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
	
	void chooseEvent(boolean random) throws NoEventsException {
		if (events.isEmpty())
			throw new NoEventsException();
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
		if (this.event != null) {
			for (Spawns spawns : this.event.arena.tSpawns.values())
				if (spawns.getCurrentTeam() != null && teams.containsValue(spawns.getCurrentTeam()))
					spawns.setCurrentTeam(null);
			for (Entry<Team, Team.Options> e : this.event.teams.entrySet())
				if (e.getValue() == null) {
					e.getKey().unload();
					teams.remove(e.getKey().name);
				}
		}
		if (event == null)
			return;
		this.event = event;
		for (Entry<Team, Team.Options> e : event.teams.entrySet())
			if (e.getValue() == null && !teams.containsValue(e.getKey()))
				teams.put(e.getKey().name, e.getKey());
		for (Team team : teams.values())
			team.newEvent();
	}
//	public Event getEvent() {
//		return event;
//	}
	
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