package com.impress.Infection.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.impress.Infection.IPlayer;
import com.impress.Infection.Infection;

public class TagAPIListener implements Listener {
	private IPlayer p;
	@EventHandler (priority = EventPriority.LOW)
	public void onNameTag(org.kitteh.tag.PlayerReceiveNameTagEvent e) {
		if ((p = IPlayer.getIPlayer(e.getNamedPlayer())).isPlaying() && p.getTeam().colorNametag)
			e.setTag(p.getChatColor() + ChatColor.stripColor(e.getTag()));
	}
	
	public static void refreshPlayer(Player player) {
		if (Infection.tagAPI)	// TODO test if this check is useless
			org.kitteh.tag.TagAPI.refreshPlayer(player);
	}
}