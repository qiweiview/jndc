package com.view.core.ndc;

import com.view.core.model.ChannelOpen;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketBuilder;
import com.view.core.protocol.NDCPacketHelper;
import com.view.core.server.ndc.NDCServer;
import com.view.core.server.ndc.NDCServerConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NDCServerTest {

    private NDCServer ndcServer;

    @BeforeEach
    public void init() {
        ndcServer = new NDCServer();
    }

    @Test
    public void runServer() {
        NDCServerConfiguration ndcServerConfiguration = new NDCServerConfiguration();
        ndcServerConfiguration.setHost("127.0.0.1");
        ndcServerConfiguration.setPort(8888);
        ndcServerConfiguration.setUniqueId("server1");

        ndcServerConfiguration.setConnectActiveCallback(ctx -> {
            NDCPacket ndcPacket = NDCPacketBuilder.readyToAcceptPacket();
            ctx.channel().writeAndFlush(ndcPacket);
            System.out.println("连接成功,通知客户端准备接收数据");
        });

        ndcServerConfiguration.setDataReadCallback((ndcPacket, serverCallbackContext) -> {
            NDCServer ndcServer1 = serverCallbackContext.getNdcServer();

            if (NDCPacketHelper.isOpenChannelPacket(ndcPacket)) {
                //todo 打开通道
                ChannelOpen object = ndcPacket.getObject(ChannelOpen.class);
                String clientId = object.getNdcClientId();

            }
        });

        ndcServer.start(ndcServerConfiguration);
    }
}
