package com.impress.Infection.data;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.impress.Infection.utilities.InvTools;

public class Kit {
	public String name;
	public ItemStack itemInHand;
	public Map<Integer, ItemStack> inventory;
	public Map<String, ItemStack> otherItems;
	public Kit() {
		name = "*";
		inventory = new HashMap<Integer, ItemStack>();
		otherItems = new HashMap<String, ItemStack>();
	}
	public void fromPlayer(Player player, boolean saveSlots) throws IllegalArgumentException {
		if (player == null || !player.isOnline())
			throw new IllegalArgumentException("Player not found");
		clear();
		PlayerInventory inv = player.getInventory();
		if (saveSlots) {
			inventory = new HashMap<Integer, ItemStack>();
			ItemStack item;
			for (int i = 0; i < 40; i++) {
				item = inv.getItem(i).clone();
				if (item != null)
					inventory.put(i, item);
				//if (inv.getItem(i) == null) player.sendMessage("index = " + i + ", null item");
				//else player.sendMessage("index = " + i + ", type = " + inv.getItem(i).getType().toString() + ", amount = " + inv.getItem(i).getAmount());
			}
		} else {
			otherItems = new HashMap<String, ItemStack>();
			int i = 0;
			for (ItemStack item : inv.getContents())
				if (item != null)
					otherItems.put("o" + i++, item);
			for (ItemStack item : inv.getArmorContents())
				if (item != null)
					otherItems.put("o" + i++, item);
		}
	}
	public boolean giveToPlayer(Player player, boolean clear, boolean rearrange, boolean tryToFit) {
		if (player == null || !player.isOnline())
			throw new IllegalArgumentException("Player not found");
		PlayerInventory inv = player.getInventory();
		
		if (!tryToFit) {
			int items = otherItems.size() + inventory.size();
			for (int i = 36; i < 40; i++)
				if (inventory.containsKey(i))
					items--;
			if (itemInHand != null)
				items++;
			if (clear)
				if (items > 36)
					return false;
			else
				if (items > InvTools.emptySlots(inv))
					return false;
		}
		
		if (clear) inv.clear();
		
		for (Integer index : inventory.keySet().toArray(new Integer[inventory.size()])) { // TODO try using int
			ItemStack item = inventory.get(index).clone();
			if (inv.getItem(index) == null)
				inv.setItem(index, item);
			else
				if (rearrange) {
					if (!inv.addItem(inv.getItem(index)).isEmpty())
						return false;
					inv.setItem(index, item);
				} else
					if (!inv.addItem(item).isEmpty())
						return false;
		}
		
		for (String key : otherItems.keySet().toArray(new String[otherItems.size()]))
			if (!inv.addItem(otherItems.get(key).clone()).isEmpty())
				return false;
		
		if (itemInHand != null) {
			ItemStack item = itemInHand.clone();
			if (inv.getItemInHand() == null)
				inv.setItemInHand(item);
			else
				if (rearrange) {
					if (!inv.addItem(inv.getItemInHand()).isEmpty())
						return false;
					inv.setItemInHand(item);
				} else
					if (!inv.addItem(item).isEmpty())
						return false;
		}
		return true;
	}
	public boolean isEmpty() {
		return inventory.isEmpty() && otherItems.isEmpty() && itemInHand == null;
	}
	public void clear() {
		if (itemInHand != null)
			itemInHand = null;
		if (inventory != null)
			inventory = new HashMap<Integer, ItemStack>();
		if (otherItems != null)
			otherItems = new HashMap<String, ItemStack>();
	}
}