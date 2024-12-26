package com.view.core.ndc;

import com.view.core.client.ndc.NDCClient;
import com.view.core.client.ndc.NDCClientConfiguration;
import com.view.core.component.app_center.ServiceIdManager;
import com.view.core.model.ChannelOpen;
import com.view.core.model.VirtualTCPService;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketBuilder;
import com.view.core.protocol.NDCPacketHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j

public class NDCClientTest {

    private NDCClient ndcClient;

    @BeforeEach
    public void init() {
        ndcClient = new NDCClient();
    }

    @Test
    public void runClient() {
        ServiceIdManager serviceIdManager = new ServiceIdManager();


        NDCClientConfiguration ndcClientConfiguration = new NDCClientConfiguration();
        ndcClientConfiguration.setServerHost("127.0.0.1");//服务端地址
        ndcClientConfiguration.setServerPort(8888);//服务端端口
        ndcClientConfiguration.setReconnectInterval(3);//重连间隔
        ndcClientConfiguration.setAutoReconnect(true);//自动重连
        ndcClientConfiguration.setReconnectMaxTimes(-1);//不限制最大重连次数
        ndcClientConfiguration.setUniqueId("client1");//客户端唯一标识

        log.info("---准备发起注册---");

        //定义暴露服务
        String host2 = "qw607.com";
        int port2 = 80;
        VirtualTCPService virtualTCPService2 = new VirtualTCPService();
        virtualTCPService2.setServiceId(serviceIdManager.generateServiceId(host2, port2));
        virtualTCPService2.setDescription("qw607");
        virtualTCPService2.setHost(host2);
        virtualTCPService2.setPort(port2);
        virtualTCPService2.setExpectPort(7777);
        ndcClient.registerService(virtualTCPService2);


        ndcClientConfiguration.setDataReadCallback((ndcPacket, channelHandlerContext) -> {

            //判断
            if (NDCPacketHelper.isReadyToAcceptPacket(ndcPacket)) {
                //todo 准备接受数据，发送通道打开请求
                ChannelOpen channelOpen = new ChannelOpen();
                channelOpen.setNdcClientId(ndcClientConfiguration.getUniqueId());
                NDCPacket openChannelPacket = NDCPacketBuilder.openChannelPacket(channelOpen);
                channelHandlerContext.writeAndFlush(openChannelPacket);
            } else {
                log.warn("未知的数据包类型:{}", ndcPacket.getType());
            }
        });


        //定义服务
        ndcClient.start(ndcClientConfiguration);
    }


}
