package com.view.core.component.data_hub;

import com.view.core.protocol.NDCPacket;
import com.view.core.server.ndc.NDCServer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * 数据包中心
 * 提供数据包的事件驱动
 */
@Deprecated
@Slf4j
@Data
public class NativePackageHub implements PackageHubI {

    private NDCServer ndcServer;

    @Override
    public void postOnServer(NDCPacket ndcPacket) {
//        if (NDCPacketHelper.isTCPActivePacket(ndcPacket)) {
//            ndcServer.write(ndcPacket);
//        } else if (NDCPacketHelper.isTCPDataPacket(ndcPacket)) {
//
//        } else {
//            log.warn("未知的数据包类型:{}", ndcPacket.getType());
//        }
    }

    @Override
    public void postOnClient(NDCPacket ndcPacket) {

    }

}
