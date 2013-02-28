package com.impress.Infection.utilities;

import org.bukkit.entity.Player;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

public class DisguiseCraftBridge {
	private static DisguiseCraftAPI api;
	private static DisguiseCraftAPI getAPI() {
		if (api == null)
			api = DisguiseCraft.getAPI();
		return api;
	}
	
	public static void infect(Player player) {
		getAPI().disguisePlayer(player, new Disguise(api.newEntityID(), DisguiseType.Zombie));
	}
	public static void undisguise(Player player) {
		getAPI().undisguisePlayer(player);
	}
}