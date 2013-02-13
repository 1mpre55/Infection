// This is work in progress, it contains some junk at the moment

package com.impress.Infection.data;

import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import com.impress.Infection.exceptions.ConfigurationMissingKeysException;

/**
 * Contains game rules
 * @author 1mpre55
 */
public class Rules implements Cloneable {
	
	public static enum Keys {
		FRIENDLYFIRE("friendly-fire");
		
		private Keys(String key) {}
		
	}
	
	public static final String parentO			 = "parent",
							   friendlyFireO	 = "friendly-fire",
							   timeLimitO		 = "time-limit";
	
	/**This will be used as default values for any rules that were not specified*/
	private Rules parent;
	private Set<String> keys;
	public boolean modified;
	
	boolean friendlyFire;
	long timeLimit;
	
	String name;
	public Rules(String name) {
		if (name == null) throw new IllegalArgumentException("Null name");
		this.name = name;
	}
	
	public boolean load(ConfigurationSection config) throws ConfigurationMissingKeysException {
		keys = config.getKeys(true);
		
		friendlyFire = config.getBoolean(friendlyFireO, true);
		timeLimit = config.getLong(friendlyFireO, -1);
		
		modified = false;
		return false;
	}
	public void save(ConfigurationSection config) {
		// TODO
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
	 * Creates a copy of these Rules with a new name
	 * @param newName - the name of the new copy
	 * @return the new copy
	 */
	public Rules clone(String newName) {
		Rules result;
		try {
			result = (Rules)clone();
			result.name = newName;
			return result;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null; // TODO test
	}
}