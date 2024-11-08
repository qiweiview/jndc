package com.view.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class RuntimeUtils {

    private static final String DIR_NAME = "ndc_runtime";
    private static final String INSTANCE_ID = "instance_id.do_not_remove_this_file";
    public static final String NDC_RUNTIME_DIR = getRuntimeDir() + File.separator + DIR_NAME;


    /**
     * 获取唯一运行时id
     *
     * @return
     */
    public static String getRuntimeUniqueId() {
        String filePath = NDC_RUNTIME_DIR + File.separator + INSTANCE_ID;
        log.info("使用文件路径：{}存储唯一编号", filePath);
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        String id;
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                char[] chars = new char[(int) file.length()];
                reader.read(chars);
                id = new String(chars);
            } catch (IOException e) {
                System.err.println("读取文件失败");
                throw new RuntimeException("读取文件失败", e);
            }
        } else {
            id = UUID.randomUUID().toString();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(id);
            } catch (IOException e) {
                System.err.println("写出文件失败");
                throw new RuntimeException("写出文件失败", e);
            }
        }
        return id;
    }

    /**
     * 运行目录
     *
     * @return
     */
    public static String getRuntimeDir() {
        return System.getProperty("user.dir");
    }

    public static String getDir(String path) {
        return getRuntimeDir() + File.separator + path;
    }

    public static void createDir(String path) {
        String userDir = getRuntimeDir();
        String mkdir = userDir + File.separator + path;
        File file = new File(mkdir);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                log.error("创建目录失败");
                System.exit(1);
            }

        }
    }
}
