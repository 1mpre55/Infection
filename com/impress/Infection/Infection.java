package com.impress.Infection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.impress.Infection.config.ArenaLoader;
import com.impress.Infection.config.GamesLoader;
import com.impress.Infection.config.KitLoader;
import com.impress.Infection.config.MessagesLoader;
import com.impress.Infection.config.RulesLoader;
import com.impress.Infection.data.Kit;
import com.impress.Infection.data.Messages;
import com.impress.Infection.exceptions.AlreadyPlayingException;
import com.impress.Infection.exceptions.GameAlreadyStartedException;
import com.impress.Infection.exceptions.GameException;
import com.impress.Infection.exceptions.GameFullException;
import com.impress.Infection.exceptions.NoPermissionException;
import com.impress.Infection.exceptions.TeamNotFoundException;
import com.impress.Infection.listeners.MainListener;
import com.impress.Infection.listeners.TagAPIListener;
import com.impress.Infection.utilities.TextTools;

/**
 * Doesn't do much yet. Just loads options from files.
 * @author 1mpre55
 * @version 0.0.2
 */
public class Infection extends JavaPlugin {
	/**
	 * The base permission node including the '.' at the end
	 */
	public static final String basePerm = "infection.";
	/**
	 * Whether debug messages should be printed or not
	 */
	public static boolean debug = false;
	/**
	 * Indicates whether the external plugin with the same name is loaded and should be used if needed.
	 */
	public static boolean tagAPI, disguiseCraft, mobDisguise;
	
	Map<String, Game> games;
	List<Game> activeGames;
	Game mainGame;
	
	GamesLoader gamesLoader;
	KitLoader kitLoader;
	ArenaLoader arenaLoader;
	RulesLoader rulesLoader;
	MessagesLoader messagesLoader;
	Messages defaultMessages;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		FileConfiguration config = getConfig();
		
		kitLoader = new KitLoader(this, "kits.yml", getLogger());
		kitLoader.saveDefaultYaml();
		messagesLoader = new MessagesLoader(this, "lang.yml", getLogger());
		messagesLoader.saveDefaultYaml();
		arenaLoader = new ArenaLoader(this, "arenas.yml", getLogger());
		arenaLoader.saveDefaultYaml();
		rulesLoader = new RulesLoader(this, "rules.yml", getLogger());
		rulesLoader.saveDefaultYaml();
		gamesLoader = new GamesLoader(this, "games.yml", getLogger());
		gamesLoader.saveDefaultYaml();
		kitLoader.load();
		messagesLoader.load();
		arenaLoader.load();
		rulesLoader.load();
		gamesLoader.load();
		
		defaultMessages = messagesLoader.getMessages(config.getString("default-messages"));
		
		PluginManager pm = getServer().getPluginManager();
		tagAPI = pm.isPluginEnabled("TagAPI");
		disguiseCraft = pm.isPluginEnabled("DisguiseCraft");
		if (!disguiseCraft)
			mobDisguise = pm.isPluginEnabled("MobDisguise");
		
		pm.registerEvents(new MainListener(), this);
		if (tagAPI)
			pm.registerEvents(new TagAPIListener(), this);
		
		games = new HashMap<String, Game>(8);
		activeGames = new ArrayList<Game>();
		
		getLogger().info(getName() + " enabled");
	}
	@Override
	public void onDisable() {
		kitLoader.save();
		messagesLoader.save();
		arenaLoader.save();
		rulesLoader.save();
		gamesLoader.save();
		HandlerList.unregisterAll(this);
		getServer().getScheduler().cancelTasks(this);
		getLogger().info(getName() + " disabled");
	}
	
	// TODO completely re-design the commands
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equals("infection")) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("kit")) {
					return kitCommand(sender, Arrays.copyOfRange(args, 1, args.length));
				}
				// Debug armor
				if (args[0].equalsIgnoreCase("armor")) {
					if (!(sender instanceof Player)) sender.sendMessage("must be a player");
					else if (args.length < 2) sender.sendMessage("please specify color");
					else {
						Material material;
						args[2] = args[2].toLowerCase();
						switch(args[2]) {
						case "chestplate": material = Material.LEATHER_CHESTPLATE; break;
						case "helmet": material = Material.LEATHER_HELMET; break;
						case "boots": material = Material.LEATHER_BOOTS; break;
						case "leggings": material = Material.LEATHER_LEGGINGS; break;
						default: sender.sendMessage("Unknown armor type"); return true;
						}
						Color color;
						args[1] = args[1].toLowerCase();
						switch(args[1]) {
						case "red": color = Color.RED; break;
						case "blue": color = Color.BLUE; break;
						case "black": color = Color.BLACK; break;
						case "gray": color = Color.GRAY; break;
						case "green": color = Color.GREEN; break;
						case "orange": color = Color.ORANGE; break;
						case "yellow": color = Color.YELLOW; break;
						case "purple": color = Color.PURPLE; break;
						case "white": color = Color.WHITE; break;
						default: color = Color.SILVER;
						}
						LeatherArmorMeta lim = (LeatherArmorMeta)getServer().getItemFactory().getItemMeta(material);
						lim.setColor(color);
						ItemStack item = new ItemStack(material);
						((Player)sender).getInventory().addItem(item);
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("join")) {
					return join(sender, Arrays.copyOfRange(args, 1, args.length));
				}
				if (args[0].equalsIgnoreCase("leave")) {
					return leave(sender, Arrays.copyOfRange(args, 1, args.length));
				}
				if (args[0].equalsIgnoreCase("changeteam")) {
					return changeTeam(sender, Arrays.copyOfRange(args, 1, args.length));
				}
				if (args[0].equalsIgnoreCase("admin")) {
					return admin(sender, Arrays.copyOfRange(args, 1, args.length));
				}
//				if (args[0].equalsIgnoreCase("yell")) {
//					return yell(sender, Arrays.copyOfRange(args, 1, args.length));
//				}
//				if (args[0].equalsIgnoreCase("stats")) {
//					return stats(sender, Arrays.copyOfRange(args, 1, args.length));
//				}
				return true;
			} else
				// TODO make a help page
				return false;
		}
		else if (command.getName().equals("infjoin"))
			return join(sender, args);
		else if (command.getName().equals("infleave"))
			return leave(sender, args);
		else if (command.getName().equalsIgnoreCase("infchangeteam"))
			return changeTeam(sender, args);
		else if (command.getName().equalsIgnoreCase("infadmin"))
			return admin(sender, args);
