package jndc.utils;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathUtils {

    public static String getDesktopPath() {
        FileSystemView fsv = FileSystemView.getFileSystemView();
        File com = fsv.getHomeDirectory();
        String Desktop = com.getPath();
        return Desktop;
    }

    public static String systemPath2JavaPackagePath(String systemPath) {
        String s = systemPath.replaceAll(Pattern.quote(File.separator), ".");
        return s;

    }

    public static String javaPackagePath2SystemPath(String javaPackage) {
        String s = javaPackage.replaceAll("\\.", Matcher.quoteReplacement(File.separator));
        return s;

    }
}
