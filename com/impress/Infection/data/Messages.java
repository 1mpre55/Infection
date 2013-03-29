package com.impress.Infection.data;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public class Messages {
	@SuppressWarnings("unused")
	private final static String pJoinO		 = "personal.join",
								pLeaveO		 = "personal.leave",
								pInfectedO	 = "personal.infected",
								bJoinO		 = "game-broadcast.join",
								bLeaveO		 = "game-broadcast.leave",
								bInfectedO	 = "game-broadcast.infected",
								bStartO		 = "game-broadcast.game-start",
								bEndO		 = "game-broadcast.game-end",
								bEndTimerO	 = "game-broadcast.game-end-timer",
								pbJoinO		 = "public-broadcast.join",
								pbLeaveO	 = "public-broadcast.leave",
								pbStartO	 = "public-broadcast.game-start",
								pbEndO		 = "public-broadcast.game-end";
	
	/**
	 * Player joined personal message
	 */
	public String pJoin;
	/**
	 * Player left personal message
	 */
	public String pLeave;
	/**
	 * Player infected personal message (for Infection games)
	 */
	public String pInfected;
	/**
	 * Player joined game-wide broadcast message
	 */
	public String bJoin;
	/**
	 * Player left game-wide broadcast message
	 */
	public String bLeave;
	/**
	 * Player infected game-wide broadcast message (for Infection games)
	 */
	public String bInfected;
	/**
	 * Game started game-wide broadcast message
	 */
	public String bStart;
	/**
	 * Game ended game-wide broadcast message
	 */
	public String bEnd;
	/**
	 * Player joined public broadcast message
	 */
	public String pbJoin;
	/**
	 * Player left public broadcast message
	 */
	public String pbLeave;
	/**
	 * Game started public broadcast message
	 */
	public String pbStart;
	/**
	 * Game ended public broadcast message
	 */
	public String pbEnd;
	
	/**
	 * Game ending game-wide broadcast messages. Key is the number of seconds until the game ends and value is the message itself.
	 */
	public Map<Integer, String> bEndTimer;
	
	/**
	 * Constructs new Messages object loading messages from <b>config</b>
	 * @param config - {@link ConfigurationSection} containing the messages
	 * @throws IllegalAccessException Reflection exception
	 * @throws NoSuchFieldException Reflection exception
	 * @throws IllegalArgumentException Reflection exception
	 * @throws SecurityException Reflection exception
	 */
	public Messages(ConfigurationSection config) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		load(config);
	}
	/**
	 * Loads the messages from <b>config</b>. This uses reflections to load the options
	 * @param config - Configuration section to load the messages from
	 * @throws IllegalAccessException Reflection exception
	 * @throws NoSuchFieldException Reflection exception
	 * @throws IllegalArgumentException Reflection exception
	 * @throws SecurityException Reflection exception
	 * @throws ReflectiveOperationException if an internal error occurs. This is most likely caused by a missing or misnamed field
	 */
	public void load(ConfigurationSection config) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException  {
		Field key;
		for (Field field : getClass().getFields())
			if (field.getType() == String.class && Modifier.isPublic(field.getModifiers())
					&& (key = getClass().getDeclaredField(field.getName() + 'O')).getType() == String.class
					&& Modifier.isStatic(key.getModifiers())) {
				field.set(this, config.getString((String)key.get(null)));
			}
		
		if (config.isConfigurationSection(bEndTimerO)) {
			bEndTimer = new HashMap<Integer, String>();
			for (String sec : config.getConfigurationSection(bEndTimerO).getKeys(false))
				try {
					bEndTimer.put(Integer.parseInt(sec), config.getString(bEndTimerO + '.' + sec));
				} catch (NumberFormatException e) {}
		}
	}
}