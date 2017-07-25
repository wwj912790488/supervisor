package com.arcsoft.supervisor.utils;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {

	/**
	 * Convert byte array to hex string.
	 * 
	 * @param b - the byte array to be convert.
	 * @return Returns hex string.
	 */
	public static String toHexString(byte[] b) {
		if (b == null)
			return null;
		String str = "";
		for (int i = 0; i < b.length; i++) {
			int v = 0xff & b[i];
			if (v < 0x10)
				str += '0';
			str += Integer.toHexString(v);
		}
		return str;
	}

	/**
	 * Convert byte array to hex string.
	 * 
	 * @param b - the byte array to be convert.
	 * @param sp - the separator between two bytes.
	 * @return Returns hex string.
	 */
	public static String toHexString(byte[] b, char sp) {
		if (b == null)
			return null;
		String str = "";
		for (int i = 0; i < b.length; i++) {
			if (i > 0)
				str += sp;
			int v = 0xff & b[i];
			if (v < 0x10)
				str += '0';
			str += Integer.toHexString(v);
		}
		return str;
	}

	/**
	 * Convert string to Integer.
	 * 
	 * @param s - the string to be convert
	 * @return Returns Integer, or null if cannot convert.
	 */
	public static Integer toInteger(String s) {
		if (s == null || s.length() == 0)
			return null;
		try {
			return Integer.decode(s);
		} catch(NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Convert string to Integer.
	 * 
	 * @param s - the string to be convert
	 * @param defaultValue - the default value
	 * @return Returns Integer, or returns default value if cannot convert.
	 */
	public static Integer toInteger(String s, Integer defaultValue) {
		Integer value = toInteger(s);
		return (value == null) ?  defaultValue : value;
	}

	/**
	 * Testing the string is empty or not.
	 * 
	 * @param s - the string to be tested
	 * @return true if {@code s} is null or empty.
	 */
	public static boolean isEmpty(String s) {
		return (s == null || s.isEmpty());
	}

	/**
	 * Testing the string is not empty.
	 * 
	 * @param s - the string to be tested
	 * @return false if {@code s} is null or empty.
	 */
	public static boolean isNotEmpty(String s) {
		return !isEmpty(s);
	}

	/**
	 * Testing the string is blank or not.
	 * 
	 * @param s - the string to be tested
	 * @return true if {@code s} is null, empty or blank.
	 */
	public static boolean isBlank(String s) {
		return (s == null || s.trim().isEmpty());
	}

	/**
	 * Testing the string is blank or not.
	 * 
	 * @param s - the string to be tested
	 * @return false if {@code s} is null, empty or blank.
	 */
	public static boolean isNotBlank(String s) {
		return !isBlank(s);
	}
	
	/**
	 * Convert string to boolean
	 * @param value - the string to be convert
	 * @return {@code boolean}. if value is "0" or "n" or "f" or "false" or value is null or 
	 * 			empty will return {@code false}, otherwise return {@code true}.
	 */
	public static final boolean toBoolean(String value) {
		return toBoolean(value, false);
	}
	
	/**
	 * Convert string to boolean
	 * @param value - the string to be convert
	 * @param defValue - the default value
	 * @return {@code boolean}. if value is null or empty will return {@code defValue}, 
	 * 			else when value is "0" or "n" or "f" or "false" then return {@code false}, otherwise return {@code true}.
	 */
	public static final boolean toBoolean(String value, boolean defValue) {
		if (value == null || value.trim().length() == 0) {
			return defValue;
		}
		
		if (value.compareToIgnoreCase("0") == 0 
			|| value.compareToIgnoreCase("n") == 0
			|| value.compareToIgnoreCase("no") == 0
			|| value.compareToIgnoreCase("f") == 0
			|| value.compareToIgnoreCase("false") == 0
			) {
			return false;
		}
		else if (value.compareToIgnoreCase("1") == 0 
			|| value.compareToIgnoreCase("y") == 0
			|| value.compareToIgnoreCase("yes") == 0
			|| value.compareToIgnoreCase("t") == 0
			|| value.compareToIgnoreCase("true") == 0
			) {
			return true;
		}

		return defValue;
	}

	/**
	 * Create comparator for string which end with numbers.
	 * 
	 * @param ignoreCase - compare ignore case
	 * @return the string comparator.
	 */
	public static Comparator<String> createComparatorForStringEndsWithNumber(final boolean ignoreCase) {
		return new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				String regex = "\\d+$";
				Pattern pattern = Pattern.compile(regex);
				Matcher m1 = pattern.matcher(o1);
				Matcher m2 = pattern.matcher(o2);
				if (!m1.find() || !m2.find()) {
					return ignoreCase ? o1.compareToIgnoreCase(o2) : o1.compareTo(o2);
				} else {
					String s1 = o1.replaceAll(regex, "");
					String s2 = o2.replaceAll(regex, "");
					int ret = ignoreCase ? s1.compareToIgnoreCase(s2) : s1.compareTo(s2);
					if (ret != 0)
						return ret;
					return Integer.valueOf(m1.group()).compareTo(Integer.valueOf(m2.group()));
				}
			}
		};
	}

}
