package com.moftium.anymapper;

import java.util.*;

public class AnyMapperTransform {


    @SuppressWarnings("unchecked")
    protected static Map<String, Object> transform(Map<String, Object> source, List<AnyMapperPoint> mappingPoints) {
        Map<String, Object> result = new HashMap<>();

        for (AnyMapperPoint point : mappingPoints) {
            Object sourceValue = getValueByPath(source, point.sourcePath());

            if (point.isList()) {
                mapList(result, point, sourceValue);
            } else {
                setValueByPath(result, point.destinationPath(), sourceValue);
            }
        }

        return result;
    }


    @SuppressWarnings("unchecked")
    private static void mapList(Map<String, Object> result, AnyMapperPoint point, Object sourceValue) {
        List<?> sourceList = (List<?>) sourceValue;
        List<Object> transformedList = new ArrayList<>();

        for (Object item : sourceList) {
            if (!(item instanceof Map)) {
                continue;
            }

            Map<String, Object> sourceItem = (Map<String, Object>) item;

            var map = transform(sourceItem, point.children());
            transformedList.add(map);
        }

        setValueByPath(result, point.destinationPath(), transformedList);
    }

    @SuppressWarnings("unchecked")
    private static Object getValueByPath(Map<String, Object> map, String[] path) {
        Object current = map;

        for (String part : path) {
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
    private static void setValueByPath(Map<String, Object> map, String[] path, Object value) {
        Map<String, Object> current = map;

        for (int i = 0; i < path.length - 1; i++) {
            String part = path[i];

            if (!current.containsKey(part) || !(current.get(part) instanceof Map)) {
                current.put(part, new HashMap<String, Object>());
            }

            current = (Map<String, Object>) current.get(part);
        }

        current.put(path[path.length - 1], value);
    }
}
