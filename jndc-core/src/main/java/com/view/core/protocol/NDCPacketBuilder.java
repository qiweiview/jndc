package com.view.core.protocol;

import com.view.core.model.ChannelOpen;
import com.view.core.model.TCPDataTransport;
import com.view.core.model.VirtualTCPService;
import com.view.core.utils.ObjectSerializableUtils;

public class NDCPacketBuilder {

    public static NDCPacket readyToAcceptPacket() {
        NDCPacket ndcPacket = NDCPacket.of(
                NDCPacket.BLANK_ADDRESS,
                NDCPacket.BLANK_ADDRESS,
                NDCPacket.UN_USED_PORT,
                NDCPacket.UN_USED_PORT,
                NDCPacket.UN_USED_PORT,
                NDCPacket.READ_TO_ACCEPT_PACKAGE,
                System.currentTimeMillis()
        );
        ndcPacket.setData(NDCPacket.BLANK_DATA);
        return ndcPacket;
    }


    public static NDCPacket registerServicePacket(VirtualTCPService virtualTCPService) {
        NDCPacket ndcPacket = NDCPacket.of(
                NDCPacket.BLANK_ADDRESS,
                NDCPacket.BLANK_ADDRESS,
                NDCPacket.UN_USED_PORT,
                NDCPacket.UN_USED_PORT,
                NDCPacket.UN_USED_PORT,
                NDCPacket.SERVICE_REGISTER,
                System.currentTimeMillis());
        ndcPacket.setData(ObjectSerializableUtils.object2bytes(virtualTCPService));
        return ndcPacket;
    }

    public static NDCPacket unregisterServicePacket(VirtualTCPService virtualTCPService) {
        NDCPacket ndcPacket = NDCPacket.of(
                NDCPacket.BLANK_ADDRESS,
                NDCPacket.BLANK_ADDRESS,
                NDCPacket.UN_USED_PORT,
                NDCPacket.UN_USED_PORT,
                NDCPacket.UN_USED_PORT,
                NDCPacket.SERVICE_UNREGISTER,
                System.currentTimeMillis());
        ndcPacket.setData(ObjectSerializableUtils.object2bytes(virtualTCPService));
        return ndcPacket;
    }

    public static NDCPacket openChannelPacket(ChannelOpen channelOpen) {
        NDCPacket ndcPacket = NDCPacket.of(
                NDCPacket.BLANK_ADDRESS,
                NDCPacket.BLANK_ADDRESS,
                NDCPacket.UN_USED_PORT,
                NDCPacket.UN_USED_PORT,
                NDCPacket.UN_USED_PORT,
                NDCPacket.OPEN_CHANNEL,
                System.currentTimeMillis());
        ndcPacket.setData(ObjectSerializableUtils.object2bytes(channelOpen));
        return ndcPacket;
    }


    public static NDCPacket dataPacket(TCPDataTransport tcpDataTransport) {
        NDCPacket ndcPacket = NDCPacket.of(
                NDCPacket.BLANK_ADDRESS,
                NDCPacket.BLANK_ADDRESS,
                NDCPacket.UN_USED_PORT,
                NDCPacket.UN_USED_PORT,
                NDCPacket.UN_USED_PORT,
                NDCPacket.TCP_DATA,
                System.currentTimeMillis());
        ndcPacket.setData(ObjectSerializableUtils.object2bytes(tcpDataTransport));
        return ndcPacket;
    }

    public static NDCPacket tcpActivePacket(TCPDataTransport tcpDataTransport) {
        NDCPacket ndcPacket = NDCPacket.of(
                NDCPacket.BLANK_ADDRESS,
                NDCPacket.BLANK_ADDRESS,
                NDCPacket.UN_USED_PORT,
                NDCPacket.UN_USED_PORT,
                NDCPacket.UN_USED_PORT,
                NDCPacket.TCP_ACTIVE,
                System.currentTimeMillis());
        ndcPacket.setData(ObjectSerializableUtils.object2bytes(tcpDataTransport));
        return ndcPacket;
    }

    public static NDCPacket tcpInactivePacket(TCPDataTransport tcpDataTransport) {
        NDCPacket ndcPacket = NDCPacket.of(
                NDCPacket.BLANK_ADDRESS,
                NDCPacket.BLANK_ADDRESS,
                NDCPacket.UN_USED_PORT,
                NDCPacket.UN_USED_PORT,
                NDCPacket.UN_USED_PORT,
                NDCPacket.TCP_IN_ACTIVE,
                System.currentTimeMillis());
        ndcPacket.setData(ObjectSerializableUtils.object2bytes(tcpDataTransport));
        return ndcPacket;
    }
}
