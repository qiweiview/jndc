package com.view.jndc.core.v2.model.jndc;

import com.view.jndc.core.v2.constant.protocol_message.BitConstant;
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
    private Integer sourcePort;

    //2个字节0-65535
    private Integer proxyPort;

    //2个字节0-65535
    private Integer destPort;

    //4字节包长度
    private Integer dataSize;

    //n字节数据包
    private byte[] data;


    public static final JNDCData SAY_HI_WORLD = new JNDCData();

    static {
        SAY_HI_WORLD.setSourceAddress("0.0.0.0");
        SAY_HI_WORLD.setDestAddress("0.0.0.1");
        SAY_HI_WORLD.setSourcePort(666);
        SAY_HI_WORLD.setProxyPort(777);
        SAY_HI_WORLD.setDestPort(888);
        SAY_HI_WORLD.setType(BitConstant.HAPPY_EVERY_DAY_TYPE);
        byte version = 0x01;
        SAY_HI_WORLD.setVersion(version);
        SAY_HI_WORLD.setData("HELLO WORLD".getBytes());
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
