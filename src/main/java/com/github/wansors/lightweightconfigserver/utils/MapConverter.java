package com.github.wansors.lightweightconfigserver.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class MapConverter {

    private MapConverter() {
    }

    public static String convertToPropertiesFormatString(final Map<String, String> map) {

	final StringBuilder propertiesStringBuilder = new StringBuilder();
	for (final Map.Entry<String, String> entry : map.entrySet()) {
	    propertiesStringBuilder.append(entry.getKey())
		    .append(": ")
		    .append(entry.getValue())
		    .append("\n");
	}

	// delete last "\n" if not empty
	if (propertiesStringBuilder.length() > 0) {
	    propertiesStringBuilder.setLength(propertiesStringBuilder.length() - 1);
	}

	return propertiesStringBuilder.toString();
    }

    public static Map<String, Object> expandMap(final Map<String, Object> map) {

	final Set<String> keys = map.keySet();
	final List<String> array = new ArrayList<>(keys);
	Collections.sort(array);

	final Map<String, Object> mapFinal = new HashMap<>();
	for (final String key : array) {
	    final Object value = map.get(key);
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
    @SuppressWarnings("unchecked")
    private static void mapper(final String key, final Object value, final Map<String, Object> map) {

	if (!key.contains(".") && !isList(key)) {
	    // Last leaf from a map
	    map.put(key, value);
	    return;
	}
	if (!key.contains(".") && isList(key)) {
	    // Last leaf but is an array
	    keyToArray(key, value, map);
	    return;
	}

	// Split key in two parts
	final String[] parts = key.split("\\.", 2);

	if (isList(parts[0])) {
	    // Get map from array key
	    final Map<String, Object> newCurrentLevelMap = getMapFromArrayKey(parts[0], map);
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

    private static boolean isList(final String key) {
	return key.matches(".*\\[\\d+\\]$");
    }

    private static String cleanArrayKey(final String key) {
	return key.substring(0, key.indexOf("["));
    }

    private static int[] getArrayIndexes(final String key, final String cleanKey) {
	return Stream.of(key.replace(cleanKey, "")
		.replace("[", "")
		.split("]"))
		.mapToInt(Integer::parseInt)
		.toArray();
    }

    @SuppressWarnings("unchecked")
    private static List<Object> getArrayLastList(final Map<String, Object> currentLevelMap, final String cleanKey, final int[] items) {
	List<Object> parent = (List<Object>) currentLevelMap.get(cleanKey);
	if (parent == null) {
	    parent = new ArrayList<>();
	    currentLevelMap.put(cleanKey, parent);
	}
	for (int x = 0; x < items.length - 1; x++) {
	    final int currentPos = items[x];
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

    private static void addItemToListFillingBlanks(final List<Object> list, final int pos, final Object value) {
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

    private static void keyToArray(final String key, final Object value, final Map<String, Object> currentLevelMap) {
	final String cleanKey = cleanArrayKey(key);
	final int[] items = getArrayIndexes(key, cleanKey);
	final List<Object> parent = getArrayLastList(currentLevelMap, cleanKey, items);

	// Add last element
	addItemToListFillingBlanks(parent, items[items.length - 1], value);

    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getMapFromArrayKey(final String key, final Map<String, Object> currentLevelMap) {
	final String cleanKey = cleanArrayKey(key);
	final int[] items = getArrayIndexes(key, cleanKey);
	final List<Object> parent = getArrayLastList(currentLevelMap, cleanKey, items);

	// Add last element
	if (!(parent.size() > items[items.length - 1])) {
	    for (int x = parent.size(); x <= items[items.length - 1]; x++) {
		parent.add(x, new HashMap<String, Object>());
	    }

	}

	return (Map<String, Object>) parent.get(items[items.length - 1]);
    }

}
