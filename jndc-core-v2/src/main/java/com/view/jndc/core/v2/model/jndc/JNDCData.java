package com.view.jndc.core.v2.model.jndc;

import com.view.jndc.core.v2.constant.protocol_message.BitConstant;
import com.view.jndc.core.v2.model.protocol_message.JNDCEncoded;
import com.view.jndc.core.v2.utils.ByteConversionUtil;
import lombok.Data;

@Data
public class JNDCData {


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

    private byte[] data;


    /**
     * 鉴权类型消息
     *
     * @return
     */
    public JNDCEncoded authMessage() {
        return toMessage(BitConstant.MESSAGE_AUTH_TYPE);
    }

    /**
     * 转为消息
     *
     * @param type
     * @return
     */
    public JNDCEncoded toMessage(byte type) {
        JNDCEncoded jndcEncoded = new JNDCEncoded();
        jndcEncoded.setSourceAddress(ByteConversionUtil.ipAddressToBytes(sourceAddress));
        jndcEncoded.setDestAddress(ByteConversionUtil.ipAddressToBytes(destAddress));
        jndcEncoded.setSourcePort(ByteConversionUtil.portToBytes(sourcePort));
        jndcEncoded.setProxyPort(ByteConversionUtil.portToBytes(proxyPort));
        jndcEncoded.setDestPort(ByteConversionUtil.portToBytes(destPort));
        jndcEncoded.setData(data);
        jndcEncoded.setType(type);
        jndcEncoded.setTag(BitConstant.NDC_PROTOCOL_TAG);
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
        jndcData.setSourceAddress(ByteConversionUtil.bytesToIPAddress(jndcEncoded.getSourceAddress()));
        jndcData.setDestAddress(ByteConversionUtil.bytesToIPAddress(jndcEncoded.getDestAddress()));
        jndcData.setSourcePort(ByteConversionUtil.bytesToPort(jndcEncoded.getSourcePort()));
        jndcData.setProxyPort(ByteConversionUtil.bytesToPort(jndcEncoded.getProxyPort()));
        jndcData.setDestPort(ByteConversionUtil.bytesToPort(jndcEncoded.getDestPort()));
        jndcData.setData(jndcEncoded.getData());

        return jndcData;
    }
}
