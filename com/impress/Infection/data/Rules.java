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
	@SuppressWarnings("unused")
	private static final String parentO			 = "parent",
							   friendlyFireO	 = "friendly-fire",
							   timeLimitO		 = "time-limit",
							   joinAfterStartO	 = "allow-join-after-start",
							   allowTeamChangeO	 = "allow-changing-team",
							   clearInvOnLeaveO	 = "clear-items-when-leaving",
							   teamArmorO		 = "use-team-armor",
							   keepInventoryO	 = "keep-inventory",
							   removeDropsO		 = "remove-death-drops",
							   teamColorNametagO = "team-color-nametags";
	
	/**This will be used as default values for any rules that were not specified*/
	private Set<String> keys;
	public boolean modified;
	
	public boolean friendlyFire;
	public long timeLimit;
	public boolean joinAfterStart;
	public boolean allowTeamChange;
	public boolean clearInvOnLeave;
	public boolean teamArmor;
	public boolean keepInventory;
	public boolean removeDrops;
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
	
	/**
	 * Loads the rules from <b>config</b>
	 * @param config {@link ConfigurationSection} to load the rules from
	 * @throws ConfigurationMissingKeysException if the config is missing some required keys. Currently there aren't any.
	 */
	public void load(ConfigurationSection config) throws ConfigurationMissingKeysException {
		keys = config.getKeys(true);
		
		friendlyFire = config.getBoolean(friendlyFireO, true);
		timeLimit = config.getLong(timeLimitO, -1);
		joinAfterStart = config.getBoolean(joinAfterStartO, true);
		allowTeamChange = config.getBoolean(allowTeamChangeO, true);
		clearInvOnLeave = config.getBoolean(clearInvOnLeaveO, false);
		teamArmor = config.getBoolean(teamArmorO, false);
		keepInventory = config.getBoolean(keepInventoryO, false);
		removeDrops = config.getBoolean(removeDropsO, false);
		teamColorNametags = config.getBoolean(teamColorNametagO, false);
		
		modified = false;
	}
	/**
	 * Saves the rules to <b>config</b>
	 * @param config - {@link ConfigurationSection} to save the rules to
	 */
	public void save(ConfigurationSection config) {
		if (keys.contains(friendlyFireO))
			config.set(friendlyFireO, friendlyFire);
		if (keys.contains(timeLimitO))
			config.set(timeLimitO, timeLimit);
		
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