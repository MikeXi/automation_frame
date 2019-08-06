package com.dextrys.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class JSONDataStore {
    private static Map<String,String> ese = new HashMap<String, String>();
    private static Map<String,String> pse = new HashMap<String, String>();
    private static Map<String,String> vse = new HashMap<String, String>();

 

    public static String eseDataFor(String environment) {
        String code = getCode(environment);
        if (ese.containsKey(code)) {
            return ese.get(code);
        } else {
            throw new RuntimeException("No data were found for the environment: " + environment);
        }
    }

    public static String pseDataFor(String environment) {
        String code = getCode(environment);
        if (pse.containsKey(code)) {
            return pse.get(code);
        } else {
            throw new RuntimeException("No data were found for the environment: " + environment);
        }
    }

    public static String vseDataFor(String environment) {
        String code = getCode(environment);
        if (vse.containsKey(code)) {
            return vse.get(code);
        } else {
            throw new RuntimeException("No data were found for the environment: " + environment);
        }
    }

    private static String getCode(String environment) {
        try {
            URL url = new URL(environment);
            String host = url.getHost();
            return host.substring(0, host.indexOf("."));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to get code for the environment: " + environment);
        }
    }
}
