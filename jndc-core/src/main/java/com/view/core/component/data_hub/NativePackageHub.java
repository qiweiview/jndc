package com.view.core.component.data_hub;

import com.view.core.model.VirtualService;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class NativePackageHub implements PackageHubI {


    @Override
    public void publishOnServer(NDCPacket ndcPacket) {
        if (NDCPacketHelper.isServiceRegisterPacket(ndcPacket)) {
            //todo 服务注册消息
            VirtualService virtualService = ndcPacket.getObject(VirtualService.class);
            log.info("接收到服务注册{}", virtualService);

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
