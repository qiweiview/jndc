package com.view.jndc.core.v2.utils;

public class OSUtils {
    private static volatile String os;


    public static boolean isLinux() {
        if (os == null) {
            os = System.getProperty("os.name");
        }

        if (os.toLowerCase().indexOf("win") != -1) {
            return false;
        }
        return true;
    }
}
