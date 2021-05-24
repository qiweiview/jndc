package jndc.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Deprecated
public class LocalFileUtils {
    private static final Charset DEFAULT_CHAR_SET = StandardCharsets.UTF_8;

    public static String readFileToString(File file) {
        byte[] bytes = readFileToByteArray(file);
        return new String(bytes, DEFAULT_CHAR_SET);


    }



    public  static String readFileToString(String filePath) {
        byte[] bytes = readFileToByteArray(new File(filePath));
        return new String(bytes, DEFAULT_CHAR_SET);


    }

    public  static byte[] readFileToByteArray(String filePath) {
        return readFileToByteArray(new File(filePath));
    }

    public static byte[] readFileToByteArray(File file) {
        if (file == null) {
            throw new RuntimeException("unSupport null");
        }

        if (!file.exists()) {
            throw new RuntimeException("not found the file ");
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[1024 * 1024];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int i;
            while ((i = fileInputStream.read(bytes)) != -1) {
                byteArrayOutputStream.write(Arrays.copyOf(bytes, i));
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("read fail cause " + e);
        }

    }
}
