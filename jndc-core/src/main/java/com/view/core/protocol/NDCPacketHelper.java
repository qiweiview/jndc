package com.view.core.protocol;

public class NDCPacketHelper {


    public static boolean isOpenChannelPacket(NDCPacket ndcPacket) {
        return ndcPacket.getType() == NDCPacket.OPEN_CHANNEL;
    }

    public static boolean isServiceRegisterPacket(NDCPacket ndcPacket) {
        return ndcPacket.getType() == NDCPacket.SERVICE_REGISTER;
    }

    public static boolean isTCPDataPacket(NDCPacket ndcPacket) {
        return ndcPacket.getType() == NDCPacket.TCP_DATA;
    }

    public static boolean isTCPActivePacket(NDCPacket ndcPacket) {
        return ndcPacket.getType() == NDCPacket.TCP_ACTIVE;
    }

    public static boolean isReadyToAcceptPacket(NDCPacket ndcPacket) {
        return ndcPacket.getType() == NDCPacket.READ_TO_ACCEPT_PACKAGE;
    }

    public static boolean isTCPInActivePacket(NDCPacket ndcPacket) {
        return ndcPacket.getType() == NDCPacket.TCP_IN_ACTIVE;
    }

    public static boolean isServiceUnRegisterPacket(NDCPacket ndcPacket) {
        return ndcPacket.getType() == NDCPacket.SERVICE_UNREGISTER;
    }

    public static boolean isHeartBeatPacket(NDCPacket ndcPacket) {
        return ndcPacket.getType() == NDCPacket.HEART_BEAT;
    }
}
