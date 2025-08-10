package com.moftium.anymapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnyMapperPoint {
    private final String[] sourcePath;
    private final String[] destinationPath;
    private final boolean isList;
    private final List<AnyMapperPoint> children;

    protected AnyMapperPoint(String[] sourcePath, String[] destinationPath, List<AnyMapperPoint> children) {
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
        this.isList = !children.isEmpty();
        this.children = children;
    }

    protected AnyMapperPoint(String[] sourcePath, String[] destinationPath) {
        this(sourcePath, destinationPath, new ArrayList<>());
    }

    public String[] sourcePath() {
        return sourcePath;
    }

    public String[] destinationPath() {
        return destinationPath;
    }

    public boolean isList() {
        return isList;
    }

    public List<AnyMapperPoint> children() {
        return children;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AnyMapperPoint{");
        sb.append("sourcePath=").append(Arrays.toString(sourcePath));
        sb.append(", destinationPath=").append(Arrays.toString(destinationPath));
        sb.append(", isList=").append(isList);
        sb.append(", children=").append(children);
        sb.append('}');
        return sb.toString();
    }
}
