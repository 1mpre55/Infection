package com.impress.Infection.config;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.impress.Infection.data.Messages;

public class MessagesLoader extends ConfigManager {
	private final Logger log;
	private final HashMap<String, Messages> messages = new HashMap<String, Messages>();
	private boolean modified = false;
	
	public MessagesLoader(JavaPlugin plugin, String fileName, Logger logger) {
		super(plugin, fileName);
		log = (logger == null)? Logger.getLogger("Minecraft") : logger;
	}
	
	public Messages getMessages(String name) {
		return messages.get(name);
	}
	void addMessages(String name, Messages messages) {
		if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Invalid messages name");
		if (messages == null) throw new IllegalArgumentException("Null messages");
		this.messages.put(name, messages);
		modified = true;
	}
	void deleteMessages(String name) {
		messages.remove(name);
		modified = true;
	}
	public HashMap<String, Messages> getAllMessages() {
		return messages;
	}
	
	@Override
	public void load() {
		FileConfiguration config = getConfig();
		try {
			for (String name : config.getKeys(false).toArray(new String[0]))
				messages.put(name, new Messages(getConfigurationSection(config, name)));
		} catch (Exception e) {
			log.warning("Internal error occured while loading messages");
			e.printStackTrace();
		}
	}
	@Override
	public void save() {
		save(false);
	}
	public void save(boolean saveIfUnchanged) {
		if (!modified && !saveIfUnchanged) return;
		// TODO
		modified = false;
	}
}