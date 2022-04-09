package jndc.utils;


import java.nio.ByteBuffer;

public class HexUtils {


    /**
     * return byte array with fix size 4
     * @param i
     * @return
     */
    public static byte[] int2ByteArray(int i){
        ByteBuffer allocate = ByteBuffer.allocate(4);
        byte[] array =allocate.putInt(i).array();
        return array;
    }

    /**
     * accept a fix length byte array
     * @param bytes
     * @return
     */
    public static int byteArray2Int(byte[] bytes){
        if (bytes.length!=4){
            throw new RuntimeException("not a int value");
        }
        ByteBuffer wrap = ByteBuffer.wrap(bytes);
        int anInt = wrap.getInt();
        return anInt;
    }

}
