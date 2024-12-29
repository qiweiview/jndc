package com.view.core.client.ndc.flow;

import com.view.core.client.ndc.NDCClient;
import com.view.core.client.ndc.NDCClientConfiguration;
import com.view.core.client.tcp.TCPClient;
import com.view.core.client.tcp.TCPClientConfiguration;
import com.view.core.model.ChannelOpen;
import com.view.core.model.TCPDataTransport;
import com.view.core.model.local_service.LocalService;
import com.view.core.model.tcp_data.TCPResponse;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketBuilder;
import com.view.core.protocol.NDCPacketHelper;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Data
@Slf4j
public class DesignedClientFlow {

    //long类型的id
    private Long longId;

    //string类型的id
    private String stringId;

    private NDCClientConfiguration ndcClientConfiguration;

    private ClientFlowSlot clientFlowSlot;

    private NDCClient ndcClient = new NDCClient();

    public DesignedClientFlow(NDCClientConfiguration ndcClientConfiguration, ClientFlowSlot clientFlowSlot) {
        this.ndcClientConfiguration = ndcClientConfiguration;
        this.clientFlowSlot = clientFlowSlot;
    }

    /**
     * 执行客户端流程（阻塞）
     */
    public void run() {

        ExecutorService executorService = ndcClient.getExecutorService();

        //设置id回调
        clientFlowSlot.setClientIdGetter(() -> ndcClientConfiguration.getUniqueId());

        //设置配置回调
        clientFlowSlot.setNdcClientConfigurationGetter(() -> ndcClientConfiguration);

        clientFlowSlot.setLongIdGetter(() -> longId);

        clientFlowSlot.setStingIdGetter(() -> stringId);


        ndcClientConfiguration.setStartedCallback(() -> {
            log.info("NDC客户端启动成功");
            clientFlowSlot.ndcClientStartSafe();
        });

        ndcClientConfiguration.setStartFailCallback((e) -> {
            log.error("NDC客户端启动失败");
            clientFlowSlot.ndcClientStartFailSafe(e);
        });

        ndcClientConfiguration.setConnectActiveCallback((ctx) -> {
            log.info("连接已建立");
            clientFlowSlot.connectionActiveSafe();
            clientFlowSlot.setChannelHandlerContextGetter(() -> ctx);
        });

        ndcClientConfiguration.setDataReadCallback((ndcPacket, clientCallbackContext) -> {
            ChannelHandlerContext context = clientCallbackContext.getContext();

            Map<String, LocalService> serviceMap = ndcClient.getNdcClientSessionMap();


            //判断
            if (NDCPacketHelper.isReadyToAcceptPacket(ndcPacket)) {
                //todo 准备接受数据，发送通道打开请求
                ChannelOpen channelOpen = new ChannelOpen();
                channelOpen.setNdcClientId(ndcClientConfiguration.getUniqueId());
                NDCPacket openChannelPacket = NDCPacketBuilder.openChannelPacket(channelOpen);
                context.writeAndFlush(openChannelPacket);
            } else if (NDCPacketHelper.isOpenChannelPacket(ndcPacket)) {
                //todo 通道打开成功
                log.info("通道打开成功:{}");


                //自动注册服务
                List<NDCPacket> registerPackage = ndcClientConfiguration.getAuthRegisterServices();
                registerPackage.forEach(context::writeAndFlush);

                clientFlowSlot.wakeUpWaitingOperation();

                clientFlowSlot.openChannelSafe();
            } else if (NDCPacketHelper.isServiceRegisterPacket(ndcPacket)) {
                //todo 服务注册响应
                LocalService localService = ndcPacket.getObject(LocalService.class);
                String serviceId = localService.getServiceId();

                localService.initTCPClientMap();
                if (localService.isSuccessful()) {
                    log.info("服务{}注册成功", localService.getName());

                    serviceMap.put(localService.getServiceId(), localService);

                    clientFlowSlot.registerTCPServiceSafe(serviceId);
                } else if (localService.isPortHasBound()) {
                    log.error("端口{}已被占用", localService.getPort());
                } else if (localService.isServiceExist()) {
                    log.error("服务{}已存在", localService.getName());
                } else if (localService.isTCPServerStartFail()) {
                    log.error("服务{}注册失败", localService.getName());
                } else {
                    log.error("非正常逻辑响应{}", localService.getRegisterResponse());
                }

            } else if (NDCPacketHelper.isServiceUnRegisterPacket(ndcPacket)) {
                //todo 服务注销响应
                LocalService localService = ndcPacket.getObject(LocalService.class);
                if (localService.isServiceNotExist()) {
                    log.error("服务{}不存在", localService.getName());
                } else if (localService.isSuccessful()) {
                } else if (localService.isSuccessful()) {
                    log.info("服务{}注销成功", localService.getName());
                    clientFlowSlot.unregisterTCPServiceSafe(localService.getServiceId());
                } else {
                    log.error("非正常逻辑响应{}", localService.getRegisterResponse());
                }

            } else if (NDCPacketHelper.isTCPActivePacket(ndcPacket)) {
                //todo 收到TCP激活包
                log.info("收到TCP激活包");
                TCPDataTransport tcpDataTransport = ndcPacket.getObject(TCPDataTransport.class);
                String clientId = tcpDataTransport.getNdcClientId();
                String serviceId = tcpDataTransport.getServiceId();
                String tcpChannelId = tcpDataTransport.getTcpChannelId();

                LocalService localService = serviceMap.get(serviceId);
                if (localService == null) {
                    //todo 服务不存在
                    log.error("未找到服务:{}", serviceId);
                    tcpDataTransport.setTcpResponse(TCPResponse.SERVICE_NOT_EXIST);
                    NDCPacket tcpActivePacket = NDCPacketBuilder.tcpActivePacket(tcpDataTransport);
                    context.writeAndFlush(tcpActivePacket);
                    return;
                }
                Map<String, TCPClient> tcpClientMap = localService.getTcpClientMap();
                TCPClient tcpClient = tcpClientMap.get(tcpChannelId);
                if (tcpClient == null) {
                    //todo 正常逻辑链路
                    tcpClient = new TCPClient();
                    tcpClientMap.put(tcpChannelId, tcpClient);

                    TCPClientConfiguration tcpClientConfiguration = new TCPClientConfiguration();
                    tcpClientConfiguration.setHost(localService.getHost());
                    tcpClientConfiguration.setPort(localService.getPort());
                    tcpClientConfiguration.setStartSuccessCallBack((tcpClient1) -> {
                        log.info("TCP客户端启动成功");
                        clientFlowSlot.tcpClientStartSafe(serviceId, tcpChannelId);
                    });
                    tcpClientConfiguration.setActiveCallBack((tcpClient1) -> {
                        log.info("TCP客户端已连接");
                        List<Thread> waitingActiveThead = tcpClientConfiguration.getWaitingActiveThead();
                        if (!waitingActiveThead.isEmpty())
                            synchronized (waitingActiveThead) {
                                if (!waitingActiveThead.isEmpty()) {
                                    waitingActiveThead.forEach(thread -> {
                                        synchronized (thread) {
                                            thread.notify();
                                        }
                                    });
                                    waitingActiveThead.clear();
                                }

                            }
                        clientFlowSlot.tcpChannelActiveSafe(serviceId, tcpChannelId);
                    });


                    tcpClientConfiguration.setStartFailCallBack((tcpClient1) -> {
                        log.error("TCP客户端启动失败");
                        tcpClientMap.remove(tcpChannelId);

                        //启动失败，即服务不存在
                        tcpDataTransport.setTcpResponse(TCPResponse.SERVICE_NOT_EXIST);
                        NDCPacket tcpActivePacket = NDCPacketBuilder.tcpActivePacket(tcpDataTransport);
                        context.writeAndFlush(tcpActivePacket);

                        clientFlowSlot.tcpClientStartFailSafe(serviceId, tcpChannelId);
                    });

                    tcpClientConfiguration.setReadCallBack((data) -> {
                        data.setTcpResponse(TCPResponse.SUCCESS);
                        data.setNdcClientId(clientId);
                        data.setServiceId(serviceId);
                        data.setTcpChannelId(tcpChannelId);
                        NDCPacket tcpActivePacket = NDCPacketBuilder.dataPacket(data);
                        context.writeAndFlush(tcpActivePacket);
                        log.debug("收到Client TCP数据,已发回");
                        byte[] bytes = data.getData();

                        clientFlowSlot.tcpChannelReadSafe(serviceId, tcpChannelId, bytes);
                    });
                    tcpClientConfiguration.setInactiveCallBack(tcpClientInactive -> {
                        log.info("TCP客户端已停止");
                        tcpClientInactive.setNdcClientId(clientId);
                        tcpClientInactive.setServiceId(serviceId);
                        tcpClientInactive.setTcpChannelId(tcpChannelId);
                        NDCPacket tcpActivePacket = NDCPacketBuilder.tcpInactivePacket(tcpClientInactive);
                        context.writeAndFlush(tcpActivePacket);
                        clientFlowSlot.tcpChannelInactiveSafe(serviceId, tcpChannelId);
                    });

                    TCPClient finalTcpClient = tcpClient;
                    executorService.submit(() -> {
                        finalTcpClient.start(tcpClientConfiguration);
                    });

                } else {
                    log.warn("异常情况TCP客户端已存在");
                }


            } else if (NDCPacketHelper.isTCPDataPacket(ndcPacket)) {
                //todo 收到TCP数据包
                TCPDataTransport tcpDataTransport = ndcPacket.getObject(TCPDataTransport.class);
                String serviceId = tcpDataTransport.getServiceId();
                String tcpChannelId = tcpDataTransport.getTcpChannelId();


                if (tcpDataTransport.isSuccessful()) {
                    //todo 数据发送成功
                    LocalService localService = serviceMap.get(serviceId);
                    if (localService == null) {
                        //todo 服务不存在
                        tcpDataTransport.setTcpResponse(TCPResponse.SERVICE_NOT_EXIST);
                        NDCPacket tcpActivePacket = NDCPacketBuilder.dataPacket(tcpDataTransport);
                        context.writeAndFlush(tcpActivePacket);
                        return;
                    }
                    Map<String, TCPClient> tcpClientMap = localService.getTcpClientMap();
                    TCPClient tcpClient = tcpClientMap.get(tcpChannelId);
                    if (tcpClient == null) {
                        //todo 客户端不存在
                        tcpDataTransport.setTcpResponse(TCPResponse.SERVICE_NOT_EXIST);
                        NDCPacket tcpActivePacket = NDCPacketBuilder.dataPacket(tcpDataTransport);
                        context.writeAndFlush(tcpActivePacket);
                        return;
                    }
                    try {
                        tcpClient.writeAndFlush(tcpDataTransport.getData());
                    } catch (Exception e) {
                        tcpDataTransport.setTcpResponse(TCPResponse.SERVICE_NOT_EXIST);
                        NDCPacket tcpActivePacket = NDCPacketBuilder.dataPacket(tcpDataTransport);
                        context.writeAndFlush(tcpActivePacket);
                    }

                    byte[] bytes = tcpDataTransport.getData();
                    clientFlowSlot.tcpChannelWriteSafe(serviceId, tcpChannelId, bytes);

                } else if (tcpDataTransport.isServiceNotExist()) {
                    //todo 服务不存在
                    LocalService localService = serviceMap.get(serviceId);
                    if (localService == null) {
                        //todo 服务不存在
                        log.error("未找到服务:{}", serviceId);
                    } else {
                        localService.stop();
                        log.info("服务{}已停止", localService.getName());
                    }
                } else {
                    log.error("未知的TCP数据包状态");
                }


            } else if (NDCPacketHelper.isTCPActivePacket(ndcPacket)) {
                //todo 收到TCP激活包
                TCPDataTransport tcpDataTransport = ndcPacket.getObject(TCPDataTransport.class);
                String serviceId = tcpDataTransport.getServiceId();
                String tcpChannelId = tcpDataTransport.getTcpChannelId();

                LocalService localService = serviceMap.get(serviceId);
                if (localService == null) {
                    //todo 服务不存在
                    log.error("未找到服务:{}", serviceId);
                    return;
                }
                Map<String, TCPClient> tcpClientMap = localService.getTcpClientMap();
                TCPClient tcpClient = tcpClientMap.get(tcpChannelId);
                if (tcpClient == null) {
                    //todo 正常逻辑链路
                    log.error("未找到客户端:{}", serviceId);
                    return;
                }
                tcpClient.stop();
            } else if (NDCPacketHelper.isTCPInActivePacket(ndcPacket)) {
                //todo 收到TCP停止包
                TCPDataTransport tcpDataTransport = ndcPacket.getObject(TCPDataTransport.class);
                String serviceId = tcpDataTransport.getServiceId();
                if (tcpDataTransport.isServiceNotExist()) {
                    //todo 服务不存在
                    log.error("未找到服务:{}", tcpDataTransport.getServiceId());
                    LocalService remove = serviceMap.remove(serviceId);
                    if (remove != null) {
                        remove.stop();
                    }
                } else if (tcpDataTransport.isRemoteConnectionInterrupt()) {
                    //todo 远程连接中断
                    log.error("远程连接中断:{}", tcpDataTransport.getServiceId());
                    LocalService remove = serviceMap.remove(serviceId);
                    if (remove != null) {
                        remove.stop();
                    }
                } else {
                    log.error("不合理的TCP停止包");
                }

            } else {
                log.warn("未知的数据包类型:{}", ndcPacket.getType());
            }
        });


        ndcClientConfiguration.setConnectInActiveCallback((ctx) -> {
            log.error("连接断开");
            NDCClient ndcClient1 = ctx.getNdcClient();
            Map<String, LocalService> serviceMap = ndcClient1.getNdcClientSessionMap();
            serviceMap.forEach((serviceId, localService) -> {
                localService.stop();
            });
            serviceMap.clear();
            clientFlowSlot.ndcClientInActiveSafe();
        });

        ndcClientConfiguration.setStopCallback(() -> {
            log.info("NDC客户端停止");
            clientFlowSlot.ndcClientStopSafe();
        });


        //定义服务
        ndcClient.start(ndcClientConfiguration);

    }

    public void stop() {

    }
}
