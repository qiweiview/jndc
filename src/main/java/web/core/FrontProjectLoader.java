package web.core;

import jndc.utils.LogPrint;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.stream.Stream;


public class FrontProjectLoader {

    public static FrontProjectLoader jndcStaticProject;

    private final int DEEP_LIMIT = 15;

    private String rootPath;

    private Map<String, InnerFileDescription> fileDescriptionMap = new HashMap<>();


    static{
        String web = FrontProjectLoader.class.getClassLoader().getResource("web_resource").getPath();
        jndcStaticProject = loadProject(web);
    }




   public InnerFileDescription findFile(String path){
       return fileDescriptionMap.get(path);
   }

    public FrontProjectLoader(String rootPath) {
        this.rootPath = rootPath;
    }

    public static FrontProjectLoader loadProject(String path) {
        FrontProjectLoader frontProjectLoader = new FrontProjectLoader(path);
        frontProjectLoader.recordFile(new File(path), 0);
        return frontProjectLoader;

    }

    private void recordFile(File file, Integer recursionDepth) {
        if (recursionDepth > DEEP_LIMIT) {
            throw new RuntimeException("over limit deep");
        }

        if (!file.exists()) {
            throw new RuntimeException("file not be found");
        }

        if (file.isDirectory()) {

            int nDeep = recursionDepth++;
            File[] files = file.listFiles();
            Stream.of(files).forEach(x -> {
                recordFile(x, nDeep);
            });
        } else {
            InnerFileDescription innerFileDescription = new InnerFileDescription(file, rootPath);
            fileDescriptionMap.put(innerFileDescription.getShortPath(),innerFileDescription);
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
            this.shortPath = this.fullPath.substring(rootPath.length()-1);
            this.fileType=file.getName().substring(file.getName().lastIndexOf(".")+1);
            loadFile();
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
         *
         *
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
