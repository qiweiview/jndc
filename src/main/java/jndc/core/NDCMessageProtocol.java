package jndc.core;

import jndc.utils.ByteArrayUtils;
import jndc.utils.HexUtils;
import jndc.utils.LogPrint;
import jndc.utils.ObjectSerializableUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * No Distance Connection Protocol
 */
public class NDCMessageProtocol {

    /*--------------------- types ---------------------*/

    public static final int TCP_DATA = 1;//tcp data transmission message

    public static final int TCP_ACTIVE = 2;//tcp active message

    public static final int MAP_REGISTER = 3;//client register message

    public static final int CONNECTION_INTERRUPTED = 4;//server or client connection interrupted

    public static final int NO_ACCESS = 5;//auth fail

    public static final int USER_ERROR = 6;//throw by user

    public static final int UN_CATCHABLE_ERROR = 7;//runtime unCatch



    /*--------------------- static variable ---------------------*/

    public static final int AUTO_UNPACK_LENGTH = 5 * 1024 * 1024;//the single package length

    public static final int FIX_LENGTH = 33;//the length of the fixed part of protocol

    public static final byte[] ACTIVE_MESSAGE = "ACTIVE_MESSAGE".getBytes();//magic variable

    private static final byte[] MAGIC = "NDC".getBytes();//magic variable


    /* ================= variable ================= */

    private int version = 1;//protocol version

    private int type;//data type

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


    public void inetSwap() {
        InetAddress temp = remoteInetAddress;
        remoteInetAddress = localInetAddress;
        localInetAddress = temp;
    }


    public static NDCMessageProtocol of(InetAddress remoteInetAddress, InetAddress localInetAddress, int remotePort, int serverPort, int localPort, int type) {
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


    public static NDCMessageProtocol parseFixInfo(byte[] bytes) {
        if (bytes.length < FIX_LENGTH) {
            throw new RuntimeException("unSupportFormat");
        }

        if (Arrays.compare(MAGIC, Arrays.copyOfRange(bytes, 0, 3)) != 0) {
            throw new RuntimeException("unSupportProtocol");
        }

        Integer version = HexUtils.hex2Integer(Arrays.copyOfRange(bytes, 3, 5));

        int type = Arrays.copyOfRange(bytes, 5, 6)[0];


        byte[] localInetAddress = Arrays.copyOfRange(bytes, 6, 10);

        byte[] remoteInetAddress = Arrays.copyOfRange(bytes, 10, 14);

        int localPort = HexUtils.hex2Integer(Arrays.copyOfRange(bytes, 14, 18));

        int serverPort = HexUtils.hex2Integer(Arrays.copyOfRange(bytes, 18, 22));

        int remotePort = HexUtils.hex2Integer(Arrays.copyOfRange(bytes, 22, 26));


        int dataSize = HexUtils.hex2Integer(Arrays.copyOfRange(bytes, 26, FIX_LENGTH));

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

    public static NDCMessageProtocol parseTotal(byte[] bytes) {
        NDCMessageProtocol NDCMessageProtocol = parseFixInfo(bytes);
        byte[] data = Arrays.copyOfRange(bytes, FIX_LENGTH, FIX_LENGTH + NDCMessageProtocol.getDataSize());
        NDCMessageProtocol.setDataWithVerification(data);
        return NDCMessageProtocol;
    }


    public void setDataWithVerification(byte[] bytes) {
        if (bytes.length < dataSize) {
            throw new RuntimeException("broken data");
        }
        setData(bytes);
    }

    /**
     * auto unpack
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

    public byte[] toByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byteArrayOutputStream.write(MAGIC);//3 byte
            byteArrayOutputStream.write(HexUtils.fillBlank(version, 2).getBytes());//2 byte   -->5
            byteArrayOutputStream.write(type);//1 byte -->6
            byteArrayOutputStream.write(localInetAddress.getAddress());//4 byte -->10
            byteArrayOutputStream.write(remoteInetAddress.getAddress());//4 byte -->14
            byteArrayOutputStream.write(HexUtils.fillBlank(localPort, 4).getBytes());//4 byte -->18
            byteArrayOutputStream.write(HexUtils.fillBlank(serverPort, 4).getBytes());//4 byte -->22
            byteArrayOutputStream.write(HexUtils.fillBlank(remotePort, 4).getBytes());//4 byte -->26

            dataSize = data.length;
            if (dataSize > AUTO_UNPACK_LENGTH) {
                throw new RuntimeException("too long data,need run autoUnpack()");
            }
            byteArrayOutputStream.write(HexUtils.fillBlank(dataSize, 7).getBytes());//7 byte -->33
            byteArrayOutputStream.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }


    public <T> T getObject(Class<T> tClass) {
        byte[] data = getData();
        if (data == null) {
            throw new RuntimeException("byte empty");
        }
        T t = ObjectSerializableUtils.bytes2object(data, tClass);
        return t;
    }


    public Integer getVersion() {
        return version;
    }


    public void setVersion(int version) {
        if (version > 0xff) {
            throw new RuntimeException("version must less than 255");
        }
        this.version = version;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
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
