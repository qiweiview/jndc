package com.view.core.utils;

import java.nio.ByteBuffer;

public class HexUtils {


    /**
     * 将字节数组转换为16进制字符串
     * @param value
     * @return
     */
    public static byte[] longToBytes(long value) {
        ByteBuffer buffer = ByteBuffer.allocate(8);  // 长度为8的字节数组（long占8字节）
        buffer.putLong(value);  // 将long值写入缓冲区
        return buffer.array();  // 返回字节数组
    }


    /**
     * return byte array with fix size 4
     *
     * @param i
     * @return
     */
    public static byte[] int2ByteArray(int i) {
        ByteBuffer allocate = ByteBuffer.allocate(4);
        byte[] array = allocate.putInt(i).array();
        return array;
    }

    /**
     * accept a fix length byte array
     *
     * @param bytes
     * @return
     */
    public static int byteArray2Int(byte[] bytes) {
        if (bytes.length != 4) {
            throw new RuntimeException("not a int value");
        }
        ByteBuffer wrap = ByteBuffer.wrap(bytes);
        int anInt = wrap.getInt();
        return anInt;
    }

    /**
     * return byte array with fix size 8
     * @param bytes
     * @return
     */
    public static long byteArray2Long(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);  // 包装字节数组为ByteBuffer
        return buffer.getLong();  // 读取long值
    }
}