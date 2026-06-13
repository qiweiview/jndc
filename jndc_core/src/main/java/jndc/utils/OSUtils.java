package jndc.utils;

public class OSUtils {
    private static volatile String os;

    private static String getOs() {
        if (os == null) {
            os = System.getProperty("os.name");
        }
        return os.toLowerCase();
    }

    public static boolean isLinux() {
        return !isWindows();
    }

    public static boolean isWindows() {
        return getOs().indexOf("win") != -1;
    }
}
