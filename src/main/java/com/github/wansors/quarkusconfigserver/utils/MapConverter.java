package com.github.wansors.quarkusconfigserver.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class MapConverter {

    private MapConverter() {
    }

    public static Map<String, Object> convert(Map<String, Object> map) {

        Set<String> keys = map.keySet();
        String[] array = keys.toArray(new String[keys.size()]);

        for (String key : array) {
            Object value = map.remove(key);
            MapConverter.mapper(key, value, map);
        }

        return map;
    }

    public static String convertToPropertiesFormatString(Map<String, Object> map) {

        StringBuilder propertiesStringBuilder = new StringBuilder();
        for (Map.Entry<String,Object> entry : map.entrySet()) {
            propertiesStringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        return propertiesStringBuilder.toString();
    }

    /**
     * Recursive method for sorting values
     * @param key
     * @param value
     * @param map
     */
    private static void mapper(String key, Object value, Map<String, Object> map) {
        
        if (!key.contains(".")) {
            map.put(key, convertValue(value));
            return;
        }

        String[] parts = key.split("\\.", 2);
        Map<String, Object> newMap = new HashMap<>();

        if (map.containsKey(parts[0])) {
            newMap = (Map<String, Object>) map.get(parts[0]);
        } else {
            map.put(parts[0], newMap);
        }

        MapConverter.mapper(parts[1], value, newMap);

    }

    private static Object convertValue(Object value) {

        if (!value.getClass().equals(String.class)) {
            return value;
        }

        String stringValue = (String) value;

        if (stringValue.matches("^[+-]?[0-9]+$")) {
            value = Integer.parseInt(stringValue);
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
