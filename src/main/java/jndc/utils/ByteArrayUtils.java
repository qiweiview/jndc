package jndc.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ByteArrayUtils {

    public static List<byte[]> bytesUnpack(byte[] bytes, int length) {
        List<byte[]> list = new ArrayList<>();
        int maxLength = bytes.length;

        if (maxLength <= length) {
            list.add(bytes);
        } else {
            int s = 0;
            int e = 0;
            while (true) {
                e = e + length;
                if (e > maxLength) {
                    e = maxLength;
                }
                if (s == e) {
                    break;
                }
                list.add(Arrays.copyOfRange(bytes, s, e));
                s = e;
            }
        }
        return list;

    }


}
