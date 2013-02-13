package com.impress.Infection.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.impress.Infection.utilities.TextTools;

public abstract class ConfigManager {
	protected JavaPlugin plugin;
	protected String fileName;
	protected File configFile;
	protected FileConfiguration fileConfiguration;
	protected boolean loaded = false;
	
	public ConfigManager(JavaPlugin plugin, String fileName) {
		if (plugin == null)
			throw new IllegalArgumentException("plugin cannot be null");
		if (!plugin.isInitialized())
			throw new IllegalArgumentException("plugin must be initiaized");
		this.plugin = plugin;
		this.fileName = fileName;
		configFile = new File(plugin.getDataFolder(), this.fileName);
	}
	
	abstract public void load();
	abstract public void save();
	
	public boolean isLoaded() {
		return loaded;
	}
	
	FileConfiguration getConfig() {
		if (fileConfiguration == null)
			reloadYaml();
		return fileConfiguration;
	}
	void reloadYaml() {
		if (configFile == null) {
			File dataFolder = plugin.getDataFolder();
			if (dataFolder == null)
				throw new IllegalStateException();
			configFile = new File(dataFolder, fileName);
		}
		fileConfiguration = YamlConfiguration.loadConfiguration(configFile);

		// Look for defaults in the jar
		InputStream defConfigStream = plugin.getResource(fileName);
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			fileConfiguration.setDefaults(defConfig);
		}
	}
	void saveYaml() {
		if (fileConfiguration == null || configFile == null)
			return;
		else
			try {
				getConfig().save(configFile);
			} catch (IOException ex) {
				plugin.getLogger().severe("Could not save " + configFile.getName());
			}
	}
	public void saveDefaultYaml() {
		if (!configFile.exists())
			plugin.saveResource(fileName, false);
	}
	
	/**
	 * Returns a {@link ConfigurationSection} creating a new one if it doesn't exist yet.
	 * @param parent - parent ConfigurationSection.
	 * @param section - name of the section to return.
	 * @return requested ConfigurationSection.
	 */
	protected static ConfigurationSection getConfigurationSection(ConfigurationSection parent, String section) {
		if (parent.isConfigurationSection(section))
			return parent.getConfigurationSection(section);
		else
			return parent.createSection(section);
	}
	/**
	 * Clears out the entire ConfigurationSection.
	 * @param config - Configuration section to clear.
	 * @param deep - whether or not we should clear and remove sections inside the given one. Should be true if
	 * you plan to remove the <b>config</b> from it's root later
	 */
	protected static void clearConfigurationSection(ConfigurationSection config, boolean deep) {
		List<String> ke = TextTools.orderByOccurrence(config.getKeys(deep).toArray(new String[0]), '.');
		Collections.reverse(ke);
		for (String key : ke.toArray(new String[0]))
			config.set(key, null);
	}
}