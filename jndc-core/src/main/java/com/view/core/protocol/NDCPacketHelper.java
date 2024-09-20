package com.view.core.protocol;

public class NDCPacketHelper {

    /**
     * 判断是否是服务注册消息
     *
     * @param ndcPacket
     * @return
     */
    public static boolean isServiceRegisterPacket(NDCPacket ndcPacket) {
        return ndcPacket.getType() == NDCPacket.SERVICE_REGISTER;
    }


    /**
     * 判断是否是服务取消注册消息
     *
     * @param ndcPacket
     * @return
     */
    public static boolean isCancelServiceRegisterPacket(NDCPacket ndcPacket) {
        return ndcPacket.getType() == NDCPacket.SERVICE_UNREGISTER;
    }
}