//		else if (command.getName().equals("infyell"))
//			return yell(sender, args);
//		else if (command.getName().equalsIgnoreCase("infstats"))
//			return stats(sender, args);
		else
			return false;
	}
	private boolean kitCommand(CommandSender sender, String[] args) {
		if (args.length == 1 && args[1].equalsIgnoreCase("list")) {
			if (sender.hasPermission(basePerm + "kits.list"))
				sender.sendMessage(TextTools.separateWithCommas(kitLoader.getKits().keySet()));
			else
				sender.sendMessage("You don't have permission to do this");
		}
		else if (args.length == 2 && args[1].equalsIgnoreCase("see")) {
			if (sender instanceof Player) {
				if (sender.hasPermission(basePerm + "kits.see") || sender.hasPermission(basePerm + "kits.see." + args[2])) {
					Kit kit = kitLoader.getKit(args[2]);
					if (kit == null)
						sender.sendMessage("kit not found");
					else
						kit.giveToPlayer((Player)sender, false, true, false);
				} else
					sender.sendMessage("You don't have permission to do this");
			} else
				sender.sendMessage("Only players can use this");
		}
		else if (args.length == 2 && args[1].equalsIgnoreCase("save")) {
			if (sender instanceof Player) {
				if (sender.hasPermission(basePerm + "kits.save") || sender.hasPermission(basePerm + "kits.save." + args[2])) {
					Kit kit = new Kit();
					kit.fromPlayer((Player)sender, true);
					if (kit.isEmpty())
						sender.sendMessage("Can't save empty kit");
					else {
						kitLoader.addKit(args[2], kit);
						sender.sendMessage("Kit saved");
					}
				} else
					sender.sendMessage("You don't have permission to do this");
			} else
				sender.sendMessage("Only players can use this");
		}
		else if (args.length == 2 && args[1].equalsIgnoreCase("delete")) {
			if (sender.hasPermission(basePerm + "kits.delete") || sender.hasPermission(basePerm + "kits.delete." + args[2])) {
				if (kitLoader.getKits().containsKey(args[2])) {
					kitLoader.deleteKit(args[2]);
					sender.sendMessage("Kit deleted");
				} else
					sender.sendMessage("Kit not found");
			} else
				sender.sendMessage("You don't have permission to do this");
		}
		else
			sender.sendMessage(ChatColor.RED + "list" + ChatColor.RESET + ": lists all available kits.\n" +
				   ChatColor.RED + "see" + ChatColor.BLUE + " <kit>" + ChatColor.RESET +
				   			": gives you a specific kit. Your current items will not be cleared.\n" +
				   ChatColor.RED + "save" + ChatColor.BLUE + " <kit>" + ChatColor.RESET +
				   			": saves your inventory as a new kit with a given name. All items will be saved in their current slots.\n" +
				   ChatColor.RED + "delete" + ChatColor.BLUE + " <kit>" + ChatColor.RESET + ": deletes the kit");
		return true;
	}
	private boolean join(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Game game = null;
			if (args.length > 0) {
				game = games.get(args[0].trim());
				if (game == null) {
					sender.sendMessage("Game " + args[0] + " was not found");
					return true;
				}
			} else
				if (mainGame != null)
					game = mainGame;
				else {
					sender.sendMessage("You must specify a game");
					return true;
				}
			
			try {
				if (args.length > 1)
					game.playerJoin(IPlayer.getIPlayer((Player)sender), args[1]);
				else
					game.playerJoin(IPlayer.getIPlayer((Player)sender), null);
			} catch (TeamNotFoundException | AlreadyPlayingException e) {
				sender.sendMessage(e.getMessage());
			} catch (GameFullException e) {
				sender.sendMessage("Game full");
			} catch (GameAlreadyStartedException e) {
				sender.sendMessage("Game already started, can't join");
			} catch (NoPermissionException e) {
				if (e.getMessage() != null)
					sender.sendMessage(e.getMessage());
				else
					sender.sendMessage("You don't have permission to do that");
				if (debug)
					getLogger().info("[DEBUG] " + sender.getName() + " needs '" + e.getPermission() + "' permission to perform that action");
			} catch (GameException e) {
				sender.sendMessage("Unexpected error occured");
				getLogger().warning("Player failed to join " + game.name + ": " + e.getMessage());
			}
		} else sender.sendMessage("Only players can join games");
		return true;
	}
	private boolean leave(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			IPlayer player = IPlayer.getIPlayer((Player)sender);
			if (!player.isPlaying()) {
				sender.sendMessage("You're not playing");
				return true;
			}
			player.getGame().playerLeave(IPlayer.getIPlayer((Player)sender));
		} else sender.sendMessage("Only players can leave games");
		return true;
	}
	private boolean changeTeam(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			IPlayer player = IPlayer.getIPlayer((Player)sender);
			if (!player.isPlaying()) {
				sender.sendMessage("You're not playing");
				return true;
			}
			try {
				if (args.length > 0)
					player.getGame().playerChangeTeam(player, args[0]);
				else
					player.getGame().playerChangeTeam(player, null);
			} catch (TeamNotFoundException e) {
				sender.sendMessage(e.getMessage());
			} catch (NoPermissionException e) {
				if (e.getMessage() != null)
					sender.sendMessage(e.getMessage());
				else
					sender.sendMessage("You don't have permission to do that");
				// TODO debug
				getLogger().info("[DEBUG] " + sender.getName() + " needs '" + e.getPermission() + "' permission to perform that action");
			}
		} else sender.sendMessage("Only players can switch teams");
		return true;
	}
	private boolean admin(CommandSender sender, String[] args) {
		// Could use a switch(String) statement when don't need to support Java 6 anymore
		final String help = ChatColor.DARK_RED + "game " + ChatColor.RED + "<args> " + ChatColor.BLUE + "- commands related to games";
		final String gameHelp = ChatColor.DARK_RED + "game load " + ChatColor.RED + "<game> " + ChatColor.BLUE + "- load the game\n" +
								ChatColor.DARK_RED + "game start " + ChatColor.RED + "<game> " + ChatColor.BLUE + "- start game's next event\n" +
								ChatColor.DARK_RED + "game end " + ChatColor.RED + "<game> " + ChatColor.BLUE + "- end game's current event\n" +
								ChatColor.DARK_RED + "game unload " + ChatColor.RED + "<game> " + ChatColor.BLUE + "- unload the game ending it's current event\n" +
								ChatColor.DARK_RED + "game list " + ChatColor.BLUE + "- list all loaded games";
		if (args.length < 1)
			sender.sendMessage(help);
		else if (args[0].equalsIgnoreCase("game")) {
			Game g;
			if (args.length < 2)
				sender.sendMessage(gameHelp);
			else if (args.length > 2 && args[1].equalsIgnoreCase("load")) {
				if ((g = gamesLoader.getGame(args[2])) != null) {
					games.put(g.name, g);
					sender.sendMessage("Game " + g.name + " was loaded");
				} else
					sender.sendMessage("Game " + args[2] + " was not found");
			} else if (args.length > 2 && args[1].equalsIgnoreCase("start")) {
				if ((g = findGame(sender, args[2])) != null)
					g.startEvent();
			} else if (args.length > 2 && args[1].equalsIgnoreCase("end")) {
				if ((g = findGame(sender, args[2])) != null)
					g.endEvent();
			} else if (args[1].equalsIgnoreCase("unload")) {
				if ((g = findGame(sender, args[2])) != null) {
					games.remove(g.name);
					sender.sendMessage("Game " + g.name + " was unloaded");
				}
			} else if (args[1].equalsIgnoreCase("list")) {
				if (gamesLoader.getAllGames().isEmpty())
					sender.sendMessage("No games found");
				else
					sender.sendMessage(TextTools.separateWithCommas(gamesLoader.getAllGames().keySet()));
			} else
				sender.sendMessage(gameHelp);
		} else
			sender.sendMessage(help);
		return true;
	}
	private Game findGame(CommandSender sender, String game) {
		Game g = games.get(game);
		if (g == null) {
			if (sender != null)
				if (sender.hasPermission(basePerm + "seeunloadedgames") && gamesLoader.getAllGames().containsKey(game))
					sender.sendMessage("Game " + game + " is not loaded");
				else
					sender.sendMessage("Game " + game + " was not found");
			return null;
		} else
			return g;
	}
}