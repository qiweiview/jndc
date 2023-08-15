package com.view.jndc.core.v2.utils;

import com.view.jndc.core.v2.constant.os.RangeThreshold;
import com.view.jndc.core.v2.exception.BixException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ByteConversionUtil {

    /*============================== 基础类型 ==========================*/

    /**
     * 将int转换为byte数组
     *
     * @param value
     * @return
     */
    public static byte[] intToByteArray(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }


    /**
     * 将byte数组转换为int
     *
     * @param byteArray
     * @return
     */
    public static int byteArrayToInt(byte[] byteArray) {
        if (byteArray.length != 4) {
            throw new IllegalArgumentException("int byte array must be of length 4");
        }
        return ByteBuffer.wrap(byteArray).getInt();
    }


    /**
     * String to byte[]
     *
     * @param input
     * @return
     */
    public static byte[] stringToBytes(String input) {
        return input.getBytes(StandardCharsets.UTF_8);
    }


    /**
     * byte[] to String
     *
     * @param bytes
     * @return
     */
    public static String bytesToString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /*============================== 应用类型 ==========================*/

    /**
     * 端口转byte[]
     *
     * @param portNumber
     * @return
     */
    public static byte[] portToBytes(int portNumber) {
        if (portNumber >= RangeThreshold.PORT_MIN && portNumber <= RangeThreshold.PORT_MAX) {
            byte[] portBytes = new byte[2];
            portBytes[0] = (byte) ((portNumber >> 8) & 0xFF); // High byte
            portBytes[1] = (byte) (portNumber & 0xFF);        // Low byte
            return portBytes;
        } else {
            throw new BixException("端口范围取值不正确");
        }

    }

    /**
     * 转byte[]端口
     *
     * @param portBytes
     * @return
     */
    public static int bytesToPort(byte[] portBytes) {
        if (portBytes.length != 2) {
            throw new IllegalArgumentException("Invalid port byte array length");
        }
        int highByte = (portBytes[0] & 0xFF) << 8;
        int lowByte = portBytes[1] & 0xFF;
        return highByte | lowByte;
    }

    /**
     * ip地址转byte[]
     *
     * @param ipAddress
     * @return
     */
    public static byte[] ipAddressToBytes(String ipAddress) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            return inetAddress.getAddress();
        } catch (UnknownHostException e) {
            throw new BixException(e);
        }
    }

    /**
     * byte[]转ip地址
     *
     * @param bytes
     * @return
     */
    public static String bytesToIPAddress(byte[] bytes) {
        try {
            InetAddress inetAddress = InetAddress.getByAddress(bytes);
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            throw new BixException(e);
        }
    }
}
