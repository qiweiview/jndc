package jndc_server.web_support.core;


import jndc.utils.ApplicationExit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class FrontProjectLoader {
    public static FrontProjectLoader jndcStaticProject;

    //递归深度限制
    private final int DEEP_LIMIT = 15;

    private String rootPath;

    //文件路径映射
    private Map<String, InnerFileDescription> fileDescriptionMap = new HashMap<>();

    private int subStringIndex = 0;

    private volatile boolean reloadInterrupt = false;


    /**
     * 加载目录项目
     *
     * @param path
     * @return
     */
    public static FrontProjectLoader loadProject(String path) {
        FrontProjectLoader frontProjectLoader = new FrontProjectLoader(path);
        //加载文件
        frontProjectLoader.reloadProject();
        return frontProjectLoader;

    }


    public void reloadProject() {

        log.debug("load front project: " + rootPath);

        //销毁旧文件
        destroyOldVersion();


        //记录文件
        recordFile(new File(rootPath), 0);

        reloadInterrupt = false;
    }

    private void destroyOldVersion() {
        reloadInterrupt = true;
        Map<String, InnerFileDescription> fileDescriptionMap = this.fileDescriptionMap;
        fileDescriptionMap.forEach((k, v) -> {
            v.release();
        });
        this.fileDescriptionMap = new HashMap<>();
    }

    public InnerFileDescription findFile(String path) {
        if (reloadInterrupt) {
            return null;
        }
        return fileDescriptionMap.get(path);
    }

    public FrontProjectLoader(String rootPath) {
        this.rootPath = rootPath;
    }


    private void recordFile(File file, Integer recursionDepth) {
        if (recursionDepth > DEEP_LIMIT) {
            log.error("over limit deep");
            ApplicationExit.exit();
        }

        if (!file.exists()) {
            log.error("file not be found:" + file);
            ApplicationExit.exit();
        }

        if (file.isDirectory()) {

            int nDeep = recursionDepth++;
            File[] files = file.listFiles();
            Stream.of(files).forEach(x -> {
                recordFile(x, nDeep);
            });
        } else {
            InnerFileDescription innerFileDescription = new InnerFileDescription(file, rootPath);
            fileDescriptionMap.put(innerFileDescription.getShortPath(), innerFileDescription);
        }
    }



    public class InnerFileDescription {


        private final int LAZY_LOAD_SIZE = 5 * 1024 * 1024;//A single file is less than 5mb loaded into the memory

        private final int NOT_RECOMMEND_CACHE_SIZE = 100 * 1024 * 1024;//not recommend cache this size file in memory

        private File file;

        private String fileType;

        private String rootPath;

        private String shortPath;

        private String fullPath;

        private boolean lazyLoad;

        private boolean cacheEnable;

        private byte[] fileBytes;

        public InnerFileDescription(File file, String rootPath) {
            this.file = file;
            this.rootPath = rootPath;
            this.fullPath = file.getAbsolutePath();
            if (subStringIndex == 0) {
//                subStringIndex = findTheDifferentIndex(this.rootPath, this.fullPath)-1;
                subStringIndex = rootPath.length();
            }
            this.shortPath = this.fullPath.substring(subStringIndex);
            this.fileType = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            loadFile();
        }

        private int findTheDifferentIndex(String s1, String s2) {
            char[] chars = s1.toCharArray();
            char[] chars1 = s2.toCharArray();
            for (int i = 0; i < chars1.length; i++) {
                if (i == chars.length || chars[i] != chars1[i]) {
                    return i;
                }
            }
            return 1;
        }


        public void release() {
            this.file = null;
            this.fileBytes = null;
        }

        private void loadFile() {
            long length = file.length();
            if (length >= LAZY_LOAD_SIZE) {
                lazyLoad = true;
                if (length > NOT_RECOMMEND_CACHE_SIZE) {
                    cacheEnable = false;
                } else {
                    cacheEnable = true;
                }
            } else {
                try {
                    fileBytes = FileUtils.readFileToByteArray(file);
                } catch (IOException e) {
                    throw new RuntimeException("load file error:" + e);
                }
                lazyLoad = false;
            }

        }

        /**
         * @return
         */
        public byte[] getData() {
            if (fileBytes != null) {
                return fileBytes;
            }
            try {
                byte[] bytes = FileUtils.readFileToByteArray(file);
                if (cacheEnable) {
                    fileBytes = bytes;
                }
                return bytes;
            } catch (IOException e) {
                throw new RuntimeException("load file error:" + e);
            }
        }

        public String getFileType() {
            return fileType;
        }

        public String getShortPath() {
            return shortPath;
        }
    }
}
