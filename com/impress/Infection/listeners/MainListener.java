package com.impress.Infection.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.impress.Infection.IPlayer;
import com.impress.Infection.Infection;
import com.impress.Infection.Spectators;
import com.impress.Infection.data.Rules.Booleans;

public class MainListener implements Listener {
	Infection plugin;
	IPlayer p, p2;
	
	public MainListener(Infection plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e) {
		IPlayer.getIPlayer(e.getPlayer()).remove();
	}
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onDamage(EntityDamageEvent e) {
		if (e.getCause() == DamageCause.FALL && e.getEntity() instanceof Player &&
				(p = IPlayer.getIPlayer((Player)e.getEntity())).isPlaying() && p.getRules().getBoolean(Booleans.DISABLE_FALL_DAMAGE))
			e.setCancelled(true);
	}
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onHit(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			if (e.getDamager() instanceof Player) {
				if ((p = IPlayer.getIPlayer((Player)e.getDamager())).isPlaying() != (p2 = IPlayer.getIPlayer((Player)e.getEntity())).isPlaying())
					e.setCancelled(true);
				else if (p.isPlaying() && p2.isPlaying()) {
					if (p.getTeam() == p2.getTeam() && p.getRules().getBoolean(Booleans.FRIENDLY_FIRE))
						e.setCancelled(true);
					else if ((p.getTeam() instanceof Spectators && ((Spectators)p.getTeam()).preventDamage)
							|| p2.getTeam() instanceof Spectators && ((Spectators)p2.getTeam()).invincible)
						e.setCancelled(true);
					else if (p.getTeam().getOptions().noPvP.contains(p2.getTeam()))
						e.setCancelled(true);
				}
			}
		}
	}
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onRespawn(PlayerRespawnEvent e) {
		Location l = IPlayer.getIPlayer(e.getPlayer()).getRespawnLocation();
		if (l != null)
			e.setRespawnLocation(l);
	}
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = e.getClickedBlock();
			if (b != null && (b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN)) {
				String[] l = ((Sign)b.getState()).getLines();
				if (l[0].trim().toLowerCase().equals("[infection]")) {
					if (l[1].trim().toLowerCase().equals("join"))
						plugin.join(e.getPlayer(), new String[]{l[2], l[3]});
					else if (l[1].trim().toLowerCase().equals("leave"))
						plugin.leave(e.getPlayer(), new String[]{});
					else if (l[1].trim().toLowerCase().equals("changeteam"))
						plugin.changeTeam(e.getPlayer(), new String[]{l[2]});
					else
						e.getPlayer().sendMessage("Unknown action");
				}
			}
		}
	}
}