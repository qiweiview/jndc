package com.view.jndc.core.v2.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class PathUtils {
    public static final String INSTANCE_ID = "instance_id.do_not_remove_this_file";

    private static final ClassLoader classLoader = PathUtils.class.getClassLoader();

    private static volatile String RUN_TIME_PATH;

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
     * 获取唯一运行时id
     *
     * @return
     */
    public static String getRuntimeUniqueId() {
        return getRuntimeUniqueId(getRuntimeDir() + File.separator, INSTANCE_ID);
    }

    /**
     * 获取唯一运行时id
     *
     * @param path     查询路径
     * @param fileName 文件名
     * @return
     */
    public static String getRuntimeUniqueId(String path, String fileName) {
        String filePath = path + File.separator + fileName;
        File file = new File(filePath);
        String id;
        if (file.exists()) {
            try {
                id = FileUtils.readFileToString(file, "utf-8");
            } catch (IOException e) {
                log.error("读取文件失败");
                throw new RuntimeException("读取文件失败", e);
            }
        } else {
            id = UUID.randomUUID().toString();
            try {
                FileUtils.writeStringToFile(file, id, "utf-8");
            } catch (IOException e) {
                log.error("写出文件失败");
                throw new RuntimeException("写出文件失败", e);
            }
        }
        return id;
    }

    /**
     * 获取运行时路径
     *
     * @return
     */
    public static String getRuntimeDir() {
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
