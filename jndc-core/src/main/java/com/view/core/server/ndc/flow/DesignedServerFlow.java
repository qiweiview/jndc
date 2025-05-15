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
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * DesignedServerFlow 负责服务端主流程控制，包括连接、数据包分发、心跳、服务注册等。
 * 通过拆分方法提升可读性和可维护性。
 */
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

    /**
     * 启动服务主流程，注册各类回调。
     */
    public void start() {
        // 设置id获取回调
        serverFlowSlot.setServerIdGetter(() -> ndcServerConfiguration.getUniqueId());
        serverFlowSlot.setLongIdGetter(() -> longId);
        serverFlowSlot.setStingIdGetter(() -> stringId);

        // 连接激活回调
        ndcServerConfiguration.setConnectActiveCallback(ctx -> {
            ChannelHandlerContext context = ctx.getContext();
            NDCPacket ndcPacket = NDCPacketBuilder.readyToAcceptPacket();
            context.writeAndFlush(ndcPacket);
            log.info("连接激活");
            serverFlowSlot.connectActiveSafe();
        });

        // 数据读取回调，分发到不同处理方法
        ndcServerConfiguration.setDataReadCallback((ndcPacket, serverCallbackContext) -> {
            // 获取服务器实例、上下文和远程地址信息
            NDCServer ndcServerReferent = serverCallbackContext.getNdcServer();
            ChannelHandlerContext ndcContex = serverCallbackContext.getContext();
            InetSocketAddress remote = serverCallbackContext.getRemote();
            // 获取客户端会话映射表
            Map<String, ChannelOpen> ndcClientSessionMap = ndcServerReferent.getNdcClientSessionMap();

            // 根据数据包类型分发到对应的处理方法
            if (NDCPacketHelper.isOpenChannelPacket(ndcPacket)) {
                //todo 处理打开通道请求
                handleOpenChannel(ndcPacket, ndcContex, ndcClientSessionMap, remote);
            } else if (NDCPacketHelper.isServiceRegisterPacket(ndcPacket)) {
                //todo 处理服务注册请求
                handleServiceRegister(ndcPacket, ndcContex, ndcClientSessionMap);
            } else if (NDCPacketHelper.isServiceUnRegisterPacket(ndcPacket)) {
                //todo 处理服务注销请求
                handleServiceUnRegister(ndcPacket, ndcContex, ndcClientSessionMap);
            } else if (NDCPacketHelper.isTCPActivePacket(ndcPacket)) {
                //todo 处理TCP连接激活请求
                handleTCPActive(ndcPacket, ndcContex, ndcClientSessionMap);
            } else if (NDCPacketHelper.isTCPDataPacket(ndcPacket)) {
                //todo 处理TCP数据传输请求
                handleTCPData(ndcPacket, ndcContex, ndcClientSessionMap);
            } else if (NDCPacketHelper.isTCPInActivePacket(ndcPacket)) {
                //todo 处理TCP连接断开请求
                handleTCPInActive(ndcPacket, ndcContex, ndcClientSessionMap);
            } else if (NDCPacketHelper.isHeartBeatPacket(ndcPacket)) {
                //todo 处理心跳包
                handleHeartBeat(ndcPacket, ndcContex);
            } else {
                // 记录未知类型的数据包
                log.info("未知数据包:{}", ndcPacket);
            }
        });

        // 连接断开回调
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
                    // 停止心跳并关闭所有服务
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

        // 服务启动、停止、失败回调
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

    /**
     * 处理打开通道的数据包
     */
    private void handleOpenChannel(NDCPacket ndcPacket, ChannelHandlerContext ndcContex, Map<String, ChannelOpen> ndcClientSessionMap, InetSocketAddress remote) {
        ChannelOpen channelOpen = ndcPacket.getObject(ChannelOpen.class);
        String clientId = channelOpen.getNdcClientId();
        ndcClientSessionMap.put(clientId, channelOpen);
        // 启动心跳
        channelOpen.startHeartBeat(HeartBeatSource.SERVER, ndcContex);
        ndcContex.channel().attr(AttributeKey.valueOf(NDCServer.CLIENT_ID)).set(clientId);
        ndcContex.writeAndFlush(ndcPacket);
        log.info("打开通道:{}", clientId);
        serverFlowSlot.openChannelSafe(clientId, remote);
    }

    /**
     * 处理服务注册的数据包
     */
    private void handleServiceRegister(NDCPacket ndcPacket, ChannelHandlerContext ndcContex, Map<String, ChannelOpen> ndcClientSessionMap) {
        LocalService localService = ndcPacket.getObject(LocalService.class);
        String serviceId = localService.getServiceId();
        String ndcClientId = localService.getNdcClientId();
        ChannelOpen channelOpen = ndcClientSessionMap.get(ndcClientId);
        if (channelOpen == null) {
            log.error("未找到客户端:{}", ndcClientId);
            localService.setRegisterResponse(RegisterResponse.CLIENT_NOT_EXIST);
            NDCPacket registerServicePacket = NDCPacketBuilder.registerServicePacket(localService);
            ndcContex.writeAndFlush(registerServicePacket);
            return;
        }
        Map<String, TCPServer> tcpServerMap = channelOpen.getTcpServerMap();
        TCPServer tcpServerExistCheck = tcpServerMap.get(serviceId);
        if (tcpServerExistCheck != null) {
            log.error("服务已经注册:{}", serviceId);
            localService.setRegisterResponse(RegisterResponse.SERVICE_EXIST);
            NDCPacket registerServicePacket = NDCPacketBuilder.registerServicePacket(localService);
            ndcContex.writeAndFlush(registerServicePacket);
            return;
        }
        // 注册事件插槽
        serverFlowSlot.serviceRegisterSafe(ndcServerConfiguration.getUniqueId(), localService);
    }

    /**
     * 处理服务注销的数据包
     */
    private void handleServiceUnRegister(NDCPacket ndcPacket, ChannelHandlerContext ndcContex, Map<String, ChannelOpen> ndcClientSessionMap) {
        LocalService localService = ndcPacket.getObject(LocalService.class);
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
        serverFlowSlot.serviceUnRegisterSafe(ndcServerConfiguration.getUniqueId(), localService);
    }

    /**
     * 处理TCP激活响应包
     */
    private void handleTCPActive(NDCPacket ndcPacket, ChannelHandlerContext ndcContex, Map<String, ChannelOpen> ndcClientSessionMap) {
        TCPDataTransport tcpDataTransport = ndcPacket.getObject(TCPDataTransport.class);
        if (tcpDataTransport.isServiceNotExist()) {
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
            // 关闭远程连接
            channelHandlerContext.close();
        } else {
            log.error("未知数据包:{}", ndcPacket);
        }
    }

    /**
     * 处理TCP数据包
     */
    private void handleTCPData(NDCPacket ndcPacket, ChannelHandlerContext ndcContex, Map<String, ChannelOpen> ndcClientSessionMap) {
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
            // 关闭远程连接
            channelHandlerContext.close();
        } else {
            log.error("未知数据包:{}", ndcPacket);
        }
    }

    /**
     * 处理TCP断开连接包
     */
    private void handleTCPInActive(NDCPacket ndcPacket, ChannelHandlerContext ndcContex, Map<String, ChannelOpen> ndcClientSessionMap) {
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
    }

    /**
     * 处理心跳包
     */
    private void handleHeartBeat(NDCPacket ndcPacket, ChannelHandlerContext ndcContex) {
        HeartBeatPack heartBeatPack = ndcPacket.getObject(HeartBeatPack.class);
        if (heartBeatPack.isForServer()) {
            log.debug("收到客户端响应心跳包:{}", ndcPacket);
            long timestamp = ndcPacket.getTimestamp();
            String ndcClientId = heartBeatPack.getNdcClientId();
            serverFlowSlot.clientHeartBeatSafe(ndcClientId, timestamp);
        } else if (heartBeatPack.isForClient()) {
            // 写回给客户端
            NDCPacket response = NDCPacketBuilder.heartBeatPacket(heartBeatPack);
            ndcContex.writeAndFlush(response);
        } else {
            log.error("未知心跳包:{}", ndcPacket);
        }
    }

    /**
     * 停止服务
     */
    public void stop() {
        ndcServer.stop();
    }
}
