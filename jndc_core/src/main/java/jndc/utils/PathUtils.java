package jndc.utils;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class PathUtils {
    private static final ClassLoader classLoader = PathUtils.class.getClassLoader();

    private static volatile String RUN_TIME_PATH;

    private static final String JNDC_HOME_DIR = ".jndc";
    private static final String SERVER_DIR = "server";
    private static final String CLIENT_DIR = "client";

    /**
     * 获取 ~/.jndc 根目录
     */
    public static String getJndcHome() {
        String userHome = System.getProperty("user.home");
        String jndcHome = userHome + File.separator + JNDC_HOME_DIR;
        mkdirIfNotExist(jndcHome);
        return jndcHome;
    }

    /**
     * 获取 server 工作空间 ~/.jndc/server
     */
    public static String getServerWorkspace() {
        String workspace = getJndcHome() + File.separator + SERVER_DIR;
        mkdirIfNotExist(workspace);
        return workspace;
    }

    /**
     * 获取 client 工作空间 ~/.jndc/client
     */
    public static String getClientWorkspace() {
        String workspace = getJndcHome() + File.separator + CLIENT_DIR;
        mkdirIfNotExist(workspace);
        return workspace;
    }

    private static void mkdirIfNotExist(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static String getDesktopPath() {
        return getDesktopPath(File.separator);
    }


    /**
     * 从多个地址中获取至少一个有效地址
     *
     * @param address
     * @return
     */
    public static String findExistPath(String... address) {
        for (int i = 0; i < address.length; i++) {
            if (new File(address[i]).exists()) {
                return (address[i]);
            }
        }
        throw new RuntimeException("数组中没有有效地址");
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
