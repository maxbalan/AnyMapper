package com.moftium.anymapper;

import java.util.*;

public class AnyMapper {
    @SuppressWarnings("unchecked")
    public static Map<String, Object> transform(Map<String, Object> source, Map<String, Object> mapping) {
        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : mapping.entrySet()) {
            String sourcePath = entry.getKey();
            Object mappingConfig = entry.getValue();
            Object sourceValue = getValueByPath(source, sourcePath);

            if (sourceValue == null || !(mappingConfig instanceof Map)) {
                continue;
            }

            Map<String, Object> configMap = (Map<String, Object>) mappingConfig;

            String type = (String) configMap.get("type");
            String destPath = (String) configMap.get("destination");

            if ("list".equalsIgnoreCase(type)) {
                if (!(sourceValue instanceof List)) {
                    continue;
                }

                mapList(sourceValue, configMap, result, destPath);
            } else {
                setValueByPath(result, destPath, sourceValue);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private static void mapList(Object sourceValue, Map<String, Object> configMap, Map<String, Object> result, String destPath) {
        List<?> sourceList = (List<?>) sourceValue;
        List<Object> transformedList = new ArrayList<>();

        for (Object item : sourceList) {
            if (!(item instanceof Map)) continue;
            Map<String, Object> sourceItem = (Map<String, Object>) item;
            Map<String, Object> transformedItem = new HashMap<>();

            for (Map.Entry<String, Object> fieldMapping : configMap.entrySet()) {
                String key = fieldMapping.getKey();
                if (Set.of("type", "destination").contains(key)) continue;

                Object subConfig = fieldMapping.getValue();
                if (!(subConfig instanceof Map)) continue;

                Map<String, Object> subConfigMap = (Map<String, Object>) subConfig;
                String subDestPath = (String) subConfigMap.get("destination");
                String subType = (String) subConfigMap.get("type");
                Object innerValue = sourceItem.get(key);

                if (innerValue == null) continue;

                if ("list".equalsIgnoreCase(subType)) {
                    Map<String, Object> temp = new HashMap<>();
                    mapList(innerValue, subConfigMap, temp, "placeholder");
                    List<?> nestedResult = (List<?>) temp.get("placeholder");
                    setValueByPath(transformedItem, subDestPath, nestedResult);
                } else {
                    setValueByPath(transformedItem, subDestPath, innerValue);
                }
            }

            transformedList.add(transformedItem);
        }

        setValueByPath(result, destPath, transformedList);
    }

    @SuppressWarnings("unchecked")
    private static Object getValueByPath(Map<String, Object> map, String path) {
        String[] parts = path.split("\\.");
        Object current = map;

        for (String part : parts) {
            if (!(current instanceof Map)) {
                return null;
            }

            current = ((Map<String, Object>) current).get(part);
            if (current == null) {
                return null;
            }
        }

        return current;
    }

    @SuppressWarnings("unchecked")
    private static void setValueByPath(Map<String, Object> map, String path, Object value) {
        String[] parts = path.split("\\.");
        Map<String, Object> current = map;

        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            if (!current.containsKey(part) || !(current.get(part) instanceof Map)) {
                current.put(part, new HashMap<String, Object>());
            }
            current = (Map<String, Object>) current.get(part);
        }

        current.put(parts[parts.length - 1], value);
    }
}
