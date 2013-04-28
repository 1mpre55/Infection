package com.impress.Infection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.impress.Infection.Team.Options.Strings;
import com.impress.Infection.data.Kit;
import com.impress.Infection.data.Messages;
import com.impress.Infection.data.Rules;
import com.impress.Infection.data.Spawns;
import com.impress.Infection.listeners.TagAPIListener;
import com.impress.Infection.utilities.Other;

/**
 * Represents a team
 * @author 1mpre55
 */
public class Team {
	/**
	 * The name of the team.
	 */
	public final String name;
	protected String configName = null;
	final Set<IPlayer> players = new HashSet<IPlayer>();
	final Game game;
	Options rootOptions;
	Options options;
	private Spawns spawns;
	//private Rules rules;
	//private Messages messages;
	
	/**
	 * Creates a new team.
	 * @param game - the game that this team is part of
	 * @param name - name of the team
	 */
	public Team(Game game, String name) {
		if (game == null)
			throw new IllegalArgumentException("Game cannot be null");
		if (name == null)
			throw new IllegalArgumentException("Name cannot be null");
		this.name = name;
		this.game = game;
		setCurrentOptions(rootOptions = new Options());
	}
	/**
	 * Creates a new team and loads it's options from config
	 * @param game - the game that this team is part of
	 * @param name - name of the team
	 * @param config - team's options
	 */
	public Team(Game game, String name, ConfigurationSection config) {
		this(game, name);
		load(config);
	}
	/**
	 * Loads team's options
	 * @param config - ConfigurationSection containing the options
	 */
	public void load(ConfigurationSection config) {
		loadOptions(rootOptions, config);
		setCurrentOptions(rootOptions);
	}
	public Options loadOptions(Options options, ConfigurationSection config) {
		if (options != null)
			options.load(config, true);
		return options;
	}
	void unload() {
		kickAll(false);
	}
	void eventStarted() {
		if (spawns == null) {
			List<Spawns> s = new ArrayList<Spawns>();
			for (Spawns sp : game.event.arena.tSpawns.values())
				if (sp.getCurrentTeam() == null)
					s.add(sp);
			if (s.isEmpty())
				for (Spawns sp : game.event.arena.tSpawns.values())
					s.add(sp);
			setSpawns(Other.getRandomFromList(s));
			if (spawns == null)
				;// Make a new CUBOID spawn
		}
		respawnAll();
	}
	void eventEnded() {
		respawnAll();
	}
	public void newEvent() {
		Options o = rootOptions;
		if (game.event.teams.containsKey(this))
			o = game.event.teams.get(this);
		if (o != options)
			setCurrentOptions(rootOptions);
		else
			options.init(this);
		setSpawns(game.event.arena.tSpawns.get(options.getString(Strings.SPAWNS)));
	}
	/**
	 * Sets team's options for current event
	 * @param options - new options
	 */
	public void setCurrentOptions(Options options) {
		if (options != null) {
			options.init(this);
			this.options = options;
		}
	}
	/**
	 * Sets team color. The color will revert when the next event starts. To change color permanently use {@link setColor(String)}.
	 * @param color - new color or null to remove color
	 */
	public void setColor(Color color) {
		options.color = color;
		if (color == null)
			options.cColor = ChatColor.RESET;
		else
			options.cColor = Other.colorToChatColor(color);
		
		if (Infection.tagAPI)
			for (IPlayer player : players.toArray(new IPlayer[players.size()]))
				TagAPIListener.refreshPlayer(player.player);
	}
	/**
	 * Sets team color. If color is valid, it will be set permanently in team's current options.
	 * @param color - name of the new color or null to remove color
	 */
	public void setColor(String color) {
		setColor(Other.colorFromString(color));
		if (color != null)
			options.setString(Strings.COLOR, color);
	}
	public void setSpawns(Spawns spawns) {
		if (this.spawns != null && this.spawns.getCurrentTeam() == this)
			this.spawns.setCurrentTeam(null);
		this.spawns = spawns;
		if (this.spawns != null && this.spawns.getCurrentTeam() == null)
			this.spawns.setCurrentTeam(this);
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
		for (IPlayer p : players.toArray(new IPlayer[players.size()])) {
			p.leaveGame(false, false);
			if (broadcast)
				;	// TODO
		}
	}
	void respawnAll() {
		if (game.active && spawns != null) {
			Location[] s = spawns.getUniqueSpawns(players.size());
			int c = 0;
			for (IPlayer p : players)
				p.respawn(s[c]);
		} else
			for (IPlayer p : players)
				p.respawn();
	}
	
