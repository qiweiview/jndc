package com.view.core.protocol;

import com.view.core.utils.ByteArrayUtils;
import com.view.core.utils.HexUtils;
import com.view.core.utils.ObjectSerializableUtils;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * No Distance Connection Protocol
 */
@Data
public class NDCPacket {


 /*
 protocol review

 ---------ndc protocol ----------
  3byte      1byte      1byte
|  ndc   |  version  |  type   |
 --------------------------------
            4byte
|          local ip            |
 --------------------------------
            4byte
|          remote ip           |
 --------------------------------
            4byte
|          local port          |
 --------------------------------
            4byte
|          server port         |
 --------------------------------
            4byte
|          remote port         |
 --------------------------------
            8byte
|        timestamp (ms)        |
 --------------------------------
             4byte
|          data length         |
 --------------------------------
    data length byte
|            data              |
 --------------------------------

*/

    /*--------------------- message types ---------------------*/

    public static final byte TCP_DATA = 0x01;//数据包

    public static final byte TCP_ACTIVE = 0x02;//TCP建立数据包

    public static final byte SERVICE_REGISTER = 0x03;//服务注册数据包

    public static final byte SERVICE_UNREGISTER = 0x04;//服务取消注册数据包

    public static final byte TCP_IN_ACTIVE = 0x05;//TCP关闭数据包

    public static final byte READ_TO_ACCEPT_PACKAGE = 0x06;//准备接收数据包

    public static final byte USER_ERROR = 0x07;//业务异常数据包

    public static final byte OPEN_CHANNEL = 0X08;//通道打开数据包


    /*--------------------- static variable ---------------------*/
    public static final byte[] BLANK_DATA = new byte[0];

    public static final InetAddress BLANK_ADDRESS;

