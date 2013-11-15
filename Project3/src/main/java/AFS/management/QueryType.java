package AFS.management;

/**
 */
public enum QueryType {
    FILES, NODES;

    public static QueryType fromString(String type) {
        return valueOf(type.toUpperCase());
    }
}
