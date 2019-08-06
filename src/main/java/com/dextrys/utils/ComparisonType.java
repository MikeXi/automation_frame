package com.dextrys.utils;

public enum ComparisonType {
    EXACTLY("exactly"),
    WITHOUT_SPACES("space insensitive"),
    CONTAINS("contains"),
    CASE_INSENSITIVE("case insensitive"),
    WITH_UNKNOWN("with Unknown"),
    NULLABLE("nullable"),
    PART_OF("part of"),
    NO_SPEC_CHARS("with no special chars and spaces");

    private final String label;

    ComparisonType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static ComparisonType parse(char code) {
        if ('e' == code) {
            return EXACTLY;
        } else if ('s' == code) {
            return WITHOUT_SPACES;
        } else if ('~' == code) {
            return CONTAINS;
        } else if ('i' == code) {
            return CASE_INSENSITIVE;
        } else if ('u' == code) {
            return WITH_UNKNOWN;
        } else if ('n' == code) {
            return NULLABLE;
        } else if ('p' == code) {
            return PART_OF;
        } else if ('c' == code) {
            return NO_SPEC_CHARS;
        } else {
            throw new RuntimeException("Unknown comparison type: " + code);
        }
    }
}
