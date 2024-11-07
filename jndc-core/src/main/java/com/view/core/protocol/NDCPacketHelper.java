package com.view.core.protocol;

public class NDCPacketHelper {


    public static boolean isOpenChannelPacket(NDCPacket ndcPacket) {
        return ndcPacket.getType() == NDCPacket.OPEN_CHANNEL;
    }


    /**
     * 判断是否是服务注册消息
     *
     * @param ndcPacket
     * @return
     */
    public static boolean isServiceRegisterPacket(NDCPacket ndcPacket) {
        return ndcPacket.getType() == NDCPacket.SERVICE_REGISTER;
    }


    public static boolean isTCPDataPacket(NDCPacket ndcPacket) {
        return ndcPacket.getType() == NDCPacket.TCP_DATA;
    }

    public static boolean isTCPActivePacket(NDCPacket ndcPacket) {
        return ndcPacket.getType() == NDCPacket.TCP_ACTIVE;
    }
}
