package jndc.core;

import jndc.utils.ByteArrayUtils;
import jndc.utils.HexUtils;
import jndc.utils.ObjectSerializableUtils;

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

    public static final byte TCP_DATA = 1;//tcp data transmission message

    public static final byte TCP_ACTIVE = 2;//tcp active message

    public static final byte MAP_REGISTER = 3;//client register message

    public static final byte CONNECTION_INTERRUPTED = 4;//server or client connection interrupted

    public static final byte NO_ACCESS = 5;//auth fail

    public static final byte USER_ERROR = 6;//throw by user

    public static final byte UN_CATCHABLE_ERROR = 7;//runtime unCatch

    public static final byte CHANNEL_HEART_BEAT = 8;//the channel heart beat

    public static final int UN_USED_PORT = 0;//the single package length

    /*--------------------- static variable ---------------------*/

    //the max length of single package,protocol just support 4 byte to this value,so the value need to less then Integer.MAX_VALUE
    public static final int AUTO_UNPACK_LENGTH = 5 * 1024 * 1024;

    public static final int FIX_LENGTH = 29;//the length of the fixed part of protocol

    public static final byte[] BLANK = "BLANK".getBytes();//magic variable

    public static final byte[] ACTIVE_MESSAGE = "ACTIVE_MESSAGE".getBytes();//magic variable

    private static final byte[] MAGIC = "NDC".getBytes();//magic variable


    /* ================= variable ================= */

    private byte version = 1;//protocol version

    private byte type;//data type

    private InetAddress remoteInetAddress;//remote ip

    private InetAddress localInetAddress;//local ip

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
        NDCMessageProtocol ndcMessageProtocol = new NDCMessageProtocol();
        ndcMessageProtocol.setVersion(getVersion());
        ndcMessageProtocol.setLocalInetAddress(getLocalInetAddress());
        ndcMessageProtocol.setRemoteInetAddress(getRemoteInetAddress());
        ndcMessageProtocol.setLocalPort(getLocalPort());
        ndcMessageProtocol.setServerPort(getServerPort());
        ndcMessageProtocol.setRemotePort(getRemotePort());
        ndcMessageProtocol.setType(getType());

        //clean data
        ndcMessageProtocol.setData("".getBytes());
        return ndcMessageProtocol;
    }


    /**
     * fast create message
     * @param remoteInetAddress the connector  ip
     * @param localInetAddress the map service ip
     * @param remotePort the connector port
     * @param serverPort the server port
     * @param localPort the map service port
     * @param type the message type
     * @return
     */
    public static NDCMessageProtocol of(InetAddress remoteInetAddress, InetAddress localInetAddress, int remotePort, int serverPort, int localPort, byte type) {
        NDCMessageProtocol NDCMessageProtocol = new NDCMessageProtocol();
        NDCMessageProtocol.setRemoteInetAddress(remoteInetAddress);
        NDCMessageProtocol.setLocalInetAddress(localInetAddress);
        NDCMessageProtocol.setLocalPort(localPort);
        NDCMessageProtocol.setServerPort(serverPort);
        NDCMessageProtocol.setRemotePort(remotePort);
        NDCMessageProtocol.setType(type);
        NDCMessageProtocol.setData("".getBytes());
        return NDCMessageProtocol;
    }





    /**
     * parse the fix part of data
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
            NDCMessageProtocol.setLocalInetAddress(InetAddress.getByAddress(localInetAddress));
            NDCMessageProtocol.setRemoteInetAddress(InetAddress.getByAddress(remoteInetAddress));
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
            byteArrayOutputStream.write(localInetAddress.getAddress());//4 byte -->9
            byteArrayOutputStream.write(remoteInetAddress.getAddress());//4 byte -->13
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
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T getObject(Class<T> tClass) {
        byte[] data = getData();
        if (data == null) {
            throw new RuntimeException("byte empty");
        }
        T t = ObjectSerializableUtils.bytes2object(data, tClass);
        return t;
    }


    /* --------------------------getter setter-------------------------- */


    @Override
    public String toString() {
        return "NDCMessageProtocol{" +
                "version=" + version +
                ", type=" + type +
                ", remoteInetAddress=" + remoteInetAddress +
                ", localInetAddress=" + localInetAddress +
                ", remotePort=" + remotePort +
                ", serverPort=" + serverPort +
                ", localPort=" + localPort +
                ", dataSize=" + dataSize +
                '}';
    }

    public byte getVersion() {
        return version;
    }


    public void setVersion(byte version) {
        this.version = version;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public InetAddress getRemoteInetAddress() {
        return remoteInetAddress;
    }

    public void setRemoteInetAddress(InetAddress remoteInetAddress) {
        this.remoteInetAddress = remoteInetAddress;
    }

    public InetAddress getLocalInetAddress() {
        return localInetAddress;
    }

    public void setLocalInetAddress(InetAddress localInetAddress) {
        this.localInetAddress = localInetAddress;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
