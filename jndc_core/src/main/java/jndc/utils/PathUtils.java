package jndc.utils;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class PathUtils {
    private static final ClassLoader classLoader = PathUtils.class.getClassLoader();

    private static volatile String RUN_TIME_PATH;

    public static String getDesktopPath() {
        return getDesktopPath(File.separator);
    }

    /**
     * 获取运行时路径
     *
     * @return
     */
    public static String getRunTimePath() {
        if (RUN_TIME_PATH == null) {
            RUN_TIME_PATH = System.getProperty("user.dir");
            if (OSUtils.isLinux()) {
                if (!RUN_TIME_PATH.startsWith("/")) {
                    RUN_TIME_PATH = "/" + RUN_TIME_PATH;
                }
            }
        }


        return RUN_TIME_PATH;

    }


    public static String getDesktopPath(String singleDir) {
        FileSystemView fsv = FileSystemView.getFileSystemView();
        File com = fsv.getHomeDirectory();
        String Desktop = com.getPath();
        return Desktop + File.separator + singleDir + File.separator;
    }

    public static String systemPath2JavaPackagePath(String systemPath) {
        String s = systemPath.replaceAll("/", ".").replaceAll("\\\\", ".");
        return s;

    }

    public static String javaPackagePath2SystemPath(String javaPackage) {
        String s = javaPackage.replaceAll("\\.", "/");
        return s;

    }
}
