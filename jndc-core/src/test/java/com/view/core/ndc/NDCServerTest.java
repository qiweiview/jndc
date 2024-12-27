package com.view.core.ndc;

import com.view.core.model.ChannelOpen;
import com.view.core.model.TCPDataTransport;
import com.view.core.model.local_service.LocalService;
import com.view.core.model.local_service.RegisterResponse;
import com.view.core.model.tcp_data.TCPResponse;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketBuilder;
import com.view.core.protocol.NDCPacketHelper;
import com.view.core.server.ndc.NDCServer;
import com.view.core.server.ndc.NDCServerConfiguration;
import com.view.core.server.tcp.TCPServer;
import com.view.core.server.tcp.TCPServerConfiguration;
import com.view.core.utils.TCPUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
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

                ctx.channel().attr(AttributeKey.valueOf(NDCServer.CLIENT_ID)).set(clientId);

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

                tcpServerConfiguration.setReadCallBack((tcpDataTransport) -> {
                    //todo tcp 服务端读取数据
                    tcpDataTransport.setTcpResponse(TCPResponse.SUCCESS);
                    tcpDataTransport.setNdcClientId(ndcClientId);
                    tcpDataTransport.setServiceId(serviceId);
                    NDCPacket registerServicePacket = NDCPacketBuilder.dataPacket(tcpDataTransport);
                    ctx.writeAndFlush(registerServicePacket);
                });

                tcpServerConfiguration.setInactiveCallBack((tcpDataTransport, tcpServer) -> {
                    //todo tcp 服务端断开连接
                    log.info("TCP服务端断开连接：{}", serviceId);
                    String tcpChannelId = tcpDataTransport.getTcpChannelId();
                    sessionMap.remove(tcpChannelId);

                    tcpDataTransport.setNdcClientId(ndcClientId);
                    tcpDataTransport.setServiceId(serviceId);

                    NDCPacket registerServicePacket = NDCPacketBuilder.tcpInactivePacket(tcpDataTransport);
                    ctx.writeAndFlush(registerServicePacket);
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
                ndcClientSessionMap.remove(ndcClientId);

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
                //todo tcp激活响应
                TCPDataTransport tcpDataTransport = ndcPacket.getObject(TCPDataTransport.class);
                if (tcpDataTransport.isServiceNotExist()) {
                    //todo 服务不存在
                    String ndcClientId = tcpDataTransport.getNdcClientId();
                    String serviceId = tcpDataTransport.getServiceId();
                    String tcpChannelId = tcpDataTransport.getTcpChannelId();
                    ChannelOpen channelOpen = ndcClientSessionMap.get(ndcClientId);
                    if (channelOpen == null) {
                        log.error("未找到客户端:{}", ndcClientId);
                        return;
                    }
                    Map<String, TCPServer> tcpServerMap = channelOpen.getTcpServerMap();
                    TCPServer tcpServer = tcpServerMap.get(serviceId);
                    if (tcpServer == null) {
                        log.error("未找到服务:{}", serviceId);
                        return;
                    }
                    Map<String, ChannelHandlerContext> sessionMap = tcpServer.getSessionMap();
                    ChannelHandlerContext channelHandlerContext = sessionMap.get(tcpChannelId);
                    if (channelHandlerContext == null) {
                        log.error("未找到会话:{}", tcpChannelId);
                        return;
                    }
                    //关闭远程连接
                    channelHandlerContext.close();


                }

            } else if (NDCPacketHelper.isTCPDataPacket(ndcPacket)) {
                //todo tcp数据包
                TCPDataTransport tcpDataTransport = ndcPacket.getObject(TCPDataTransport.class);
                if (tcpDataTransport.isSuccessful()) {
                    String ndcClientId = tcpDataTransport.getNdcClientId();
                    String serviceId = tcpDataTransport.getServiceId();
                    String tcpChannelId = tcpDataTransport.getTcpChannelId();
                    ChannelOpen channelOpen = ndcClientSessionMap.get(ndcClientId);
                    if (channelOpen == null) {
                        log.error("未找到客户端:{}", ndcClientId);
                        return;
                    }
                    Map<String, TCPServer> tcpServerMap = channelOpen.getTcpServerMap();
                    TCPServer tcpServer = tcpServerMap.get(serviceId);
                    if (tcpServer == null) {
                        log.error("未找到服务:{}", serviceId);
                        tcpDataTransport.setTcpResponse(TCPResponse.SERVICE_NOT_EXIST);
                        NDCPacket tcpActivePacket = NDCPacketBuilder.dataPacket(tcpDataTransport);
                        ctx.writeAndFlush(tcpActivePacket);
                        return;
                    }
                    Map<String, ChannelHandlerContext> sessionMap = tcpServer.getSessionMap();
                    ChannelHandlerContext channelHandlerContext = sessionMap.get(tcpChannelId);
                    if (channelHandlerContext == null) {
                        log.error("未找到会话:{}", tcpChannelId);
                        return;
                    }
                    byte[] data = tcpDataTransport.getData();
                    channelHandlerContext.writeAndFlush(data);
                } else if (tcpDataTransport.isServiceNotExist()) {
                    //todo 服务不存在
                    String ndcClientId = tcpDataTransport.getNdcClientId();
                    String serviceId = tcpDataTransport.getServiceId();
                    String tcpChannelId = tcpDataTransport.getTcpChannelId();
                    ChannelOpen channelOpen = ndcClientSessionMap.get(ndcClientId);
                    if (channelOpen == null) {
                        log.error("未找到客户端:{}", ndcClientId);
                        return;
                    }
                    Map<String, TCPServer> tcpServerMap = channelOpen.getTcpServerMap();
                    TCPServer tcpServer = tcpServerMap.get(serviceId);
                    if (tcpServer == null) {
                        log.error("未找到服务:{}", serviceId);
                        return;
                    }
                    Map<String, ChannelHandlerContext> sessionMap = tcpServer.getSessionMap();
                    ChannelHandlerContext channelHandlerContext = sessionMap.get(tcpChannelId);
                    if (channelHandlerContext == null) {
                        log.error("未找到会话:{}", tcpChannelId);
                        return;
                    }
                    //关闭远程连接
                    channelHandlerContext.close();

                }


            } else if (NDCPacketHelper.isTCPInActivePacket(ndcPacket)) {
                //todo tcp断开连接
                TCPDataTransport tcpDataTransport = ndcPacket.getObject(TCPDataTransport.class);
                String ndcClientId = tcpDataTransport.getNdcClientId();
                String serviceId = tcpDataTransport.getServiceId();
                String tcpChannelId = tcpDataTransport.getTcpChannelId();
                ChannelOpen channelOpen = ndcClientSessionMap.get(ndcClientId);
                if (channelOpen == null) {
                    log.error("未找到客户端:{}", ndcClientId);
                    return;
                }
                Map<String, TCPServer> tcpServerMap = channelOpen.getTcpServerMap();
                TCPServer tcpServer = tcpServerMap.get(serviceId);
                if (tcpServer == null) {
                    log.error("未找到服务:{}", serviceId);

                    tcpDataTransport.setTcpResponse(TCPResponse.SERVICE_NOT_EXIST);
                    NDCPacket tcpActivePacket = NDCPacketBuilder.tcpInactivePacket(tcpDataTransport);
                    ctx.writeAndFlush(tcpActivePacket);
                    return;
                }
                Map<String, ChannelHandlerContext> sessionMap = tcpServer.getSessionMap();
                ChannelHandlerContext channelHandlerContext = sessionMap.get(tcpChannelId);
                if (channelHandlerContext == null) {
                    log.error("未找到会话:{}", tcpChannelId);
                    return;
                }
                sessionMap.remove(tcpChannelId);
                channelHandlerContext.close();
            } else {
                log.info("未知数据包:{}", ndcPacket);
            }
        });

        ndcServerConfiguration.setConnectInActiveCallback((serverCallbackContext) -> {
            log.info("连接断开");
            NDCServer ndcServer1 = serverCallbackContext.getNdcServer();
            ChannelHandlerContext context = serverCallbackContext.getContext();
            Object o = context.channel().attr(AttributeKey.valueOf(NDCServer.CLIENT_ID)).get();
            if (o != null) {
                String clientId = (String) o;
                Map<String, ChannelOpen> ndcClientSessionMap = ndcServer1.getNdcClientSessionMap();
                ChannelOpen channelOpen = ndcClientSessionMap.get(clientId);
                if (channelOpen != null) {
                    Map<String, TCPServer> tcpServerMap = channelOpen.getTcpServerMap();
                    tcpServerMap.forEach((serviceId, tcpServer) -> {
                        tcpServer.stop();
                    });

                    ndcClientSessionMap.remove(clientId);
                }
            }


        });


        ndcServer.start(ndcServerConfiguration);
    }
}
