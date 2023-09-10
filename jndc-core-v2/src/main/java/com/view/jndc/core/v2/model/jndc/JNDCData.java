package com.view.jndc.core.v2.model.jndc;

import com.view.jndc.core.v2.constant.protocol_message.BitConstant;
import com.view.jndc.core.v2.enum_value.JNDCMessageType;
import com.view.jndc.core.v2.model.protocol_message.JNDCEncoded;
import com.view.jndc.core.v2.utils.ByteConversionUtil;
import lombok.Data;

import java.util.Arrays;

@Data
public class JNDCData {


    //协议版本 1字节
    private byte version;

    //报文类型 1字节
    private byte type;//data type

    //ipv4 4字节
    private String sourceAddress;//remote ip

    //ipv4 4字节
    private String destAddress;//local ip

    //2个字节0-65535
    private int sourcePort;

    //2个字节0-65535
    private int proxyPort;

    //2个字节0-65535
    private int destPort;

    //4字节包长度
    private int dataSize;

    //n字节数据包
    private byte[] data;


    public static final JNDCData SAY_HELLO_TO_WORLD = new JNDCData();

    static {
        SAY_HELLO_TO_WORLD.setSourceAddress("0.0.0.0");
        SAY_HELLO_TO_WORLD.setDestAddress("0.0.0.0");
        SAY_HELLO_TO_WORLD.setSourcePort(666);
        SAY_HELLO_TO_WORLD.setProxyPort(777);
        SAY_HELLO_TO_WORLD.setDestPort(888);
        SAY_HELLO_TO_WORLD.setType(JNDCMessageType.HAPPY_EVERY_DAY.value);
        SAY_HELLO_TO_WORLD.setVersion(BitConstant.PROTOCOL_VERSION);
//        SAY_HELLO_TO_WORLD.setData("HELLO WORLD".getBytes());
        SAY_HELLO_TO_WORLD.setData(BitConstant.EMPTY_BYTE_ARRAY);
    }

    /**
     * 打开通道
     *
     * @return
     */
    public static JNDCData getThin() {
        JNDCData jndcData = new JNDCData();
        jndcData.setSourceAddress("0.0.0.0");
        jndcData.setDestAddress("0.0.0.0");
        jndcData.setVersion(BitConstant.PROTOCOL_VERSION);
        jndcData.setData(BitConstant.EMPTY_BYTE_ARRAY);
        return jndcData;
    }

    public static JNDCData testBandwidth() {
        JNDCData jndcData = getThin();
        jndcData.setType(JNDCMessageType.TEST_BANDWIDTH_0X20.value);
        jndcData.setData(BitConstant.MB128);
        return jndcData;
    }


    public static JNDCData createConnectionActiveType() {
        JNDCData jndcData = getThin();
        jndcData.setType(JNDCMessageType.CHANNEL_SERVICE_0X12.value);
        return jndcData;
    }

    public static JNDCData createConnectionInActiveType() {
        JNDCData jndcData = getThin();
        jndcData.setType(JNDCMessageType.CHANNEL_SERVICE_0X14.value);
        return jndcData;
    }

    public static JNDCData createDataTransmissionType() {
        JNDCData jndcData = getThin();
        jndcData.setType(JNDCMessageType.CHANNEL_SERVICE_0X13.value);
        return jndcData;
    }

    public static JNDCData createOpenChannelType() {
        JNDCData jndcData = getThin();
        jndcData.setType(JNDCMessageType.CHANNEL_0X10.value);
        return jndcData;
    }

    public static JNDCData createServiceRegisterType() {
        JNDCData jndcData = getThin();
        jndcData.setType(JNDCMessageType.CHANNEL_SERVICE_0X11.value);
        return jndcData;
    }


    /**
     * 转为消息
     *
     * @return
     */
    public JNDCEncoded toEncoded() {
        JNDCEncoded jndcEncoded = new JNDCEncoded();
        //固定头
        jndcEncoded.setTag(BitConstant.NDC_PROTOCOL_TAG);
        jndcEncoded.setVersion(version);
        jndcEncoded.setType(type);
        jndcEncoded.setSourceAddress(ByteConversionUtil.ipAddressToBytes(sourceAddress));
        jndcEncoded.setDestAddress(ByteConversionUtil.ipAddressToBytes(destAddress));
        jndcEncoded.setSourcePort(ByteConversionUtil.portToBytes(sourcePort));
        jndcEncoded.setProxyPort(ByteConversionUtil.portToBytes(proxyPort));
        jndcEncoded.setDestPort(ByteConversionUtil.portToBytes(destPort));

        jndcEncoded.setData(data);

        return jndcEncoded;
    }


    /**
     * 转为消息对象
     *
     * @param jndcEncoded
     * @return
     */
    public static JNDCData parse(JNDCEncoded jndcEncoded) {
        JNDCData jndcData = new JNDCData();
        jndcData.setVersion(jndcEncoded.getVersion());
        jndcData.setType(jndcEncoded.getType());
        jndcData.setSourceAddress(ByteConversionUtil.bytesToIPAddress(jndcEncoded.getSourceAddress()));
        jndcData.setSourceAddress(ByteConversionUtil.bytesToIPAddress(jndcEncoded.getSourceAddress()));
        jndcData.setDestAddress(ByteConversionUtil.bytesToIPAddress(jndcEncoded.getDestAddress()));
        jndcData.setSourcePort(ByteConversionUtil.bytesToPort(jndcEncoded.getSourcePort()));
        jndcData.setProxyPort(ByteConversionUtil.bytesToPort(jndcEncoded.getProxyPort()));
        jndcData.setDestPort(ByteConversionUtil.bytesToPort(jndcEncoded.getDestPort()));
        jndcData.setData(jndcEncoded.getData());
        jndcData.setDataSize(ByteConversionUtil.byteArrayToInt(jndcEncoded.getDataSize()));

        return jndcData;
    }

    @Override
    public String toString() {
        return "JNDCData{" +
                "version=" + ByteConversionUtil.byteToHex(version) +
                ", type=" + ByteConversionUtil.byteToHex(type) +
                ", sourceAddress='" + sourceAddress + '\'' +
                ", destAddress='" + destAddress + '\'' +
                ", sourcePort=" + sourcePort +
                ", proxyPort=" + proxyPort +
                ", destPort=" + destPort +
                ", dataSize=" + dataSize +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
