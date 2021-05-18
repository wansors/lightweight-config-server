package com.github.wansors.quarkusconfigserver.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MapConverter {

	private MapConverter() {
	}

	public static Map<String, Object> convert(Map<String, String> map) {

		Set<String> keys = map.keySet();
		List<String> array = new ArrayList<>(keys);
		Collections.sort(array);

		Map<String, Object> mapFinal = new HashMap<>();
		for (String key : array) {
			Object value = map.remove(key);
			MapConverter.mapper(key, value, mapFinal, null, 0);
		}

		return mapFinal;
	}

	public static String convertToPropertiesFormatString(Map<String, String> map) {

		StringBuilder propertiesStringBuilder = new StringBuilder();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			propertiesStringBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
		}

		// delete last "\n" if not empty
		if (propertiesStringBuilder.length() > 0) {
			propertiesStringBuilder.setLength(propertiesStringBuilder.length() - 1);
		}

		return propertiesStringBuilder.toString();
	}

	/**
	 * Recursive method for sorting values
	 *
	 * @param key
	 * @param value
	 * @param map
	 */
	private static void mapper(String key, Object value, Map<String, Object> map, List<Object> origList, int pos) {

		if (!key.contains(".") && !isList(key)) {
			if (map != null) {
				map.put(key, convertValue(value));
			} else {
				Map<String, Object> mapt;
				if (origList.size() > pos) {
					mapt = (Map<String, Object>) origList.get(pos);
				} else {
					// New map on list
					mapt = new HashMap<>();
					origList.add(pos, mapt);
				}
				mapt.put(key, convertValue(value));
			}
			return;
		} else if (!key.contains(".") && isList(key)) {
			// Last leaf but is an array
			String listName = key.substring(0, key.lastIndexOf("["));
			int itemPos = Integer.valueOf(key.substring(key.lastIndexOf("[") + 1, key.length() - 1));
			List<Object> listT = null;
			if (map != null) {
				// Add it to map
				listT = (List<Object>) map.get(listName);
				if (listT == null) {
					// new list on map
					listT = new ArrayList<>();
					map.put(listName, listT);
				}
				System.out.print(" " + key + " " + itemPos);
				listT.add(itemPos, convertValue(value));
			} else {
				System.out.print(" " + key + " " + pos);
				// new list on list
				listT = (List<Object>) origList.get(pos);
				if (listT == null) {
					// new list on map
					listT = new ArrayList<>();
					origList.add(pos, listT);
				}
				listT.add(itemPos, convertValue(value));
			}
			return;
		}

		// Split key in two parts
		String[] parts = key.split("\\.", 2);

		if (isList(parts[0])) {

			String listName = parts[0].substring(0, parts[0].lastIndexOf("["));
			int itemPos = Integer.valueOf(parts[0].substring(parts[0].lastIndexOf("[") + 1, parts[0].length() - 1));

			// we need to put in a list
			List<Object> list;
			if (map != null) {
				list = (List<Object>) map.get(listName);
				if (list == null) {
					list = new ArrayList<>();
					map.put(listName, list);
				}
			} else {
				if (origList.size() > pos) {
					list = (List<Object>) origList.get(pos);
				} else {
					list = new ArrayList<>();
					origList.add(pos, list);
				}
			}

			mapper(parts[1], value, null, list, itemPos);
		} else {

			Map<String, Object> newMap;
			if (map != null) {
				newMap = (Map<String, Object>) map.get(parts[0]);
				if (newMap == null) {
					newMap = new HashMap<>();
					map.put(parts[0], newMap);
				}
			} else {
				newMap = (Map<String, Object>) origList.get(pos);
				if (newMap == null) {
					newMap = new HashMap<>();
					origList.add(pos, newMap);
				}

			}

			MapConverter.mapper(parts[1], value, newMap, null, 0);
		}

	}

	private static boolean isList(String key) {
		return key.matches(".*\\[\\d+\\]$");
	}

	private static Object convertValue(Object value) {

		if (!value.getClass().equals(String.class)) {
			return value;
		}

		String stringValue = (String) value;

		if (stringValue.matches("^[+-]?[0-9]+$")) {
			value = Long.parseLong(stringValue);
		} else if (stringValue.matches("^[+-]?[0-9]+\\.[0-9]+$")) {
			value = Float.parseFloat(stringValue);
		} else if (stringValue.matches("(?i:^(true|on|yes)$)")) {
			value = true;
		} else if (stringValue.matches("(?i:^(false|off|no)$)")) {
			value = false;
		}

		return value;
	}
}
