package com.impress.Infection.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * A few tools for managing inventories
 * @author 1mpre55
 */
public class InvTools {
	/**
	 * Calculates the number of empty slots in an inventory.
	 * @param inv - the inventory to check
	 * @return the number of empty slots
	 */
	public static int emptySlots(Inventory inv) {
		int result = 0;
		
		for (ItemStack item : inv.getContents())
			if (item == null) result++;
		
		return result;
	}
	
	private final static String type = "type",
								amount = "amount",
								damage = "damage",
								data = "data",
								enchs = "enchantments";
	/**
	 * Loads an {@link ItemStack} from a given ConfigurationSection
	 * @param config - {@link ConfigurationSection} to load item's data from
	 * @return the resulting ItemStack or null if config is missing "type" key or if the type was not recognized
	 */
	public static ItemStack loadItem(ConfigurationSection config) {
		if (config == null) throw new NullPointerException("null config");
		Material m;
		if (config.isInt(type))
			m = Material.getMaterial(config.getInt(type));
		else
			m = Material.matchMaterial(config.getString(type).trim());
		if (m == null) return null;
		ItemStack result = new ItemStack(m);
		int i;
		if (config.isInt(amount) && (i = config.getInt(amount)) > 0)
			result.setAmount(i);
		if (config.isInt(damage) && (i = config.getInt(damage)) > Short.MIN_VALUE && i < Short.MAX_VALUE)
			result.setDurability((short)i);
		if (config.isInt(data) && (i = config.getInt(data)) > Byte.MIN_VALUE && i < Byte.MAX_VALUE)
			result.setData(new MaterialData(result.getTypeId(), (byte)i));
		if (config.isList(enchs) && config.getStringList(enchs) != null) {
			String[] enchantments = config.getStringList(enchs).toArray(new String[0]);
			String[] e;
			Enchantment en;
			int level;
			for (String ench : enchantments) {
				e = ench.split(":", 2);
				e[0] = e[0].trim();
				try {
					en = Enchantment.getById(Integer.parseInt(e[0]));
				} catch (NumberFormatException ex) {
					en = Enchantment.getByName(e[0]);
				}
				if (en == null) continue;
				try {
					level = Integer.parseInt(e[1].trim());
				} catch (NumberFormatException ex) {
					continue;
				}
				result.addUnsafeEnchantment(en, level);
			}
		}
		return result;
	}
	/**
	 * Saves the ItemStack to a {@link ConfigurationSection}. This is more user-friendly than ConfigurationSection's
	 * native method because it doesn't save the "==" key.
	 * @param config - ItemStack's section it's data should be saved to.
	 * @param item - the ItemStack to be saved
	 */
	public static void saveItem(ConfigurationSection config, ItemStack item) {
		if (config == null || item == null) throw new IllegalArgumentException();
		config.set(type, item.getType().toString());
		int i;
		config.set(amount, (i = item.getAmount()) > 1 ? i : null);
		config.set(damage, (i = item.getDurability()) == 0 ? null : i);
		if (item.getEnchantments().isEmpty())
			config.set(enchs, null);
		else {
			List<String> enchantments = new ArrayList<String>();
			Map<Enchantment,Integer> e = item.getEnchantments();
			for (Enchantment ench : item.getEnchantments().keySet())
				enchantments.add(ench.getName() + ':' + e.get(ench));
			config.set(enchs, enchantments);
		}
	}
	
	/**
	 * Scans the inventory for disallowed items, optionally removing them.
	 * @param inv - PlayerInventory to scan through.
	 * @param filter - List of item IDs.
	 * @param isBlacklist - if true, filter is considered to be a blacklist, otherwise it's considered to be a whitelist.
	 * @param remove - if true all disallowed items will be removed.
	 * @return true if disallowed items were found, false otherwise.
	 */
	public static boolean itemFilter(PlayerInventory inv, List<Integer> filter, boolean isBlacklist, boolean remove) {
		boolean result = false;
		for (int i = 0; i < 40; i++)
			if (isBlacklist == filter.contains(inv.getItem(i).getTypeId()))
				if (remove) {
					inv.clear(i);
					result = true;
				} else return true;
		return result;
	}
	/**
	 * Scans the inventory for items with disallowed enchantments, optionally removing the items.
	 * @param inv - PlayerInventory to scan through.
	 * @param filter - List of enchantments.
	 * @param isBlacklist - if true, filter is considered to be a blacklist, otherwise it's considered to be a whitelist.
	 * @param remove - if true all disallowed items will be removed.
	 * @return true if disallowed enchantments were found, false otherwise.
	 */
	public static boolean enchFilter(PlayerInventory inv, List<Enchantment> filter, boolean isBlacklist, boolean remove) {
		boolean result = false;
		for (int i = 0; i < 40; i++)
			if (isBlacklist == hasEnchantments(inv.getItem(i), filter))
				if (remove) {
					inv.clear(i);
					result = true;
				} else return true;
		return result;
	}
	
	/**
	 * Checks if an item has any of given enchantments applied to it
	 * @return true if any of the given enchantments was found, false otherwise.
	 */
	public static boolean hasEnchantments(ItemStack item, List<Enchantment> enchantments) {
		for (Enchantment e : item.getEnchantments().keySet().toArray(new Enchantment[0]))
			if (enchantments.contains(e))
				return true;
		return false;
	}
	/**
	 * Checks if the potion has any of given potion effects
	 * @return true if any of the given effects was found, false otherwise.
	 */
	public static boolean hasPotionEffect(Collection<PotionEffect> potion, List<PotionEffectType> effects) {
		for (PotionEffect pe : potion.toArray(new PotionEffect[0]))
			if (effects.contains(pe.getType()))
				return true;
		return false;
	}
	
	/**
	 * Colors the armor in a given color
	 * @param color - what color the armor should be
	 * @param leatherArmor - the type of leather armor that you want to get
	 * @return the colored armor or null if {@link leatherArmor} is not a type of leather armor.
	 */
	public static ItemStack getColoredArmor(Color color, Material leatherArmor) {
		if (leatherArmor != Material.LEATHER_HELMET && leatherArmor != Material.LEATHER_CHESTPLATE &&
				leatherArmor != Material.LEATHER_LEGGINGS && leatherArmor != Material.LEATHER_BOOTS)
			return null;
		LeatherArmorMeta lim = (LeatherArmorMeta)Bukkit.getItemFactory().getItemMeta(leatherArmor);
		lim.setColor(color);
		ItemStack result = new ItemStack(leatherArmor);
		result.setItemMeta(lim);
		return result;
	}
	/**
	 * Clears a {@link PlayerInventory}
	 * @param inv - inventory to clear
	 */
	public static void clearPlayerInv(PlayerInventory inv) {
		inv.clear(-1, -1);
	}
	/**
	 * Naturally drops all inventory's contents in a given location. If inventory is a {@link PlayerInventory} it'll drop the armor as well.
	 * Note: Contents will not be removed from the inventory!
	 * @param inventory - inventory to get the items from
	 * @param location - location where the items will be dropped.
	 */
	public static void dropItems(Inventory inventory, Location location) {
		World world = location.getWorld();
		for (int i = 0; i < inventory.getSize(); i++)
			world.dropItemNaturally(location, inventory.getItem(i));
		if (inventory instanceof PlayerInventory && inventory.getSize() == 36)
			for (int i = 36; i < 40; i++)
				world.dropItemNaturally(location, inventory.getItem(i));
	}
}