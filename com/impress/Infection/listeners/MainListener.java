package com.impress.Infection.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.impress.Infection.IPlayer;

public class MainListener implements Listener {
	IPlayer p;
	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e) {
		IPlayer.getIPlayer(e.getPlayer()).remove();
	}
}