// This is work in progress, it contains some junk at the moment

package com.impress.Infection.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;

import com.impress.Infection.exceptions.CircularInheritanceException;
import com.impress.Infection.exceptions.ConfigurationMissingKeysException;

/**
 * Contains game rules
 * @author 1mpre55
 */
public class Rules {
	public static enum Booleans {
		FRIENDLY_FIRE("friendly-fire", true),
		JOIN_AFTER_START("allow-join-after-start", true),
		ALLOW_TEAM_CHANGE("allow-changing-team", false),
		CLEAR_INV_ON_LEAVE("clear-items-when-leaving", false),
		TEAM_ARMOR("use-team-armor", false),
		KEEP_INVENTORY("keep-inventory", false),
		REMOVE_DROPS("remove-death-drops", false),
		TEAM_COLOR_NAMETAGS("team-color-nametags", true),
		DISABLE_FALL_DAMAGE("disable-fall-damage", false);
		public final String key;
		private final boolean def;
		private Booleans(String key, boolean def) {
			this.key = key;
			this.def = def;
		}
	}
	
	private Rules parent;
	
	private final Map<String, Boolean> booleans = new HashMap<String, Boolean>();
	public boolean modified;
	
	public boolean friendlyFire;
	public boolean teamColorNametags;
	
	String name;
	
	/**
	 * Creates an empty rules container
	 * @param name
	 */
	public Rules(String name) {
		if (name == null) throw new IllegalArgumentException("Null name");
		this.name = name;
	}
	
	public boolean getBoolean(Booleans b) {
		if (booleans.containsKey(b.key))
			return booleans.get(b.key);
		else if (parent == null)
			return b.def;
		else
			return parent.getBoolean(b);
	}
	
	/**
	 * Sets parent Rules for this to inherit from.
	 * @param parent 
	 */
	public void setParent(Rules parent) throws CircularInheritanceException {
		for (Rules r = parent; r != null; r = r.parent)
			if (r == this)
				throw new CircularInheritanceException();
		this.parent = parent;
	}
	
	/**
	 * Loads the rules from <b>config</b>
	 * @param config {@link ConfigurationSection} to load the rules from. Can be null
	 * @param clear
	 * @throws ConfigurationMissingKeysException if the config is missing some required keys. Currently there aren't any.
	 */
	public void load(ConfigurationSection config, boolean clear) throws ConfigurationMissingKeysException {
		if (clear) {
			booleans.clear();
		}
		if (config != null)
			for (Booleans b : Booleans.values())
				if (config.isBoolean(b.key))
					booleans.put(b.key, config.getBoolean(b.key));
		
		modified = false;
	}
	public void init() {
		friendlyFire = getBoolean(Booleans.FRIENDLY_FIRE);
		teamColorNametags = getBoolean(Booleans.TEAM_COLOR_NAMETAGS);
	}
	/**
	 * Saves the rules to <b>config</b>
	 * @param config - {@link ConfigurationSection} to save the rules to
	 */
	public void save(ConfigurationSection config) {
		for (Entry<String, Boolean> e : booleans.entrySet())
			config.set(e.getKey(), e.getValue());
		
		modified = false;
	}
	
	/**
	 * Checks if the ConfigurationSection contains all required game rules
	 * @param config - ConfigurationSection to check
	 * @return if the section can be used to load valid root Rules
	 */
	public static boolean isRootValid(ConfigurationSection config) {
		// No game rules are required at the moment
		return true;
	}
	
	/**
	 * Creates a new Rules object that will inherit from this Rules
	 * @param newName - the name of the new copy
	 * @return the new copy
	 */
	public Rules getChild(String newName) {
		try {
			Rules child = new Rules(newName);
			child.setParent(this);
			return child;
		} catch (CircularInheritanceException e) {
			e.printStackTrace();
			return null;
		}
	}
}