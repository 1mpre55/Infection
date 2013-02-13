package com.impress.Infection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Doesn't do anything yet. Well, technically it says when it's being enabled and disabled, but all plugins do that.
 * @author 1mpre55
 * @version 0.0.1
 */
public class Infection extends JavaPlugin {
	public static final String basePerm = "infection.";
	
	@Override
	public void onEnable() {
		getLogger().info(getName() + " enabled");
	}
	@Override
	public void onDisable() {
		getLogger().info(getName() + " disabled");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		return true;
	}
}