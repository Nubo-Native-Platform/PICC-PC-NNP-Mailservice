package com.nubons.nnpmailservice.utils;

public class LogUtils {
    public static String sanitizeForLog(Object value) {
        if (value == null) {
            return null;
        }

        return value.toString().replace("\r", "").replace("\n", "");
    }
}
