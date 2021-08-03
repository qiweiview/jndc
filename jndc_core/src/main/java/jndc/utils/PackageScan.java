package jndc.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

@Slf4j
public class PackageScan {
    private static final ClassLoader classLoader = PackageScan.class.getClassLoader();
    private static final String ClassPath = classLoader.getResource("").getFile();

    static {
        log.info("class path:---> " + classLoader.getResource("").getPath());
    }

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


        URL url = classLoader.getResource(s);
        if (url == null) {
            log.error("scan class can not found the path: " + s);
            return classes;
        }

        String protocol = url.getProtocol();
        try {
            if ("file".equals(protocol)) {
                //todo 文件
                String file = url.getFile();

                String decode = URLDecoder.decode(file, StandardCharsets.UTF_8.name());
                loadAllClass(new File(decode), classes);
            } else if ("jar".equals(protocol)) {
                //todo jar包
                JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
                loadAllClassFromJar(jarFile, classes, scanPath);
            }

        } catch (Exception e) {
            throw new RuntimeException("scan fail cause" + e);
        }


        return classes;
    }

    private static void loadAllClassFromJar(JarFile jarFile, List<Class> classes, String targetPre) {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String name = PathUtils.systemPath2JavaPackagePath(jarEntry.getName());
            // 判断是不是class文件
            if (name.startsWith(targetPre) && name.endsWith(".class")) {
                //todo 字节码
                name = name.substring(0, name.length() - 6);
                try {
                    Class<?> aClass = classLoader.loadClass(name);
                    classes.add(aClass);
                } catch (ClassNotFoundException e) {
                    log.error("can not load class " + name);
                }
            }
        }
    }

    /**
     * 递归扫描路径下的所有类
     *
     * @param file
     * @param classes
     */
    private static void loadAllClass(File file, List<Class> classes) {

        if (!file.exists()) {
            log.error("load all class can not found the path: " + file);
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
