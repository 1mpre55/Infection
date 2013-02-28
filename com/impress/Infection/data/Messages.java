package com.impress.Infection.data;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
	
	public String pJoin,
				  pLeave,
				  pInfected,
				  bJoin,
				  bLeave,
				  bInfected,
				  bStart,
				  bEnd,
				  pbJoin,
				  pbLeave,
				  pbStart,
				  pbEnd;
	public Map<Integer, String> bEndTimer;
	
	public Messages(ConfigurationSection config) throws ReflectiveOperationException {
		load(config);
	}
	public void load(ConfigurationSection config) throws ReflectiveOperationException {
		Field key;
		for (Field field : getClass().getFields())
			if (field.getType() == String.class && Modifier.isPublic(field.getModifiers())
					&& (key = getClass().getField(field.getName() + 'O')).getType() == String.class
					&& Modifier.isStatic(key.getModifiers())) {
				field.set(this, config.getString((String)key.get(null)));
			}
		
		if (config.isConfigurationSection(bEndTimerO))
			for (String sec : config.getConfigurationSection(bEndTimerO).getKeys(false).toArray(new String[0]))
				try {
					bEndTimer.put(Integer.parseInt(sec), config.getString(bEndTimerO + '.' + sec));
				} catch (NumberFormatException e) {}
	}
}