package com.view.jndc.core.v2.constant.protocol_message;

public class BitConstant {

    public static final byte[] NDC_PROTOCOL_TAG = "NDC".getBytes();//协议标志


    /*--------------------- 消息类型(0-7)(0-f)  ---------------------*/
    //0x0 鉴权包
    //0x1 数据包
    //0x2 异常包
    //0x3 心跳包
    //0x4 心跳包
    //0x5 心跳包
    //0x6 心跳包
    //0x7 心跳包


    public static final byte MESSAGE_AUTH_TYPE = 0x00;//鉴权包

    public static final byte HAPPY_EVERY_DAY_TYPE = 0x7f;//每天开心

}
