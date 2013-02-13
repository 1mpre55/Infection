package com.impress.Infection.utilities;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;

/**
 * Some random tools
 * @author 1mpre55
 */
public class Other {
	/**
	 * Returns a random element from the list.
	 * @param list - list to get the element from.
	 * @return random element from the list or null if the list is empty
	 */
	public static <V> V getRandomFromList(List<V> list) {
		if (list.isEmpty()) return null;
		return list.get((int)(Math.random() * list.size()));
	}
	/**
	 * Returns a random element from the array.
	 * @param array - array to get the element from.
	 * @return random element from the array or null if the array is empty
	 */
	public static <V> V getRandomFromArray(V[] array) {
		if (array.length < 1) return null;
		return array[(int)(Math.random() * array.length)];
	}
	
	/**
	 * Returns a Color described by the string.
	 * @param string - Either the name of the color or 3 integers: "red, green, blue".
	 * @return the {@link Color} described by the string
	 */
	public static Color colorFromString(String string) {
		if (string == null || string.isEmpty()) return null;
		if (string.indexOf(',') > 0)
			try {
				int[] rgb = TextTools.intsFromCSVString(string);
				if (rgb.length == 3)
					return Color.fromRGB(rgb[0], rgb[1], rgb[2]);
			} catch (NumberFormatException e) {}
		if (string.equalsIgnoreCase("red")) return Color.RED;
		if (string.equalsIgnoreCase("blue")) return Color.BLUE;
		if (string.equalsIgnoreCase("green")) return Color.GREEN;
		if (string.equalsIgnoreCase("black")) return Color.BLACK;
		if (string.equalsIgnoreCase("white")) return Color.WHITE;
		if (string.equalsIgnoreCase("yellow")) return Color.YELLOW;
		if (string.equalsIgnoreCase("orange")) return Color.ORANGE;
		if (string.equalsIgnoreCase("gray")) return Color.GRAY;
		if (string.equalsIgnoreCase("purple")) return Color.PURPLE;
		if (string.equalsIgnoreCase("aqua")) return Color.AQUA;
		if (string.equalsIgnoreCase("fuchsia")) return Color.FUCHSIA;
		if (string.equalsIgnoreCase("lime")) return Color.LIME;
		if (string.equalsIgnoreCase("maroon")) return Color.MAROON;
		if (string.equalsIgnoreCase("navy")) return Color.NAVY;
		if (string.equalsIgnoreCase("olive")) return Color.OLIVE;
		if (string.equalsIgnoreCase("silver")) return Color.SILVER;
		return null;
	}
	/**
	 * Converts a {@link Color} to {@link DyeColor} 
	 * @param color - color that should be converted
	 * @return the closest DyeColor to a given Color
	 */
	public static DyeColor colorToDyeColor(Color color) {
		// Let the native methods try to convert
		DyeColor result = DyeColor.getByColor(color);
		if (result != null) return result;
		
		// Use known similar colors. This should cover all colors returned by colorFromString(String) where the string is the name of the color
		if (color.equals(Color.RED)) result = DyeColor.RED;
		else if (color.equals(Color.BLUE)) result = DyeColor.LIGHT_BLUE;
		else if (color.equals(Color.GREEN)) result = DyeColor.GREEN;
		else if (color.equals(Color.BLACK)) result = DyeColor.BLACK;
		else if (color.equals(Color.WHITE)) result = DyeColor.WHITE;
		else if (color.equals(Color.YELLOW)) result = DyeColor.YELLOW;
		else if (color.equals(Color.ORANGE)) result = DyeColor.ORANGE;
		else if (color.equals(Color.GRAY)) result = DyeColor.GRAY;
		else if (color.equals(Color.PURPLE)) result = DyeColor.PURPLE;
		else if (color.equals(Color.AQUA)) result = DyeColor.CYAN;
		else if (color.equals(Color.FUCHSIA)) result = DyeColor.MAGENTA;
		else if (color.equals(Color.LIME)) result = DyeColor.LIME;
		else if (color.equals(Color.MAROON)) result = DyeColor.PINK;
		else if (color.equals(Color.NAVY)) result = DyeColor.BLUE;
		else if (color.equals(Color.OLIVE)) result = DyeColor.BROWN;
		else if (color.equals(Color.SILVER)) result = DyeColor.SILVER;
		if (result != null) return result;
		
		
		// If all else fails, find the closest color using math
		DyeColor[] dyes = DyeColor.values();
		Color tmp;
		int id = 0;
		for (int i = 0, j = 0, k = Integer.MAX_VALUE; i < dyes.length; i++)
			if ((j = Math.abs((tmp = dyes[i].getColor()).getRed() - color.getRed())
				   + Math.abs(tmp.getGreen() - color.getGreen())
				   + Math.abs(tmp.getBlue() - color.getBlue())) < k) {
				id = i;
				k = j;
			}
		return dyes[id];
	}
	/**
	 * Converts a {@link Color} to {@link ChatColor} 
	 * @param color - color that should be converted
	 * @return the closest ChatColor to a given Color
	 */
	public static ChatColor colorToChatColor(Color color) {
		ChatColor result = null;
		if (color.equals(Color.RED)) result = ChatColor.RED;
		else if (color.equals(Color.BLUE)) result = ChatColor.BLUE;
		else if (color.equals(Color.GREEN)) result = ChatColor.DARK_GREEN;
		else if (color.equals(Color.BLACK)) result = ChatColor.BLACK;
		else if (color.equals(Color.WHITE)) result = ChatColor.WHITE;
		else if (color.equals(Color.YELLOW)) result = ChatColor.YELLOW;
		else if (color.equals(Color.ORANGE)) result = ChatColor.GOLD;
		else if (color.equals(Color.GRAY)) result = ChatColor.DARK_GRAY;
		else if (color.equals(Color.PURPLE)) result = ChatColor.DARK_PURPLE;
		else if (color.equals(Color.AQUA)) result = ChatColor.DARK_AQUA;
		else if (color.equals(Color.FUCHSIA)) result = ChatColor.LIGHT_PURPLE;
		else if (color.equals(Color.LIME)) result = ChatColor.GREEN;
		else if (color.equals(Color.MAROON)) result = ChatColor.DARK_RED;
		else if (color.equals(Color.NAVY)) result = ChatColor.DARK_BLUE;
		else if (color.equals(Color.OLIVE)) result = ChatColor.AQUA;
		else if (color.equals(Color.SILVER)) result = ChatColor.GRAY;
		return result;
	}
}