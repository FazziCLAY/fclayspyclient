package com.fazziclay.fclaypersonstatusclient;

public class Debug {
    public static final boolean DEBUG = false;

    public static void debug(Object o) {
        if (DEBUG) {
            System.out.println("DEBUG: " + o);
        }
    }
}
