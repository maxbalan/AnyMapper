package com.moftium.anymapper;

import com.moftium.anymapper.config.AnyMapperConfig;
import com.moftium.anymapper.exception.AnyMapperConfigParserException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AnyMapper {
    private final Map<String, Object> mappingConfig;
    private final List<AnyMapperPoint> mappingPoints;
    private final AnyMapperConfig config;

    public AnyMapper(Map<String, Object> mappingConfig) throws AnyMapperConfigParserException {
        this.mappingConfig = mappingConfig;
        this.config = new AnyMapperConfig(10);
        this.mappingPoints = parseMapping(mappingConfig, 1);
    }

    public AnyMapper(Map<String, Object> mappingConfig, AnyMapperConfig config) throws AnyMapperConfigParserException {
        this.mappingConfig = mappingConfig;
        this.config = config;
        this.mappingPoints = parseMapping(mappingConfig, 1);
    }

    @SuppressWarnings("unchecked")
    private LinkedList<AnyMapperPoint> parseMapping(Map<String, Object> mappingConfig, int nestingLevel) throws AnyMapperConfigParserException {
        if (nestingLevel > this.config.nestingLevels()) {
            throw new AnyMapperConfigParserException(String.format("config nesting level is > %d", this.config.nestingLevels()));
        }

        LinkedList<AnyMapperPoint> result = new LinkedList<>();

        for (Map.Entry<String, Object> entry : mappingConfig.entrySet()) {
            if (!(entry.getValue() instanceof Map)) {
                throw new AnyMapperConfigParserException(String.format("config key [%s] is not a map", entry.getKey()));
            }

            String[] sourcePath = entry.getKey().split("\\.");
            Map<String, Object> config = (Map<String, Object>) entry.getValue();

            if (!config.containsKey("destination")) {
                throw new AnyMapperConfigParserException(String.format("config key [%s] missing 'destination' field", entry.getKey()));
            }

            String[] destinationPath = ((String) config.get("destination")).split("\\.");
            boolean isList = config.containsKey("type") && "list".equalsIgnoreCase((String) config.get("type"));

            List<AnyMapperPoint> children = new ArrayList<>();

            if (isList) {
                HashMap<String, Object> conf = new HashMap<>(config);
                conf.remove("type");
                conf.remove("destination");

                children = parseMapping(conf, nestingLevel + 1);
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
