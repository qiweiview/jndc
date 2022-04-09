package jndc.core;

import jndc.utils.ByteArrayUtils;
import jndc.utils.HexUtils;
import jndc.utils.ObjectSerializableUtils;
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
public class NDCMessageProtocol {


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
            4byte
|          data length         |
 --------------------------------
    data length byte
|            data              |
 --------------------------------

*/

    /*--------------------- message types ---------------------*/

    public static final byte TCP_DATA = 0x01;//数据包

    public static final byte TCP_ACTIVE = 0x02;//通道打开数据包

    public static final byte SERVICE_REGISTER = 0x03;//服务注册数据包

    public static final byte SERVICE_UNREGISTER = 0x04;//服务取消注册数据包

    public static final byte CONNECTION_INTERRUPTED = 0x05;//服务中断数据包

    public static final byte NO_ACCESS = 0x06;//鉴权不通过数据包

    public static final byte USER_ERROR = 0x07;//业务异常数据包

    public static final byte UN_CATCHABLE_ERROR = 0x08;//系统异常数据包

    public static final byte CHANNEL_HEART_BEAT = 0x09;//心跳数据包

    public static final byte OPEN_CHANNEL = 0X0A;//通道打开数据包


    /*--------------------- static variable ---------------------*/
    public static final int UN_USED_PORT = 0;//the single package length

    //the max length of single package,protocol just support 4 byte to this value,so the value need to less then Integer.MAX_VALUE
    public static final int AUTO_UNPACK_LENGTH = 5 * 1024 * 1024;

    public static final int FIX_LENGTH = 29;//the length of the fixed part of protocol

    public static final byte[] BLANK = "BLANK".getBytes();//magic variable

    public static final byte[] ACTIVE_MESSAGE = "ACTIVE_MESSAGE".getBytes();//magic variable

    private static final byte[] MAGIC = "NDC".getBytes();//magic variable


    /* ================= variable ================= */

    private byte version = 1;//protocol version

    private byte type;//data type

    private InetAddress remoteAddress;//remote ip

    private InetAddress localAddress;//local ip

    private int remotePort;

    private int serverPort;

    private int localPort;

    private int dataSize;

    private byte[] data;


    private NDCMessageProtocol() {

    }

    /**
     * need optimization
     */
    public NDCMessageProtocol copy() {
        return copy(true);
    }

    private NDCMessageProtocol copy(boolean withData) {
        NDCMessageProtocol ndcMessageProtocol = new NDCMessageProtocol();
        ndcMessageProtocol.setVersion(getVersion());
        ndcMessageProtocol.setLocalAddress(getLocalAddress());
        ndcMessageProtocol.setRemoteAddress(getRemoteAddress());
        ndcMessageProtocol.setLocalPort(getLocalPort());
        ndcMessageProtocol.setServerPort(getServerPort());
        ndcMessageProtocol.setRemotePort(getRemotePort());
        ndcMessageProtocol.setType(getType());

        //clear data
        if (withData) {
            //todo 清除数据
            ndcMessageProtocol.setData("".getBytes());
        } else {
            //todo 复制数据
            ndcMessageProtocol.setData(getData());
        }

        return ndcMessageProtocol;
    }

    public NDCMessageProtocol copyWithData() {
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
    public static NDCMessageProtocol of(InetAddress remoteAddress, InetAddress localAddress, int remotePort, int serverPort, int localPort, byte type) {
        NDCMessageProtocol NDCMessageProtocol = new NDCMessageProtocol();
        NDCMessageProtocol.setRemoteAddress(remoteAddress);
        NDCMessageProtocol.setLocalAddress(localAddress);
        NDCMessageProtocol.setLocalPort(localPort);
        NDCMessageProtocol.setServerPort(serverPort);
        NDCMessageProtocol.setRemotePort(remotePort);
        NDCMessageProtocol.setType(type);
        NDCMessageProtocol.setData(new byte[0]);
        return NDCMessageProtocol;
    }


    /**
     * parse the fix part of data
     *
     * @param bytes
     * @return
     */
    public static NDCMessageProtocol parseFixInfo(byte[] bytes) {
        if (bytes.length < FIX_LENGTH) {
            throw new RuntimeException("unSupportFormat");
        }


        //replace with Arrays.compare in jdk 9
        if (!Arrays.equals(MAGIC, Arrays.copyOfRange(bytes, 0, 3))) {
            throw new RuntimeException("unSupportProtocol");
        }

        byte version = Arrays.copyOfRange(bytes, 3, 4)[0];

        byte type = Arrays.copyOfRange(bytes, 4, 5)[0];


        byte[] localInetAddress = Arrays.copyOfRange(bytes, 5, 9);

        byte[] remoteInetAddress = Arrays.copyOfRange(bytes, 9, 13);

        int localPort = HexUtils.byteArray2Int(Arrays.copyOfRange(bytes, 13, 17));

        int serverPort = HexUtils.byteArray2Int(Arrays.copyOfRange(bytes, 17, 21));

        int remotePort = HexUtils.byteArray2Int(Arrays.copyOfRange(bytes, 21, 25));


        int dataSize = HexUtils.byteArray2Int(Arrays.copyOfRange(bytes, 25, FIX_LENGTH));

        NDCMessageProtocol NDCMessageProtocol = new NDCMessageProtocol();
        NDCMessageProtocol.setVersion(version);
        NDCMessageProtocol.setType(type);
        try {
            NDCMessageProtocol.setLocalAddress(InetAddress.getByAddress(localInetAddress));
            NDCMessageProtocol.setRemoteAddress(InetAddress.getByAddress(remoteInetAddress));
        } catch (UnknownHostException e) {
            throw new RuntimeException("UnknownHostException");
        }
        NDCMessageProtocol.setLocalPort(localPort);
        NDCMessageProtocol.setServerPort(serverPort);
        NDCMessageProtocol.setRemotePort(remotePort);
        NDCMessageProtocol.setDataSize(dataSize);
        return NDCMessageProtocol;
    }

    /**
     * decode
     *
     * @param bytes
     * @return
     */
    public static NDCMessageProtocol parseTotal(byte[] bytes) {
        NDCMessageProtocol NDCMessageProtocol = parseFixInfo(bytes);
        byte[] data = Arrays.copyOfRange(bytes, FIX_LENGTH, FIX_LENGTH + NDCMessageProtocol.getDataSize());
        NDCMessageProtocol.setDataWithVerification(data);
        return NDCMessageProtocol;
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
    public List<NDCMessageProtocol> autoUnpack() {
        List<NDCMessageProtocol> ndcMessageProtocols = new ArrayList<>();
        byte[] data = getData();
        List<byte[]> list = ByteArrayUtils.bytesUnpack(data, AUTO_UNPACK_LENGTH);
        list.forEach(x -> {
            NDCMessageProtocol copy = copy();
            copy.setData(x);
            ndcMessageProtocols.add(copy);
        });
        return ndcMessageProtocols;
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

            dataSize = data.length;
            if (dataSize > AUTO_UNPACK_LENGTH) {
                throw new RuntimeException("too long data,need run autoUnpack()");
            }
            byteArrayOutputStream.write(HexUtils.int2ByteArray(dataSize));//4 byte -->29
            byteArrayOutputStream.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }


    /**
     * byte array to object
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


}
