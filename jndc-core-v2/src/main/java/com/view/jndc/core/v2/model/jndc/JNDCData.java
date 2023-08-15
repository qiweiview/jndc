package com.view.jndc.core.v2.model.jndc;

import com.view.jndc.core.v2.constant.protocol_message.BitConstant;
import com.view.jndc.core.v2.model.protocol_message.JNDCDataMessage;
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
    public JNDCDataMessage authMessage() {
        return toMessage(BitConstant.MESSAGE_AUTH_TYPE);
    }

    /**
     * 转为消息
     *
     * @param type
     * @return
     */
    public JNDCDataMessage toMessage(byte type) {
        JNDCDataMessage jndcDataMessage = new JNDCDataMessage();
        jndcDataMessage.setSourceAddress(ByteConversionUtil.ipAddressToBytes(sourceAddress));
        jndcDataMessage.setDestAddress(ByteConversionUtil.ipAddressToBytes(destAddress));
        jndcDataMessage.setSourcePort(ByteConversionUtil.portToBytes(sourcePort));
        jndcDataMessage.setProxyPort(ByteConversionUtil.portToBytes(proxyPort));
        jndcDataMessage.setDestPort(ByteConversionUtil.portToBytes(destPort));
        jndcDataMessage.setData(data);
        jndcDataMessage.setType(type);
        jndcDataMessage.setTag(BitConstant.NDC_PROTOCOL_TAG);
        return jndcDataMessage;
    }


    /**
     * 转为消息
     *
     * @param jndcDataMessage
     * @return
     */
    public static JNDCData parse(JNDCDataMessage jndcDataMessage) {
        JNDCData jndcData = new JNDCData();
        jndcData.setSourceAddress(ByteConversionUtil.bytesToIPAddress(jndcDataMessage.getSourceAddress()));
        jndcData.setDestAddress(ByteConversionUtil.bytesToIPAddress(jndcDataMessage.getDestAddress()));
        jndcData.setSourcePort(ByteConversionUtil.bytesToPort(jndcDataMessage.getSourcePort()));
        jndcData.setProxyPort(ByteConversionUtil.bytesToPort(jndcDataMessage.getProxyPort()));
        jndcData.setDestPort(ByteConversionUtil.bytesToPort(jndcDataMessage.getDestPort()));
        jndcData.setData(jndcDataMessage.getData());

        return jndcData;
    }
}
