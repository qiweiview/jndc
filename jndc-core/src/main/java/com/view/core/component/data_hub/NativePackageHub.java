package com.view.core.component.data_hub;

import com.view.core.component.GlobalBeanContext;
import com.view.core.model.VirtualService;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * 数据包中心
 * 提供数据包的事件驱动
 */
@Slf4j
@Data
public class NativePackageHub implements PackageHubI {


    @Override
    public void publishOnServer(NDCPacket ndcPacket) {
        if (NDCPacketHelper.isServiceRegisterPacket(ndcPacket)) {
            //todo 服务注册消息
            VirtualService virtualService = ndcPacket.getObject(VirtualService.class);
            log.info("接收到服务注册{}", virtualService);

            GlobalBeanContext.APP_CENTER.deployService(virtualService);

        } else if (NDCPacketHelper.isCancelServiceRegisterPacket(ndcPacket)) {
            //todo 服务取消注册
            VirtualService virtualService = ndcPacket.getObject(VirtualService.class);
            log.info("接收到服务取消注册{}", virtualService);
        }
    }

    @Override
    public void publishOnClient(NDCPacket ndcPacket) {

    }
}
