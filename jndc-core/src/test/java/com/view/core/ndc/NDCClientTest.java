package com.view.core.ndc;

import com.view.core.client.ndc.NDCClient;
import com.view.core.client.ndc.NDCClientConfiguration;
import com.view.core.client.tcp.TCPClient;
import com.view.core.client.tcp.TCPClientConfiguration;
import com.view.core.component.app_center.ServiceIdManager;
import com.view.core.model.ChannelOpen;
import com.view.core.model.TCPDataTransport;
import com.view.core.model.local_service.LocalService;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketBuilder;
import com.view.core.protocol.NDCPacketHelper;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

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

        String clientId = "client1";
        NDCClientConfiguration ndcClientConfiguration = new NDCClientConfiguration();
        ndcClientConfiguration.setServerHost("127.0.0.1");//服务端地址
        ndcClientConfiguration.setServerPort(8888);//服务端端口
        ndcClientConfiguration.setReconnectInterval(3);//重连间隔
        ndcClientConfiguration.setAutoReconnect(true);//自动重连
        ndcClientConfiguration.setReconnectMaxTimes(-1);//不限制最大重连次数
        ndcClientConfiguration.setUniqueId(clientId);//客户端唯一标识

        log.info("---准备发起注册---");

        //定义暴露服务
        String host2 = "qw607.com";
        int port2 = 80;
        LocalService localService2 = new LocalService();
        localService2.setServiceId(serviceIdManager.generateServiceId(host2, port2));
        localService2.setName("qw607");
        localService2.setHost(host2);
        localService2.setPort(port2);
        localService2.setExpectBindPort(7777);
        localService2.setNdcClientId(clientId);


        ndcClientConfiguration.setDataReadCallback((ndcPacket, clientCallbackContext) -> {
            ChannelHandlerContext context = clientCallbackContext.getContext();
            NDCClient ndcClient1 = clientCallbackContext.getNdcClient();

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

                ndcClient1.registerService(localService2);
            } else if (NDCPacketHelper.isServiceRegisterPacket(ndcPacket)) {
                //todo 服务注册响应
                LocalService localService = ndcPacket.getObject(LocalService.class);
                if (localService.isSuccessful()) {
                    log.info("服务{}注册成功", localService.getName());

                    serviceMap.put(localService.getServiceId(), localService);

                    //注销服务
                    NDCPacket unregisterServicePacket = NDCPacketBuilder.unregisterServicePacket(localService);
                    context.writeAndFlush(unregisterServicePacket);
                } else if (localService.isPortHasBound()) {
                    log.error("端口{}已被占用", localService.getPort());
                } else if (localService.isServiceExist()) {
                    log.error("服务{}已存在", localService.getName());
                } else if (localService.isOtherError()) {
                    log.error("服务{}注册失败", localService.getName());
                }

            } else if (NDCPacketHelper.isServiceUnRegisterPacket(ndcPacket)) {
                //todo 服务注销响应
                LocalService localService = ndcPacket.getObject(LocalService.class);
                if (localService.isServiceNotExist()) {
                    log.error("服务{}不存在", localService.getName());
                } else if (localService.isSuccessful()) {
                    log.info("服务{}注销成功", localService.getName());
                }

            } else if (NDCPacketHelper.isTCPActivePacket(ndcPacket)) {
                //todo 收到TCP激活包
                TCPDataTransport tcpDataTransport = ndcPacket.getObject(TCPDataTransport.class);
                String serviceId = tcpDataTransport.getServiceId();
                String tcpChannelId = tcpDataTransport.getTcpChannelId();

                LocalService localService = serviceMap.get(serviceId);
                if (localService == null) {
                    log.error("未找到服务:{}", serviceId);
                    NDCPacket tcpActivePacket = NDCPacketBuilder.tcpActivePacket(tcpDataTransport);
                    context.writeAndFlush(tcpActivePacket);
                    return;
                }
                Map<String, TCPClient> tcpClientMap = localService.getTcpClientMap();
                TCPClient tcpClient = tcpClientMap.get(tcpChannelId);
                if (tcpClient == null) {
                    tcpClient = new TCPClient();
                    tcpClientMap.put(tcpChannelId, tcpClient);

                    TCPClientConfiguration tcpClientConfiguration = new TCPClientConfiguration();
                    tcpClientConfiguration.setHost(localService.getHost());
                    tcpClientConfiguration.setPort(localService.getPort());
                    tcpClientConfiguration.setStartFailCallBack((tcpClient1) -> {
                        log.error("TCP客户端启动失败");
                        tcpClientMap.remove(tcpChannelId);
                    });

                    tcpClientConfiguration.setActiveCallBack((tcpContext) -> {
                        byte[] data = tcpDataTransport.getData();
                        tcpContext.writeAndFlush(data);
                    });
                    tcpClient.start(tcpClientConfiguration);
                }


            } else {
                log.warn("未知的数据包类型:{}", ndcPacket.getType());
            }
        });


        //定义服务
        ndcClient.start(ndcClientConfiguration);
    }


}
