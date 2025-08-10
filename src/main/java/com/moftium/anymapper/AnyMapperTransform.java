package com.moftium.anymapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnyMapperTransform {

    private AnyMapperTransform() {
    }

    @SuppressWarnings("unchecked")
    protected static Map<String, Object> transform(Map<String, Object> source, List<AnyMapperPoint> mappingPoints) {
        final int size = mappingPoints.size();
        final Map<String, Object> result = new LinkedHashMap<>(size);

        for (int i = 0; i < size; i++) {
            AnyMapperPoint point = mappingPoints.get(i);
            Object sourceValue = getValueByPath(source, point.sourcePath());

            if (sourceValue != null) {
                if (point.isList()) {
                    mapList(result, point, sourceValue);
                } else {
                    setValueByPath(result, point.destinationPath(), sourceValue);
                }
            }
        }

        return result;
    }


    @SuppressWarnings("unchecked")
    private static void mapList(Map<String, Object> result, AnyMapperPoint point, Object sourceValue) {
        if (!(sourceValue instanceof List<?>)) {
            return;
        }

        List<?> sourceList = (List<?>) sourceValue;
        int size = sourceList.size();
        List<Object> transformedList = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            Object item = sourceList.get(i);
            if (item instanceof Map<?, ?>) {
                Map<String, Object> childMap = transform((Map<String, Object>) item, point.children());
                transformedList.add(childMap);
            }
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
    private static void setValueByPath(Map<String, Object> result, String[] path, Object value) {
        Map<String, Object> current = result;

        for (int i = 0; i < path.length - 1; i++) {
            String part = path[i];
            Map<String, Object> child = (Map<String, Object>) current.get(part);
            if (child == null) {
                child = new LinkedHashMap<>(4);
                current.put(part, child);
            }
            current = child;
        }

        current.put(path[path.length - 1], value);
    }
}