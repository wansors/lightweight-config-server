package com.github.wansors.quarkusconfigserver.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class MapConverter {

	private static final String DEFAULT_TYPE = "java.lang.String";

	private MapConverter() {
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

	public static Map<String, Object> expandMap(Map<String, Object> map) {

		Set<String> keys = map.keySet();
		List<String> array = new ArrayList<>(keys);
		Collections.sort(array);

		Map<String, Object> mapFinal = new HashMap<>();
		for (String key : array) {
			Object value = map.get(key);
			MapConverter.mapper(key, value, mapFinal);
		}

		return mapFinal;
	}

	/**
	 * Recursive method for sorting values
	 *
	 * @param key
	 * @param value
	 * @param map
	 */
	private static void mapper(String key, Object value, Map<String, Object> map) {

		if (!key.contains(".") && !isList(key)) {
			// Last leaf from a map
			map.put(key, value);
			return;
		} else if (!key.contains(".") && isList(key)) {
			// Last leaf but is an array
			keyToArray(key, value, map);
			return;
		}

		// Split key in two parts
		String[] parts = key.split("\\.", 2);

		if (isList(parts[0])) {
			// Get map from array key
			Map<String, Object> newCurrentLevelMap = getMapFromArrayKey(parts[0], map);
			// Continue mapping
			mapper(parts[1], value, newCurrentLevelMap);
		} else {

			Map<String, Object> newMap;
			newMap = (Map<String, Object>) map.get(parts[0]);
			if (newMap == null) {
				newMap = new HashMap<>();
				map.put(parts[0], newMap);
			}

			MapConverter.mapper(parts[1], value, newMap);
		}

	}

	private static boolean isList(String key) {
		return key.matches(".*\\[\\d+\\]$");
	}

	private static String cleanArrayKey(String key) {
		return key.substring(0, key.indexOf("["));
	}

	private static int[] getArrayIndexes(String key, String cleanKey) {
		return Stream.of(key.replace(cleanKey, "").replace("[", "").split("]")).mapToInt(Integer::parseInt).toArray();
	}

	private static List<Object> getArrayLastList(Map<String, Object> currentLevelMap, String cleanKey, int[] items) {
		List<Object> parent = (List<Object>) currentLevelMap.get(cleanKey);
		if (parent == null) {
			parent = new ArrayList<>();
			currentLevelMap.put(cleanKey, parent);
		}
		for (int x = 0; x < (items.length - 1); x++) {
			int currentPos = items[x];
			List<Object> currentList = null;
			if (parent.size() > currentPos) {
				currentList = (List<Object>) parent.get(currentPos);
			}
			if (currentList == null) {
				currentList = new ArrayList<>();
				addItemToListFillingBlanks(parent, currentPos, currentList);
			}
			parent = currentList;
		}
		return parent;
	}

	private static void addItemToListFillingBlanks(List<Object> list, int pos, Object value) {
		if (list.size() > pos) {
			list.remove(pos);
		} else {
			// Fill blanks
			for (int x = list.size(); x < pos; x++) {
				list.add(x, null);
			}
		}
		list.add(pos, value);

	}

	private static void keyToArray(String key, Object value, Map<String, Object> currentLevelMap) {
		String cleanKey = cleanArrayKey(key);
		int[] items = getArrayIndexes(key, cleanKey);
		List<Object> parent = getArrayLastList(currentLevelMap, cleanKey, items);

		// Add last element
		addItemToListFillingBlanks(parent, items[items.length - 1], value);

	}

	private static Map<String, Object> getMapFromArrayKey(String key, Map<String, Object> currentLevelMap) {
		String cleanKey = cleanArrayKey(key);
		int[] items = getArrayIndexes(key, cleanKey);
		List<Object> parent = getArrayLastList(currentLevelMap, cleanKey, items);

		// Add last element
		Map<String, Object> map;
		if (parent.size() > items[items.length - 1]) {
			map = (Map<String, Object>) parent.get(items[items.length - 1]);
		} else {
			map = new HashMap<>();
			parent.add(items[items.length - 1], map);
		}

		return map;
	}
}
