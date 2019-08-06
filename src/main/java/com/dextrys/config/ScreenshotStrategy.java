package com.dextrys.config;

 public enum ScreenshotStrategy {
    ALL,
    FAILURE_ONLY,
    NONE;

    public static ScreenshotStrategy parse(String property) {
        if (property.toLowerCase().equals("all")) {
            return ALL;
        }
        if (property.toLowerCase().equals("failure_only")) {
            return FAILURE_ONLY;
        }
        if (property.toLowerCase().equals("none")) {
            return NONE;
        }
        throw new RuntimeException("Unknown screenshot shooting strategy: " + property);
    }
}
