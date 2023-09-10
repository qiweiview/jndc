package com.view.jndc.core.v2.enum_value;

public enum JNDCMessageType {


    /*--------------------- 消息类型(0-7)(0-f)  ---------------------*/
    //0x00 鉴权包
    //0x10 通道包 0x11 服务注册包
    //0x20 带宽测试包
    //0x3 心跳包
    //0x4 心跳包
    //0x5 心跳包
    //0x6 心跳包
    //0x7 心跳包


    AUTH_0X00("鉴权包", (byte) 0x00),


    CHANNEL_0X10("通道包", (byte) 0x10),
    CHANNEL_SERVICE_0X11("服务注册包", (byte) 0x11),
    CHANNEL_SERVICE_0X12("连接建立包", (byte) 0x12),
    CHANNEL_SERVICE_0X13("数据传输包", (byte) 0x13),
    CHANNEL_SERVICE_0X14("连接中断包", (byte) 0x14),


    TEST_BANDWIDTH_0X20("带宽测试包", (byte) 0x20),


    HAPPY_EVERY_DAY("服务注册包", (byte) 0x7f),


    ;

    public String name;
    public byte value;

    JNDCMessageType(String name, byte value) {
        this.name = name;
        this.value = value;
    }


}
