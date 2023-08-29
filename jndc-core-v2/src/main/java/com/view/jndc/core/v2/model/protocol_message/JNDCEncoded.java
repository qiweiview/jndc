package com.view.jndc.core.v2.model.protocol_message;


import com.view.jndc.core.v2.constant.protocol_message.BitConstant;
import com.view.jndc.core.v2.constant.protocol_message.StaticConfig;
import com.view.jndc.core.v2.exception.BixException;
import com.view.jndc.core.v2.utils.ByteArrayUtils;
import com.view.jndc.core.v2.utils.ByteConversionUtil;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 协议报文对象
 */
@Data
public class JNDCEncoded {


 /*

        [protocol review]

         --------- ndc protocol ----------
          1byte      1byte      1byte
        |  0x71(启)|  version  |  type  |
         --------------------------------
                    4byte
        |          source ip            |
         --------------------------------
                    4byte
        |          dest ip              |
         --------------------------------
                    2byte
        |          source port          |
         --------------------------------
                    2byte
        |          proxy port           |
         --------------------------------
                    2byte
        |          dest port            |
         --------------------------------
                    4byte
        |          data length          |
         --------------------------------
            data length byte
        |            data               |
         --------------------------------

*/


//    /*--------------------- static variable ---------------------*/
//    public static final int UN_USED_PORT = 0;//the single package length
//
//    //the max length of single package,protocol just support 4 byte to this value,so the value need to less then Integer.MAX_VALUE
//    public static final int AUTO_UNPACK_LENGTH = 5 * 1024 * 1024;
//
//    public static final int FIX_LENGTH = 29;//the length of the fixed part of protocol
//
//    public static final byte[] BLANK = "BLANK".getBytes();//magic variable
//
//    public static final byte[] ACTIVE_MESSAGE = "ACTIVE_MESSAGE".getBytes();//magic variable
//
//    private static final byte[] MAGIC = "NDC".getBytes();//magic variable


    /* ================= variable ================= */
    //协议标识 3字节
    private byte[] tag;

    //协议版本 1字节
    private byte version;

    //报文类型 1字节
    private byte type;//data type

    //ipv4 4字节
    private byte[] sourceAddress;//remote ip

    //ipv4 4字节
    private byte[] destAddress;//local ip

    //2个字节0-65535
    private byte[] sourcePort;

    //2个字节0-65535
    private byte[] proxyPort;

    //2个字节0-65535
    private byte[] destPort;

    //4字节包长度
    private byte[] dataSize;

    //n字节数据包
    private byte[] data;


    /**
     * 转为传输格式
     *
     * @return
     */
    public byte[] toTransferFormat() {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byteArrayOutputStream.write(tag);//3 byte
            byteArrayOutputStream.write(version);//1 byte   -->4
            byteArrayOutputStream.write(type);//1 byte -->5
            byteArrayOutputStream.write(sourceAddress);//4 byte -->9
            byteArrayOutputStream.write(destAddress);//4 byte -->13
            byteArrayOutputStream.write(sourcePort);//2 byte -->15
            byteArrayOutputStream.write(proxyPort);//2 byte -->17
            byteArrayOutputStream.write(destPort);//2 byte -->19


            if (data == null) {
                //todo 补全
                data = new byte[0];
            }


            int length = data.length;
            if (length > StaticConfig.AUTOMATIC_UNPACKING_LENGTH) {
                throw new BixException("超过拆包限定长度");
            } else {
                //计算数据长度
                dataSize = calculateDataSize();
                byteArrayOutputStream.write(dataSize);//4 byte -->23
            }
            byteArrayOutputStream.write(data);//n byte
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new BixException("ByteArrayOutputStream构建失败");
        }

    }

    /**
     * 计算长度
     *
     * @return
     */
    private byte[] calculateDataSize() {
        return ByteConversionUtil.intToByteArray(data.length);
    }


    /**
     * 转为编码格式
     *
     * @param bytes
     * @return
     */
    public static JNDCEncoded toEncodedFormat(byte[] bytes) {

        JNDCEncoded jndcEncoded = new JNDCEncoded();


        if (bytes.length < StaticConfig.MESSAGE_VERIFICATION_LENGTH) {
            throw new BixException("错误报文");
        }


        //replace with Arrays.compare in jdk 9
        if (!Arrays.equals(BitConstant.NDC_PROTOCOL_TAG, Arrays.copyOfRange(bytes, 0, 3))) {
            throw new BixException("不支持的协议类型");
        }

        jndcEncoded.setVersion(bytes[4]);

        jndcEncoded.setType(bytes[5]);


        jndcEncoded.setSourceAddress(Arrays.copyOfRange(bytes, 5, 9));

        jndcEncoded.setDestAddress(Arrays.copyOfRange(bytes, 9, 13));

        jndcEncoded.setSourcePort(Arrays.copyOfRange(bytes, 13, 15));

        jndcEncoded.setProxyPort(Arrays.copyOfRange(bytes, 15, 17));

        jndcEncoded.setDestPort(Arrays.copyOfRange(bytes, 17, 19));

        jndcEncoded.setDataSize(Arrays.copyOfRange(bytes, 19, 23));

        return jndcEncoded;
    }

    /**
     * 拆包
     *
     * @return
     */
    public List<JNDCEncoded> autoUnpack() {
        List<JNDCEncoded> ndcMessageProtocols = new ArrayList<>();
        byte[] data = getData();
        List<byte[]> list = ByteArrayUtils.bytesUnpack(data, StaticConfig.AUTOMATIC_UNPACKING_LENGTH);
        list.forEach(x -> {
            JNDCEncoded copy = copy(true);
            copy.setData(x);
            ndcMessageProtocols.add(copy);
        });
        return ndcMessageProtocols;
    }

    private JNDCEncoded copy(boolean withoutData) {
        JNDCEncoded ndcMessageProtocol = new JNDCEncoded();
        ndcMessageProtocol.setVersion(getVersion());
        ndcMessageProtocol.setSourceAddress(getSourceAddress());
        ndcMessageProtocol.setDestAddress(getDestAddress());
        ndcMessageProtocol.setSourcePort(getSourcePort());
        ndcMessageProtocol.setDestPort(getDestPort());
        ndcMessageProtocol.setType(getType());

        //clear data
        if (withoutData) {
            //todo 清除数据
            ndcMessageProtocol.setData(new byte[0]);
        } else {
            //todo 复制数据
            ndcMessageProtocol.setData(getData());
        }

        return ndcMessageProtocol;
    }


}