    static {
        try {
            BLANK_ADDRESS = InetAddress.getByName("");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    //the single package length
    public static final int UN_USED_PORT = 0;

    //the max length of single package
    //protocol just support 4 byte to this value,so the max value is 2^31-1
    public static final int AUTO_UNPACK_LENGTH = 5 * 1024 * 1024;

    //the length of the fixed part of protocol
    public static final int FIX_LENGTH = 37;

    //magic variable
    public static final byte[] BLANK = "BLANK".getBytes();

    //magic variable
    public static final byte[] ACTIVE_MESSAGE = "ACTIVE_MESSAGE".getBytes();

    //magic variable
    private static final byte[] MAGIC = "NDC".getBytes();


    /* ================= variable ================= */

    //protocol version
    private byte version = 1;

    //data type
    private byte type;

    //remote ip
    private InetAddress remoteAddress;

    //local ip
    private InetAddress localAddress;

    private int remotePort;

    private int serverPort;

    private int localPort;

    private long timestamp = System.currentTimeMillis();

    private int dataSize;

    private byte[] data;


    /**
     * need optimization
     */
    public NDCPacket copy() {
        return copy(true);
    }

    private NDCPacket copy(boolean withData) {
        NDCPacket ndcPacket = new NDCPacket();
        ndcPacket.setVersion(getVersion());
        ndcPacket.setLocalAddress(getLocalAddress());
        ndcPacket.setRemoteAddress(getRemoteAddress());
        ndcPacket.setLocalPort(getLocalPort());
        ndcPacket.setServerPort(getServerPort());
        ndcPacket.setRemotePort(getRemotePort());
        ndcPacket.setType(getType());
        ndcPacket.setTimestamp(getTimestamp());

        //clear data
        if (withData) {
            //todo 清除数据
            ndcPacket.setData("".getBytes());
        } else {
            //todo 复制数据
            ndcPacket.setData(getData());
        }

        return ndcPacket;
    }

    public NDCPacket copyWithData() {
        return copy(false);
    }


    /**
     * fast create message
     *
     * @param remoteAddress the connector  ip
     * @param localAddress  the map service ip
     * @param remotePort    the connector port
     * @param serverPort    the server port
     * @param localPort     the map service port
     * @param type          the message type
     * @return
     */
    public static NDCPacket of(InetAddress remoteAddress,
                               InetAddress localAddress,
                               int remotePort,
                               int serverPort,
                               int localPort,
                               byte type,
                               long timestamp) {

        NDCPacket NdcPacket = new NDCPacket();
        NdcPacket.setRemoteAddress(remoteAddress);
        NdcPacket.setLocalAddress(localAddress);
        NdcPacket.setLocalPort(localPort);
        NdcPacket.setServerPort(serverPort);
        NdcPacket.setRemotePort(remotePort);
        NdcPacket.setType(type);
        NdcPacket.setTimestamp(timestamp);
        NdcPacket.setData(new byte[0]);
        return NdcPacket;
    }


    /**
     * parse the fix part of data
     *
     * @param bytes
     * @return
     */
    public static NDCPacket parseFixInfo(byte[] bytes) {
        if (bytes.length < FIX_LENGTH) {
            throw new RuntimeException("unSupportFormat");
        }


        //replace with Arrays.compare in jdk 9
        if (!Arrays.equals(MAGIC, Arrays.copyOfRange(bytes, 0, 3))) {//3 byte
            throw new RuntimeException("unSupportProtocol");
        }

        byte version = Arrays.copyOfRange(bytes, 3, 4)[0];//1 byte

        byte type = Arrays.copyOfRange(bytes, 4, 5)[0];//1 byte

        byte[] localInetAddress = Arrays.copyOfRange(bytes, 5, 9);//4 byte

        byte[] remoteInetAddress = Arrays.copyOfRange(bytes, 9, 13);//4 byte

        int localPort = HexUtils.byteArray2Int(Arrays.copyOfRange(bytes, 13, 17));//4 byte

        int serverPort = HexUtils.byteArray2Int(Arrays.copyOfRange(bytes, 17, 21));//4 byte

        int remotePort = HexUtils.byteArray2Int(Arrays.copyOfRange(bytes, 21, 25));//4 byte

        long timestamp = HexUtils.byteArray2Long(Arrays.copyOfRange(bytes, 25, 33));//8 byte

        int dataSize = HexUtils.byteArray2Int(Arrays.copyOfRange(bytes, 33, FIX_LENGTH));//4 byte

        NDCPacket NdcPacket = new NDCPacket();
        NdcPacket.setVersion(version);
        NdcPacket.setType(type);
        try {
            NdcPacket.setLocalAddress(InetAddress.getByAddress(localInetAddress));
            NdcPacket.setRemoteAddress(InetAddress.getByAddress(remoteInetAddress));
        } catch (UnknownHostException e) {
            //todo 未知主机异常
            throw new RuntimeException("UnknownHostException");
        }
        NdcPacket.setLocalPort(localPort);
        NdcPacket.setServerPort(serverPort);
        NdcPacket.setRemotePort(remotePort);
        NdcPacket.setDataSize(dataSize);
        NdcPacket.setTimestamp(timestamp);
        return NdcPacket;
    }


    /**
     * need verification
     *
     * @param bytes
     */
    public void setDataWithVerification(byte[] bytes) {
        if (bytes.length < dataSize) {
            throw new RuntimeException("broken data");
        }
        setData(bytes);
    }

    /**
     * auto unpack
     *
     * @return
     */
    public List<NDCPacket> autoUnpack() {
        List<NDCPacket> NDCPackets = new ArrayList<>();
        byte[] data = getData();
        List<byte[]> list = ByteArrayUtils.bytesUnpack(data, AUTO_UNPACK_LENGTH);
        list.forEach(x -> {
            NDCPacket copy = copy();
            copy.setData(x);
            NDCPackets.add(copy);
        });
        return NDCPackets;
    }

    /**
     * 裁剪字节数组
     *
     * @param bytes
     * @param length
     * @return
     */
    private byte[] cropByteArray(byte[] bytes, int length) {
        if (bytes.length <= length) {
            return bytes;
        }
        return Arrays.copyOf(bytes, length);
    }

    /**
     * encode
     *
     * @return
     */
    public byte[] toByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byteArrayOutputStream.write(MAGIC);//3 byte
            byteArrayOutputStream.write(version);//1 byte   -->4
            byteArrayOutputStream.write(type);//1 byte -->5
            byteArrayOutputStream.write(cropByteArray(localAddress.getAddress(), 4));//4 byte -->9
            byteArrayOutputStream.write(cropByteArray(remoteAddress.getAddress(), 4));//4 byte -->13
            byteArrayOutputStream.write(HexUtils.int2ByteArray(localPort));//4 byte -->17
            byteArrayOutputStream.write(HexUtils.int2ByteArray(serverPort));//4 byte -->21
            byteArrayOutputStream.write(HexUtils.int2ByteArray(remotePort));//4 byte -->25
            byteArrayOutputStream.write(HexUtils.longToBytes(timestamp));//8 byte -->33

            dataSize = data.length;
            if (dataSize > AUTO_UNPACK_LENGTH) {
                throw new RuntimeException("too long data,need run autoUnpack()");
            }
            byteArrayOutputStream.write(HexUtils.int2ByteArray(dataSize));//4 byte -->37
            byteArrayOutputStream.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }


    /**
     * get object
     *
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T getObject(Class<T> tClass) {
        byte[] data = getData();
        if (data == null || data.length == 0) {
            throw new RuntimeException("byte empty");
        }
        T t = ObjectSerializableUtils.bytes2object(data, tClass);
        return t;
    }


    /**
     * 计算单包延迟
     * @return
     */
    public long packageTimeout() {
        return System.currentTimeMillis() - timestamp;
    }
}
