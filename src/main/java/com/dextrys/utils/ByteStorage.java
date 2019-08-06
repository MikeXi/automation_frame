package com.dextrys.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Ilia Vakhrushev
 * Date: 8/9/15
 *
 * Dummy class for cross-thread storage of files intercepted by proxy.
 *
 */
public class ByteStorage {
    private static final Logger logger = LoggerFactory.getLogger(ByteStorage.class);

    public static Map<String, byte[]> map = new HashMap<String, byte[]>();
    private static boolean enabled = false;

    public static boolean isEnabled() {
        return enabled;
    }

    public static void enable() {
        enabled = true;
        logger.debug("ByteStorage enabled");
    }

    public static void disable() {
        enabled = false;
        logger.debug("ByteStorage disabled");
    }
}
