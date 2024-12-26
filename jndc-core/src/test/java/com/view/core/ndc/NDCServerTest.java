package com.view.core.ndc;

import com.view.core.model.ChannelOpen;
import com.view.core.model.TCPDataTransport;
import com.view.core.model.local_service.LocalService;
import com.view.core.model.local_service.RegisterResponse;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketBuilder;
import com.view.core.protocol.NDCPacketHelper;
import com.view.core.server.ndc.NDCServer;
import com.view.core.server.ndc.NDCServerConfiguration;
import com.view.core.server.tcp.TCPServer;
import com.view.core.server.tcp.TCPServerConfiguration;
import com.view.core.utils.TCPUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

@Slf4j
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
            NDCServer ndcServer = serverCallbackContext.getNdcServer();
            ChannelHandlerContext ctx = serverCallbackContext.getContext();
            Map<String, ChannelOpen> ndcClientSessionMap = ndcServer.getNdcClientSessionMap();


            if (NDCPacketHelper.isOpenChannelPacket(ndcPacket)) {
                //todo 打开通道
                ChannelOpen object = ndcPacket.getObject(ChannelOpen.class);
                String clientId = object.getNdcClientId();
                ndcClientSessionMap.put(clientId, object);

                log.info("record change:{}", clientId);
                ctx.writeAndFlush(ndcPacket);
            } else if (NDCPacketHelper.isServiceRegisterPacket(ndcPacket)) {
                //todo 注册
                LocalService localService = ndcPacket.getObject(LocalService.class);
                String serviceId = localService.getServiceId();


                //判断客户端是否存在
                String ndcClientId = localService.getNdcClientId();
                ChannelOpen channelOpen = ndcClientSessionMap.get(ndcClientId);
                if (channelOpen == null) {
                    log.error("未找到客户端:{}", ndcClientId);
                    localService.setRegisterResponse(RegisterResponse.CLIENT_NOT_EXIST);
                    NDCPacket registerServicePacket = NDCPacketBuilder.registerServicePacket(localService);
                    ctx.writeAndFlush(registerServicePacket);
                    return;
                }

                //判断服务是否已经注册
                Map<String, TCPServer> tcpServerMap = channelOpen.getTcpServerMap();
                TCPServer tcpServerExist = tcpServerMap.get(serviceId);
                if (tcpServerExist != null) {
                    log.error("服务已经注册:{}", serviceId);
                    localService.setRegisterResponse(RegisterResponse.SERVICE_EXIST);
                    NDCPacket registerServicePacket = NDCPacketBuilder.registerServicePacket(localService);
                    ctx.writeAndFlush(registerServicePacket);
                    return;
                }


                int port = localService.getPort();
                boolean bindable = TCPUtils.portBindable(port);
                if (!bindable) {
                    log.error("端口{}已被占用", port);
                    localService.setRegisterResponse(RegisterResponse.PORT_HAS_BOUND);
                    NDCPacket registerServicePacket = NDCPacketBuilder.registerServicePacket(localService);
                    ctx.writeAndFlush(registerServicePacket);
                }

                tcpServerExist = new TCPServer();

                Map<String, ChannelHandlerContext> sessionMap = tcpServerExist.getSessionMap();


                TCPServerConfiguration tcpServerConfiguration = new TCPServerConfiguration();
                tcpServerConfiguration.setPort(localService.getExpectBindPort());
                tcpServerConfiguration.setNdcClientId(localService.getNdcClientId());
                tcpServerConfiguration.setServiceId(serviceId);

                tcpServerConfiguration.setStartSuccessCallBack(tcpServer -> {
                    log.info("TCP服务启动成功：{}", serviceId);
                    localService.setRegisterResponse(RegisterResponse.SUCCESS);
                    NDCPacket registerServicePacket = NDCPacketBuilder.registerServicePacket(localService);
                    ctx.writeAndFlush(registerServicePacket);
                    tcpServerMap.put(serviceId, tcpServer);
                });
                tcpServerConfiguration.setStartFailCallBack(tcpServer -> {
                    log.error("TCP服务启动失败：{}", serviceId);
                    localService.setRegisterResponse(RegisterResponse.OTHER_ERROR);
                    NDCPacket registerServicePacket = NDCPacketBuilder.unregisterServicePacket(localService);
                    ctx.writeAndFlush(registerServicePacket);
                    ndcClientSessionMap.remove(serviceId);
                });

                tcpServerConfiguration.setStopCallBack(tcpServer -> {
                    log.info("TCP服务关闭：{}", serviceId);
                    localService.setRegisterResponse(RegisterResponse.OTHER_ERROR);
                    NDCPacket registerServicePacket = NDCPacketBuilder.unregisterServicePacket(localService);
                    ctx.writeAndFlush(registerServicePacket);
                    ndcClientSessionMap.remove(serviceId);
                });

                tcpServerConfiguration.setActiveCallBack((tcpDataTransport, context) -> {
                    log.info("TCP服务激活：{}", serviceId);
                    String tcpChannelId = tcpDataTransport.getTcpChannelId();

                    sessionMap.put(tcpChannelId, context);

                    tcpDataTransport.setNdcClientId(ndcClientId);
                    tcpDataTransport.setServiceId(serviceId);
                    ctx.writeAndFlush(NDCPacketBuilder.tcpActivePacket(tcpDataTransport));
                });

                //启动服务
                tcpServerExist.start(tcpServerConfiguration);


            } else if (NDCPacketHelper.isServiceUnRegisterPacket(ndcPacket)) {
                //todo 数据包

                LocalService localService = ndcPacket.getObject(LocalService.class);
                String serviceId = localService.getServiceId();

                //判断客户端是否存在
                String ndcClientId = localService.getNdcClientId();
                ChannelOpen channelOpen = ndcClientSessionMap.get(ndcClientId);
                if (channelOpen == null) {
                    log.error("未找到客户端:{}", ndcClientId);
                    localService.setRegisterResponse(RegisterResponse.CLIENT_NOT_EXIST);
                    NDCPacket registerServicePacket = NDCPacketBuilder.registerServicePacket(localService);
                    ctx.writeAndFlush(registerServicePacket);
                    return;
                }
                Map<String, TCPServer> tcpServerMap = channelOpen.getTcpServerMap();
                TCPServer tcpServerExist = tcpServerMap.get(serviceId);
                if (tcpServerExist == null) {
                    log.error("服务不存在:{}", serviceId);
                    localService.setRegisterResponse(RegisterResponse.SERVICE_NOT_EXIST);
                    NDCPacket registerServicePacket = NDCPacketBuilder.registerServicePacket(localService);
                    ctx.writeAndFlush(registerServicePacket);
                    return;
                }

                //关闭服务
                tcpServerExist.stop();


            } else if (NDCPacketHelper.isTCPActivePacket(ndcPacket)) {
                //todo 数据包
                TCPDataTransport object = ndcPacket.getObject(TCPDataTransport.class);
                if (object.isServiceNotExist()) {
                    //todo 服务不存在
                    String ndcClientId = object.getNdcClientId();
                    String serviceId = object.getServiceId();
                    String tcpChannelId = object.getTcpChannelId();
                    ChannelOpen channelOpen = ndcClientSessionMap.get(ndcClientId);
                    if (channelOpen == null) {
                        log.error("未找到客户端:{}", ndcClientId);
                        return;
                    }
                    Map<String, TCPServer> tcpServerMap = channelOpen.getTcpServerMap();
                    TCPServer tcpServer = tcpServerMap.get(serviceId);
                    Map<String, ChannelHandlerContext> sessionMap = tcpServer.getSessionMap();
                    ChannelHandlerContext channelHandlerContext = sessionMap.get(tcpChannelId);
                    if (channelHandlerContext == null) {
                        log.error("未找到会话:{}", tcpChannelId);
                        return;
                    }
                    //关闭远程连接
                    channelHandlerContext.close();


                }

            } else {
                log.info("未知数据包:{}", ndcPacket);
            }
        });

        ndcServer.start(ndcServerConfiguration);
    }
}
