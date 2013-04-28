package com.impress.Infection.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.impress.Infection.data.Rules;
import com.impress.Infection.exceptions.CircularInheritanceException;
import com.impress.Infection.exceptions.ConfigurationMismatchException;
import com.impress.Infection.exceptions.ConfigurationMissingKeysException;
import com.impress.Infection.exceptions.GameException;

public class RulesLoader extends ConfigManager {
	private final Logger log;
	private final static HashMap<String, Rules> rules = new HashMap<String, Rules>();
	private boolean modified = false;
	
	public RulesLoader(JavaPlugin plugin, String fileName, Logger logger) {
		super(plugin, fileName);
		log = (logger == null)? Logger.getLogger("Minecraft") : logger;
	}
	
	public Rules getRules(String name) {
		return rules.get(name);
	}
	void addRules(String name, Rules rules) {
		if (name == null || name.trim().isEmpty())
			throw new IllegalArgumentException("Invalid rules name");
		if (rules == null)
			throw new IllegalArgumentException("Null rules");
		RulesLoader.rules.put(name, rules);
		modified = true;
	}
	void deleteRules(String name) {
		rules.remove(name);
		modified = true;
	}
	public HashMap<String, Rules> getAllRules() {
		return rules;
	}
	
	@Override
	public void load() {
		FileConfiguration config = getConfig();
		Set<String> rulesNames = config.getKeys(false);
		List<String> parentStack = new ArrayList<String>();
		for (String rulesName : rulesNames.toArray(new String[rulesNames.size()]))
			if (!rules.containsKey(rulesName))
				try {
					loadRules(config, rulesName, parentStack);
				} catch (GameException e) {
					log.warning(e.getMessage() + " Rules " + rulesName + " failed to load: " + e.getMessage());
					parentStack.clear();
				}
		modified = false;
		loaded = true;
	}
	private void loadRules(ConfigurationSection config, String rulesName, List<String> parentStack) throws GameException {
		// Check inheritance loops
		if (parentStack != null) {
			if (parentStack.contains(rulesName))
				throw new CircularInheritanceException("Inheritance loop in rules configuration.");
			parentStack.add(rulesName);
		}
		
		// Check if the rules exist
		ConfigurationSection rulesConfig = config.getConfigurationSection(rulesName);
		if (rulesConfig == null)
			throw new ConfigurationMismatchException("Rules " + rulesName + " are invalid.");
		Rules rls = null;
		
		// Should we inherit from other rules
		String parent = rulesConfig.getString("parent");
		if (parent != null && parentStack != null) {
			if (!rules.containsKey(parent))
				loadRules(config, parent, parentStack);
			rls = rules.get(parent).getChild(rulesName);
		}
		if (rls == null) {
			if (!Rules.isRootValid(rulesConfig))
				throw new ConfigurationMissingKeysException("Root rules " + rulesName + " is missing one or more keys.");
			rls = new Rules(rulesName);
		}
		rls.load(rulesConfig, true);
		rls.init();
		rules.put(rulesName, rls);
		parentStack.remove(rulesName);
	}
	@Override
	public void save() {
		save(false);
	}
	public void save(boolean saveIfUnchanged) {
		if (!modified && !saveIfUnchanged)
			return;
		clearConfigurationSection(getConfig(), true);
		for (Entry<String, Rules> e : rules.entrySet()) {
			if (e.getValue() == null)
				continue;
			ConfigurationSection cs = getConfigurationSection(getConfig(), e.getKey());
			clearConfigurationSection(cs, true);
			e.getValue().save(cs);
		}
		modified = false;
	}
}