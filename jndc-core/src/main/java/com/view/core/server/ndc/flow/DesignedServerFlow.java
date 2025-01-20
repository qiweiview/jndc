package com.view.core.server.ndc.flow;

import com.view.core.model.ChannelOpen;
import com.view.core.model.TCPDataTransport;
import com.view.core.model.heart_beat.HeartBeatPack;
import com.view.core.model.heart_beat.HeartBeatSource;
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
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Data
@Slf4j
public class DesignedServerFlow {

    //long类型的id
    private Long longId;

    //string类型的id
    private String stringId;

    private NDCServerConfiguration ndcServerConfiguration;

    private ServerFlowSlot serverFlowSlot;

    private NDCServer ndcServer = new NDCServer();


    public DesignedServerFlow(NDCServerConfiguration ndcServerConfiguration, ServerFlowSlot serverFlowSlot) {
        this.ndcServerConfiguration = ndcServerConfiguration;
        this.serverFlowSlot = serverFlowSlot;
    }

    public void run() {

        //设置id获取回调
        serverFlowSlot.setServerIdGetter(() -> ndcServerConfiguration.getUniqueId());

        serverFlowSlot.setLongIdGetter(() -> longId);

        serverFlowSlot.setStingIdGetter(() -> stringId);

        ndcServerConfiguration.setConnectActiveCallback(ctx -> {
            ChannelHandlerContext context = ctx.getContext();

            NDCPacket ndcPacket = NDCPacketBuilder.readyToAcceptPacket();
            context.writeAndFlush(ndcPacket);
            log.info("连接激活");
            serverFlowSlot.connectActiveSafe();
        });

        ndcServerConfiguration.setDataReadCallback((ndcPacket, serverCallbackContext) -> {
            NDCServer ndcServerReferent = serverCallbackContext.getNdcServer();
            ExecutorService executorService = ndcServerReferent.getExecutorService();
            ChannelHandlerContext ndcContex = serverCallbackContext.getContext();
            InetSocketAddress remote = serverCallbackContext.getRemote();

            Map<String, ChannelOpen> ndcClientSessionMap = ndcServerReferent.getNdcClientSessionMap();


            if (NDCPacketHelper.isOpenChannelPacket(ndcPacket)) {
                //todo 打开通道
                ChannelOpen channelOpen = ndcPacket.getObject(ChannelOpen.class);
                String clientId = channelOpen.getNdcClientId();
                ndcClientSessionMap.put(clientId, channelOpen);
                //启动心跳
                channelOpen.startHeartBeat(HeartBeatSource.SERVER, ndcContex);


                ndcContex.channel().attr(AttributeKey.valueOf(NDCServer.CLIENT_ID)).set(clientId);
                ndcContex.writeAndFlush(ndcPacket);

                log.info("打开通道:{}", clientId);
                serverFlowSlot.openChannelSafe(clientId, remote);
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
                    ndcContex.writeAndFlush(registerServicePacket);
                    return;
                }

                //判断服务是否已经注册
                Map<String, TCPServer> tcpServerMap = channelOpen.getTcpServerMap();
                TCPServer tcpServerExistCheck = tcpServerMap.get(serviceId);
                if (tcpServerExistCheck != null) {
                    log.error("服务已经注册:{}", serviceId);
                    localService.setRegisterResponse(RegisterResponse.SERVICE_EXIST);
                    NDCPacket registerServicePacket = NDCPacketBuilder.registerServicePacket(localService);
                    ndcContex.writeAndFlush(registerServicePacket);
                    return;
                }

                //注册事件插槽
                serverFlowSlot.serviceRegisterSafe(ndcServerConfiguration.getUniqueId(),localService);

                //独立绑定，不再在此处判断端口是否被占用
                /*boolean bindable = TCPUtils.portBindable(port);
                if (!bindable) {
                    log.error("端口{}已被占用", port);
                    localService.setRegisterResponse(RegisterResponse.PORT_HAS_BOUND);
                    NDCPacket registerServicePacket = NDCPacketBuilder.registerServicePacket(localService);
                    ndcContex.writeAndFlush(registerServicePacket);
                }*/

                //不在此处创建tcp服务
                //tcpServerExist = new TCPServer();
                /*Map<String, ChannelHandlerContext> sessionMap = tcpServerExist.getSessionMap();


                TCPServerConfiguration tcpServerConfiguration = new TCPServerConfiguration();
                tcpServerConfiguration.setPort(localService.getExpectBindPort());
                tcpServerConfiguration.setNdcClientId(localService.getNdcClientId());
                tcpServerConfiguration.setServiceId(serviceId);

                tcpServerConfiguration.setStartSuccessCallBack(tcpServer -> {
                    localService.setRegisterResponse(RegisterResponse.SUCCESS);
                    NDCPacket registerServicePacket = NDCPacketBuilder.registerServicePacket(localService);
                    ndcContex.writeAndFlush(registerServicePacket);
                    log.info("TCP服务启动成功：{}，发送响应", serviceId);
                    tcpServerMap.put(serviceId, tcpServer);

                    serverFlowSlot.tcpServerStartSuccessSafe(tcpServerConfiguration);
                });


                tcpServerConfiguration.setStartFailCallBack(tcpServer -> {
                    log.error("TCP服务启动失败：{}", serviceId);
                    localService.setRegisterResponse(RegisterResponse.TCP_SERVER_START_FAIL);
                    NDCPacket registerServicePacket = NDCPacketBuilder.unregisterServicePacket(localService);
                    ndcContex.writeAndFlush(registerServicePacket);
                    ndcClientSessionMap.remove(serviceId);

                    serverFlowSlot.tcpServerStartFailSafe(ndcClientId, serviceId);
                });


                tcpServerConfiguration.setActiveCallBack((tcpDataTransport, context) -> {
                    InetSocketAddress tcpRemote = tcpDataTransport.getRemote();
                    String tcpChannelId = tcpDataTransport.getTcpChannelId();
                    log.debug("TCP服务激活：{}，接收远程会话", tcpChannelId);
                    sessionMap.put(tcpChannelId, context);
                    tcpDataTransport.setNdcClientId(ndcClientId);
                    tcpDataTransport.setServiceId(serviceId);
                    ndcContex.writeAndFlush(NDCPacketBuilder.tcpActivePacket(tcpDataTransport));
                    serverFlowSlot.tcpChannelActiveSafe(ndcClientId, serviceId, tcpChannelId, tcpRemote);
                });

                tcpServerConfiguration.setReadCallBack((tcpDataTransport) -> {
                    //todo tcp 服务端读取数据
                    String tcpChannelId = tcpDataTransport.getTcpChannelId();
                    InetSocketAddress tcpRemote = tcpDataTransport.getRemote();


                    log.debug("TCP服务端读取数据：{}", serviceId);
                    tcpDataTransport.setTcpResponse(TCPResponse.SUCCESS);
                    tcpDataTransport.setNdcClientId(ndcClientId);
                    tcpDataTransport.setServiceId(serviceId);
                    NDCPacket registerServicePacket = NDCPacketBuilder.dataPacket(tcpDataTransport);
                    ndcContex.writeAndFlush(registerServicePacket);
                    byte[] data = registerServicePacket.getData();
                    serverFlowSlot.tcpChannelReadSafe(ndcClientId, serviceId, tcpChannelId, tcpRemote, data);
                });

                tcpServerConfiguration.setInactiveCallBack((tcpDataTransport, tcpServer) -> {
                    //todo tcp 服务端断开连接
                    log.debug("TCP服务端断开连接：{}", serviceId);
                    String tcpChannelId = tcpDataTransport.getTcpChannelId();
                    InetSocketAddress tcpRemote = tcpDataTransport.getRemote();
                    sessionMap.remove(tcpChannelId);
                    tcpDataTransport.setTcpResponse(TCPResponse.REMOTE_CONNECTION_INTERRUPT);
                    tcpDataTransport.setNdcClientId(ndcClientId);
                    tcpDataTransport.setServiceId(serviceId);

                    NDCPacket registerServicePacket = NDCPacketBuilder.tcpInactivePacket(tcpDataTransport);
                    ndcContex.writeAndFlush(registerServicePacket);

                    serverFlowSlot.tcpChannelInactiveSafe(ndcClientId, serviceId, tcpChannelId, tcpRemote);
                });

                tcpServerConfiguration.setStopCallBack(tcpServer -> {
                    log.info("TCP服务停止：{}", serviceId);
                    serverFlowSlot.tcpServerStopSafe(ndcClientId, serviceId);
                });

                TCPServer finalTcpServerExist = tcpServerExist;
                executorService.submit(() -> {
                    //启动服务
                    finalTcpServerExist.start(tcpServerConfiguration);
                });*/


            } else if (NDCPacketHelper.isServiceUnRegisterPacket(ndcPacket)) {
                //todo 数据包

                LocalService localService = ndcPacket.getObject(LocalService.class);
                String serviceId = localService.getServiceId();

                //判断客户端是否存在
                String ndcClientId = localService.getNdcClientId();
                if (ndcClientId == null) {
                    log.error("未找到客户端:{}", ndcClientId);
                    localService.setRegisterResponse(RegisterResponse.CLIENT_NOT_EXIST);
                    NDCPacket registerServicePacket = NDCPacketBuilder.registerServicePacket(localService);
                    ndcContex.writeAndFlush(registerServicePacket);
                    return;
                }
                ChannelOpen channelOpen = ndcClientSessionMap.get(ndcClientId);
                if (channelOpen == null) {
                    log.error("未找到客户端:{}", ndcClientId);
                    localService.setRegisterResponse(RegisterResponse.CLIENT_NOT_EXIST);
                    NDCPacket registerServicePacket = NDCPacketBuilder.registerServicePacket(localService);
                    ndcContex.writeAndFlush(registerServicePacket);
                    return;
                }
                ndcClientSessionMap.remove(ndcClientId);

                //不再在此处关闭服务
                /*Map<String, TCPServer> tcpServerMap = channelOpen.getTcpServerMap();

                TCPServer tcpServerExist = tcpServerMap.get(serviceId);
                if (tcpServerExist == null) {
                    log.error("服务不存在:{}", serviceId);
                    localService.setRegisterResponse(RegisterResponse.SERVICE_NOT_EXIST);
                    NDCPacket registerServicePacket = NDCPacketBuilder.registerServicePacket(localService);
                    ndcContex.writeAndFlush(registerServicePacket);
                    return;
                }

                //关闭服务
                tcpServerExist.stop();*/

                //注册事件插槽
                serverFlowSlot.serviceUnRegisterSafe(ndcServerConfiguration.getUniqueId(), localService);


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
                        log.error("tcp active service not exist未找到会话:{}", tcpChannelId);
                        return;
                    }
                    //关闭远程连接
                    channelHandlerContext.close();


                } else {
                    log.error("未知数据包:{}", ndcPacket);
                }

            } else if (NDCPacketHelper.isTCPDataPacket(ndcPacket)) {
                //todo tcp数据包
                log.debug("收到TCP数据包");
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
                        ndcContex.writeAndFlush(tcpActivePacket);
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

                    serverFlowSlot.tcpChannelWriteSafe(ndcClientId, serviceId, tcpChannelId, data);
                    log.debug("写出数据包至客户端{}", tcpChannelId);
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

                } else {
                    log.error("未知数据包:{}", ndcPacket);
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
                    ndcContex.writeAndFlush(tcpActivePacket);
                    return;
                }
                Map<String, ChannelHandlerContext> sessionMap = tcpServer.getSessionMap();
                ChannelHandlerContext channelHandlerContext = sessionMap.get(tcpChannelId);
                if (channelHandlerContext == null) {
                    log.debug("tcp inactive未找到会话:{}", tcpChannelId);
                    return;
                }
                sessionMap.remove(tcpChannelId);
                log.info("移除tcp channel会话{}", tcpChannelId);
                channelHandlerContext.close();
            } else if (NDCPacketHelper.isHeartBeatPacket(ndcPacket)) {
                //todo 心跳包
                HeartBeatPack heartBeatPack = ndcPacket.getObject(HeartBeatPack.class);
                if (heartBeatPack.isForServer()) {
                    log.debug("收到客户端响应心跳包:{}", ndcPacket);
                    long timestamp = ndcPacket.getTimestamp();
                    String ndcClientId = heartBeatPack.getNdcClientId();
                    serverFlowSlot.clientHeartBeatSafe(ndcClientId, timestamp);
                } else if (heartBeatPack.isForClient()) {
                    //写回给客户端
                    NDCPacket response = NDCPacketBuilder.heartBeatPacket(heartBeatPack);
                    ndcContex.writeAndFlush(response);
                } else {
                    log.error("未知心跳包:{}", ndcPacket);
                }

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
                    //停止心跳
                    channelOpen.stopHeartBeat();
                    Map<String, TCPServer> tcpServerMap = channelOpen.getTcpServerMap();
                    tcpServerMap.forEach((serviceId, tcpServer) -> {
                        tcpServer.stop();
                        log.info("关闭服务：{}", serviceId);
                    });


                    ndcClientSessionMap.remove(clientId);
                }
                serverFlowSlot.connectInActiveSafe(clientId);
            }


        });

        ndcServerConfiguration.setStartedCallback(() -> {
            log.info("服务启动");
            serverFlowSlot.ndcServerStartSafe();
        });

        ndcServerConfiguration.setStopCallback(() -> {
            log.info("服务停止");
            serverFlowSlot.ndcServerStopSafe();
        });

        ndcServerConfiguration.setFailCallback((e) -> {
            log.error("服务启动失败");
            serverFlowSlot.ndcServerStartFailSafe(e);
        });

        ndcServer.start(ndcServerConfiguration);
    }

    public void stop() {
        ndcServer.stop();
    }
}
