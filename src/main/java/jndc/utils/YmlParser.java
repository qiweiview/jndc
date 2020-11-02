package jndc.utils;



import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 *         <dependency>
 *             <groupId>org.yaml</groupId>
 *             <artifactId>snakeyaml</artifactId>
 *             <version>1.25</version>
 *         </dependency>
 */
public class YmlParser {
    private static final Yaml yaml = new Yaml();


    public <T> T parseFile(String path, Class<T> type) throws FileNotFoundException {
        return parseFile(new FileInputStream(new File(path)), type);
    }

    public <T> T parseFile(File file, Class<T> type) throws FileNotFoundException {
        return parseFile(new FileInputStream(file), type);
    }

    public <T> T parseFile(InputStream input, Class<T> type) {
        T t = yaml.loadAs(input, type);
        return t;
    }


    public <T> String toYmlString(T t) {
        String s = yaml.dumpAsMap(t);
        return s;
    }


}
