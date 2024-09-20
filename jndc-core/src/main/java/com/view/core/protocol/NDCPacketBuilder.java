package com.view.core.protocol;

import com.view.core.model.VirtualService;
import com.view.core.utils.ObjectSerializableUtils;

public class NDCPacketBuilder {

    public static NDCPacket heartBeatPacket() {
        NDCPacket ndcPacket = NDCPacket.of(
                NDCPacket.BLANK_ADDRESS,
                NDCPacket.BLANK_ADDRESS,
                NDCPacket.UN_USED_PORT,
                NDCPacket.UN_USED_PORT,
                NDCPacket.UN_USED_PORT,
                NDCPacket.CHANNEL_HEART_BEAT);
        ndcPacket.setData(NDCPacket.BLANK_DATA);
        return ndcPacket;
    }

    public static NDCPacket registerServicePacket(VirtualService virtualService) {
        NDCPacket ndcPacket = NDCPacket.of(
                NDCPacket.BLANK_ADDRESS,
                NDCPacket.BLANK_ADDRESS,
                NDCPacket.UN_USED_PORT,
                NDCPacket.UN_USED_PORT,
                NDCPacket.UN_USED_PORT,
                NDCPacket.SERVICE_REGISTER);
        ndcPacket.setData(ObjectSerializableUtils.object2bytes(virtualService));
        return ndcPacket;
    }
}
