package com.view.jndc.core.v2.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ByteArrayUtils {

    /**
     * 定长拆分字节码
     *
     * @param bytes
     * @param length
     * @return
     */
    public static List<byte[]> bytesUnpack(byte[] bytes, int length) {
        List<byte[]> list = new ArrayList<>();
        int maxLength = bytes.length;
        int currentIndex = 0;

        while (currentIndex < maxLength) {
            int endIndex = Math.min(currentIndex + length, maxLength);
            byte[] subArray = Arrays.copyOfRange(bytes, currentIndex, endIndex);
            list.add(subArray);
            currentIndex = endIndex;
        }

        return list;
    }


}
