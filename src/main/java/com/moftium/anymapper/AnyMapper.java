package com.moftium.anymapper;

import java.util.*;

public class AnyMapper {
    private final Map<String, Object> mappingConfig;
    private final List<AnyMapperPoint> mappingPoints;

    public AnyMapper(Map<String, Object> mappingConfig) throws AnyMapperConfigParserException {
        this.mappingConfig = mappingConfig;
        this.mappingPoints = parseMapping(mappingConfig);
    }

    @SuppressWarnings("unchecked")
    private LinkedList<AnyMapperPoint> parseMapping(Map<String, Object> mappingConfig) throws AnyMapperConfigParserException {
        LinkedList<AnyMapperPoint> result = new LinkedList<>();

        for (Map.Entry<String, Object> entry : mappingConfig.entrySet()) {
            if (!(entry.getValue() instanceof Map)) {
                throw new AnyMapperConfigParserException("config key [%s] is not a map".formatted(entry.getKey()));
            }

            String[] sourcePath = entry.getKey().split("\\.");
            Map<String, Object> config = (Map<String, Object>) entry.getValue();

            if (!config.containsKey("destination")) {
                throw new AnyMapperConfigParserException("config key [%s] missing destination field".formatted(entry.getKey()));
            }

            String[] destinationPath = ((String) config.get("destination")).split("\\.");
            boolean isList = config.containsKey("type") && "list".equalsIgnoreCase((String) config.get("type"));

            List<AnyMapperPoint> children = new ArrayList<>();

            if (isList) {
                var conf = new HashMap<>(config);
                conf.remove("type");
                conf.remove("destination");

                children = parseMapping(conf);
            }

            result.add(new AnyMapperPoint(sourcePath, destinationPath, children));
        }

        return result;
    }

    public Map<String, Object> mappingConfig() {
        return new HashMap<>(mappingConfig);
    }

    public List<AnyMapperPoint> mappingPoints() {
        return new ArrayList<>(mappingPoints);
    }

    public Map<String, Object> transform(Map<String, Object> source) {
        return AnyMapperTransform.transform(source, mappingPoints);
    }
}