	/**
	 * Sends a team chat message from a given player
	 * @param sender - the sender of the message
	 * @param message - the message
	 */
	public void teamChat(IPlayer sender, String message) {
		broadcast('<' + sender.player.getDisplayName() + "> " + message);	// TODO
	}
	/**
	 * Broadcasts a message to all players on the team
	 * @param message - the message
	 */
	public void broadcast(String message) {
		for (IPlayer pd : players.toArray(new IPlayer[players.size()]))
			pd.player.sendMessage(message);
	}
	void updateCompassTarget() {
		Location t = null;	// TODO
		for (IPlayer pd : players.toArray(new IPlayer[players.size()]))
			pd.setCompassTarget(t);
	}
	/**
	 * Gives a kit to all player on the team.
	 * @param kit - the kit
	 */
	public void giveKit(Kit kit) {
		if (kit == null) return;
		for (IPlayer pd : players.toArray(new IPlayer[players.size()]))
			kit.giveToPlayer(pd.player, false, false, true);
	}
	
	/**
	 * Returns {@link Rules} that apply to this team. May return null if the team
	 * does not have it's own rules and if the game did not choose an event yet
	 * @return team's rules
	 */
	public Rules getRules() {
		if (options.rules == null)
			return game.getRules();
		else
			return options.rules;
	}
	/**
	 * Returns {@link Messages} associated with this team. May return null if the team
	 * does not have it's own {@link Messages} and if the game did not choose an event yet
	 * @return team's messages
	 */
	public Messages getMessages() {
		if (options.messages == null)
			return game.getMessages();
		else
			return options.messages;
	}
	/**
	 * Returns {@link Options}
	 * @return
	 */
	public Options getOptions() {
		return options;
	}
	
	/**
	 * Returns the next spawn location for a player on the team.
	 * @return a (re)spawn location
	 */
	public Location getSpawn() {
		return spawns.getSpawn();
	}
	
	/**
	 * Creates a new team of specified type.
	 * @param type - type of the team you want to create. Use null to create a default team.
	 * @param game - the game that this team should be attached to.
	 * @param name - name of the team. Some teams may allow null names.
	 * @param defaultIfUnknownType - if true this will return default Team if <b>type</b> is unknown.
	 * @return the newly generated team or null if type is unknown.
	 */
	static Team createByType(String type, Game game, String name, boolean defaultIfUnknownType) {
		if (type == null || type.equalsIgnoreCase("default") || type.equalsIgnoreCase("regular"))
			return new Team(game, name);
		if (type.equalsIgnoreCase("spectators") || type.equalsIgnoreCase("spectator"))
			return new Spectators(game, name);
		if (type.equalsIgnoreCase("zombies") || type.equalsIgnoreCase("zombie"))
			return new Zombies(game, name);
		if (type.equalsIgnoreCase("survivors") || type.equalsIgnoreCase("survivor"))
			return new Survivors(game, name);
		if (defaultIfUnknownType)
			return new Team(game, name);
		else
			return null;
	}
	/**
	 * Contains team options.
	 */
	public static class Options {
		static interface Data<V> {
			String getKey();
			V getDef();
		}
		public static enum Booleans implements Data<Boolean> {
			//NO_FALL("disable-fall-damage", false);
			;
			public final String key;
			private final boolean def;
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
		public static enum Strings implements Data<String> {
			COLOR("color", null),
			RULES("rules", null),
			MESSAGES("messages", null),
			SPAWNS("spawns", null);
			
			public final String key;
			private final String def;
			private Strings(String key, String def) {
				this.key = key;
				this.def = def;
			}
			@Override
			public String getKey() {
				return key;
			}
			@Override
			public String getDef() {
				return def;
			}
		}
		public static enum StringLists implements Data<List<String>> {
			PREVENT_DAMAGE_TO("prevent-damage-to", new ArrayList<String>());
			
