package com.moftium.anymapper.config;

public class AnyMapperConfig {
    private final int nestingLevels;

    public AnyMapperConfig(int nestingLevels) {
        this.nestingLevels = nestingLevels;
    }

    public int nestingLevels() {
        return nestingLevels;
    }

}
