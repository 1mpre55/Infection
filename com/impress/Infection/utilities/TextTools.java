package com.impress.Infection.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TextTools {
	/**
	 * Orders strings by the number of occurrence of a given character.
	 * @param values - unordered array of strings.
	 * @param ch - the character.
	 * @return a List<String> with strings in order.
	 */
	public static List<String> orderByOccurrence(String[] values, char ch) {
		int[] occurrence = new int[values.length];
		int max = 0;
		for (int i = 0; i < values.length; i++) {
			occurrence[i] = countChars(values[i], ch);
			if (max < occurrence[i])
				max = occurrence[i];
		}
		ArrayList<String> result = new ArrayList<String>(values.length);
		for (int i = 0, k = 0; i <= max; i++) {
			for (int j = 0; j < occurrence.length; j++)
				if (i == occurrence[j]) {
					result.add(values[j]);
					k++;
				}
			if (k >= values.length);
		}
		return result;
	}
	/**
	 * Combines the strings in the List into a single CSV string.
	 * @param values - List of strings to combine.
	 * @return the resulting string.
	 */
	public static String separateWithCommas(List<String> values) {
		return separateWithCommas(values.toArray(new String[0]));
	}
	/**
	 * Combines the strings in the Set into a single CSV string.
	 * @param values - Set of strings to combine.
	 * @return the resulting string.
	 */
	public static String separateWithCommas(Set<String> values) {
		return separateWithCommas(values.toArray(new String[0]));
	}
	/**
	 * Combines the strings in the array into a single CSV string.
	 * @param values - array of strings to combine.
	 * @return the resulting string.
	 */
	public static String separateWithCommas(String[] values) {
		if (values == null) return null;
		if (values.length == 0) return "";
		StringBuilder result = new StringBuilder(values[0]);
		for (int i = 1; i < values.length; i++)
			result.append(", ").append(values[i]);
		return result.toString();
	}
	/**
	 * Counts the number of occurrences of a character in a string.
	 * @param string - the string.
	 * @param ch - the character.
	 * @return the number of occurrences.
	 */
	public static int countChars(String string, char ch) {
		int i = 0;
		for (char c : string.toCharArray())
			if (c == ch) i++;
		return i;
	}
	/**
	 * Splits a CSV string into an array of integers
	 * @param string - the CSV string containing only integer values
	 * @return int array containing the values
	 * @throws NumberFormatException if one of the values does not contain a single parsable integer.
	 */
	public static int[] intsFromCSVString(String string) throws NumberFormatException {
		String[] strings = splitCSV(string);
		int[] result = new int[strings.length];
		for (int i = 0; i < result.length; i++)
			result[i] = Integer.parseInt(strings[i].trim());
		return result;
	}
	/**
	 * Splits a CSV string into an array of strings
	 * @param CSVString - the CSV string
	 * @return String array containing CSV values
	 */
	public static String[] splitCSV(String CSVString) {
		String[] result = CSVString.split(",");
		for (int i = 0; i < result.length; i++)
			result[i] = result[i].trim();
		return result;
	}
}