			public final String key;
			private final List<String> def;
			private StringLists(String key, List<String> def) {
				this.key = key;
				this.def = def;
			}
			@Override
			public String getKey() {
				return key;
			}
			@Override
			public List<String> getDef() {
				return def;
			}
		}
		public static enum Other implements Data<Object> {
			;
			
			public final String key;
			private final Object def;
			private Other(String key, Object def) {
				this.key = key;
				this.def = def;
			}
			@Override
			public String getKey() {
				return key;
			}
			@Override
			public Object getDef() {
				return def;
			}
		}
		
		final Map<String, Boolean> booleans = new HashMap<String, Boolean>();
		final Map<String, String> strings = new HashMap<String, String>();
		final Map<String, List<String>> stringLists = new HashMap<String, List<String>>();
		final Map<String, Object> other = new HashMap<String, Object>();
		
		private Options parent = null;
		public boolean modified;
		
		public Color color;
		public ChatColor cColor;
		Rules rules;
		Messages messages;
		public boolean colorNametag;
		public Set<Team> noPvP;
		
		public boolean getBoolean(Data<Boolean> b) {
			if (booleans.containsKey(b.getKey()))
				return booleans.get(b.getKey());
			else if (parent == null)
				return b.getDef();
			else
				return parent.getBoolean(b);
		}
		public String getString(Data<String> s) {
			if (strings.containsKey(s.getKey()))
				return strings.get(s.getKey());
			else if (parent == null)
				return s.getDef();
			else
				return parent.getString(s);
		}
		public List<String> getStringList(Data<List<String>> sl) {
			if (stringLists.containsKey(sl.getKey()))
				return stringLists.get(sl.getKey());
			else if (parent == null)
				if (sl.getDef() == null)
					return null;
				else
					return new ArrayList<String>(sl.getDef());
			else
				return parent.getStringList(sl);
		}
		public Object getOther(Data<?> o) {
			if (other.containsKey(o.getKey()))
				return other.get(o.getKey());
			else if (parent == null)
				return o.getDef();
			else
				return parent.getOther(o); 
		}
		
		public void setBoolean(Data<Boolean> b, boolean value) {
			booleans.put(b.getKey(), value);
		}
		public void setString(Data<String> s, String value) {
			strings.put(s.getKey(), value);
		}
		public void setStringList(Data<List<String>> l, List<String> value) {
			stringLists.put(l.getKey(), value);
		}
		
		Options load(ConfigurationSection config, boolean clear) {
			if (clear) {
				booleans.clear();
				strings.clear();
				stringLists.clear();
				other.clear();
			}
			if (config != null) {
				for (Booleans b : Booleans.values())
					if (config.isBoolean(b.key))
						setBoolean(b, config.getBoolean(b.key));
				for (Strings s : Strings.values())
					if (config.isString(s.key))
						setString(s, config.getString(s.key));
				for (StringLists l : StringLists.values())
					if (config.isList(l.key))
						setStringList(l, config.getStringList(l.key));
			}
			modified = false;
			return this;
		}
		Options init(Team team) {
			color = com.impress.Infection.utilities.Other.colorFromString(getString(Strings.COLOR));
			if (color == null)
				cColor = ChatColor.RESET;
			else
				cColor = com.impress.Infection.utilities.Other.colorToChatColor(color);
			rules = team.game.plugin.rulesLoader.getRules(getString(Strings.RULES));
			messages = team.game.plugin.messagesLoader.getMessages(getString(Strings.MESSAGES));
			noPvP = new HashSet<Team>();
			for (String s : getStringList(StringLists.PREVENT_DAMAGE_TO)) {
				Team t = team.game.teams.get(s);
				if (t != null)
					noPvP.add(t);
			}
			return this;
		}
		void save(ConfigurationSection config) {
			
		}
		/**
		 * @return new Options that inherit from this
		 */
		public Options getChild() {
			Options child = new Options();
			child.parent = this;
			return child;
		}
	}
}