package com.impress.Infection.config;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import com.impress.Infection.Infection;
import com.impress.Infection.data.Kit;
import com.impress.Infection.exceptions.ConfigurationMismatchException;
import com.impress.Infection.utilities.InvTools;

public class KitLoader extends ConfigManager {
	private final Logger log;
	private final HashMap<String, Kit> kits = new HashMap<String, Kit>();
	private boolean modified = false;
	
	public KitLoader(Infection plugin, String fileName, Logger logger) {
		super(plugin, fileName);
		log = (logger == null)? Logger.getLogger("Minecraft") : logger;
	}
	
	public Kit getKit(String name) {
		return kits.get(name);
	}
	public void addKit(String name, Kit kit) {
		if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Invalid kit name");
		if (kit == null) throw new IllegalArgumentException("Null kit");
		kits.put(name, kit);
		modified = true;
	}
	public void deleteKit(String name) {
		if (kits.remove(name) != null)
			modified = true;
	}
	public HashMap<String, Kit> getKits() {
		return kits;
	}
	
	@Override
	public void load() {
		FileConfiguration config = getConfig();
		Set<String> kitNames = config.getKeys(false);
		for (String kitName : kitNames.toArray(new String[0]))
			try {
				loadKit(config.getConfigurationSection(kitName), kitName);
			} catch (ConfigurationMismatchException e) {
				log.warning(e.getMessage());
			}
		modified = false;
		loaded = true;
	}
	private void loadKit(ConfigurationSection config, String kitName) throws ConfigurationMismatchException {
		// Check if the kit exists
		if (config == null) throw new ConfigurationMismatchException("Kit " + kitName + " is invalid.");
		
		String[] contents = config.getKeys(false).toArray(new String[0]);
		Kit kit = new Kit();
		kit.name = kitName;
		
		for (String key : contents) {
			ItemStack item = InvTools.loadItem(getConfigurationSection(config, key));
			if (item == null) continue;
			if (key.equals("hand"))
				kit.itemInHand = item;
			else
				try {
					int i = Integer.parseInt(key);
					if (i >= 0 && i < 40) {
						if (kit.inventory.put(i, item) != null)
							throw new ConfigurationMismatchException("Kit " + kitName + " contains duplicate inventory slot IDs");
					} else
						kit.otherItems.put(key, item);
				} catch (NumberFormatException e) {
					kit.otherItems.put(key, item);
				}
		}
		kits.put(kitName, kit);
	}
	@Override
	public void save() {
		save(false);
	}
	public void save(boolean saveIfUnchanged) {
		if (!modified && !saveIfUnchanged) return;
		FileConfiguration config = getConfig();
		clearConfigurationSection(config, true);
		String[] kitNames = kits.keySet().toArray(new String[0]);
		for (String name : kitNames)
			saveKit(getConfigurationSection(config, name), name);
		saveYaml();
		modified = false;
	}
	private void saveKit(ConfigurationSection config, String kitName) {
		if (config == null) throw new IllegalArgumentException("Null config section for kit " + kitName);
		Kit kit = kits.get(kitName);
		if (kit == null) throw new NoSuchElementException(kitName);
		
		if (kit.itemInHand != null)
			InvTools.saveItem(getConfigurationSection(config, "hand"), kit.itemInHand);
		for (Integer index : kit.inventory.keySet().toArray(new Integer[0]))	// TODO int
			InvTools.saveItem(getConfigurationSection(config, index.toString()), kit.inventory.get(index));
		for (String item : kit.otherItems.keySet().toArray(new String[0]))
			InvTools.saveItem(getConfigurationSection(config, item), kit.otherItems.get(item));
		
//		for (String key : config.getKeys(false).toArray(new String[0]))
//			config.set(key + ".==", null);
	}
}