package jndc.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class PackageScan {
    private static final ClassLoader classLoader = PackageScan.class.getClassLoader();
    private static final String ClassPath = classLoader.getResource("").getFile();

    /**
     * 扫描路径
     *
     * @param scanPath
     * @return
     */
    public static List<Class> scanClass(String scanPath) {

        List<Class> classes = new ArrayList<>();

        //转换为系统地址
        String s = PathUtils.javaPackagePath2SystemPath(scanPath);


        URL resource = classLoader.getResource(s);
        if (resource == null) {
            log.error("can not found the path: " + scanPath);
            return classes;
        }
        String file = resource.getFile();
        try {
            String decode = URLDecoder.decode(file, StandardCharsets.UTF_8.name());
            loadAllClass(new File(decode), classes);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("decode fail cause" + e);
        }

        return classes;
    }

    /**
     * 递归扫描路径下的所有类
     *
     * @param file
     * @param classes
     */
    private static void loadAllClass(File file, List<Class> classes) {

        if (!file.exists()) {
            log.error("can not found the path: " + file);
            return;
        }
        Stream.of(file.listFiles()).forEach(x -> {
            if (x.isDirectory()) {
                //todo 目录
                loadAllClass(x, classes);
            } else {
                //类文件
                String path = x.getPath();
                String s = toJavaClassPath(path);
                try {
                    Class<?> aClass = classLoader.loadClass(s);
                    classes.add(aClass);
                } catch (ClassNotFoundException e) {
                    log.error("can not load class " + s);
                }

            }
        });

    }

    /**
     * 替换成类路径
     *
     * @param path
     * @return
     */
    private static String toJavaClassPath(String path) {
        String substring = path.substring(ClassPath.length() - 1, path.length() - 6);
        String s = PathUtils.systemPath2JavaPackagePath(substring);
        log.debug(path + "--->" + s);
        return s;
    }


}
