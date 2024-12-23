package com.view.core.client.ndc;

import com.view.core.component.GlobalBeanContext;
import com.view.core.model.ChannelOpen;
import com.view.core.model.TCPDataTransport;
import com.view.core.model.VirtualTCPService;
import com.view.core.protocol.NDCPCodec;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketBuilder;
import com.view.core.protocol.NDCPacketHelper;
import com.view.core.protocol.callback.ChannelRead0Consumer;
import com.view.core.protocol.callback.ChannelRead0Function;
import com.view.core.server.ndc.SessionContext;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NDCClient {

    private Channel clientChannel;

    private EventLoopGroup workerGroup;

    private List<NDCPacket> tobeSendPackage = new ArrayList<>();

    private ChannelHandlerContext serverContext;

    private NDCClientConfiguration ndcClientConfiguration;

    private int retryTimes = 0;


    private Map<String, VirtualTCPService> ndcClientSessionMap = new ConcurrentHashMap<>();


    public void stop() {
        //停止重试
        ndcClientConfiguration.doBreakOperation();
        if (clientChannel != null && clientChannel.isOpen()) {
            clientChannel.close();
            log.info("NDC客户端关闭");
            workerGroup.shutdownGracefully();
            ndcClientConfiguration.getStopCallback().run();
        }

    }

    public void start(NDCClientConfiguration ndcClientConfiguration) {
        ndcClientConfiguration.resetRetryBreak();

        //设置配置，适配重连
        if (this.ndcClientConfiguration == null) {
            this.ndcClientConfiguration = ndcClientConfiguration;
        }

        //检查配置
        ndcClientConfiguration.check();

        //打印配置
        ndcClientConfiguration.printConfiguration();


        workerGroup = new NioEventLoopGroup();

        try {

            //active回调
            ChannelRead0Function<NDCPacket, SessionContext> activeCallback = (ctx, msg) -> {
                //todo active回调
                SessionContext sessionContext = SessionContext.of(ctx);

                //重置重试次数
                retryTimes = 0;

                //设置服务端上下文
                serverContext = ctx;

                //发送开通通道包
                ChannelOpen channelOpen = new ChannelOpen();
                channelOpen.setNdcClientId(ndcClientConfiguration.getUniqueId());
                NDCPacket openChannelPacket = NDCPacketBuilder.openChannelPacket(channelOpen);
                ctx.writeAndFlush(openChannelPacket);

                return sessionContext;
            };

            //read回调
            ChannelRead0Consumer<NDCPacket> readCallback = (ctx, msg) -> {
                //todo read回调

                //获取消息
                NDCPacket ndcPacket = msg[0];

                //判断
                if (NDCPacketHelper.isOpenChannelPacket(ndcPacket)) {
                    //todo 打开通道
                    handleOpenChannel(ndcPacket);
                } else if (NDCPacketHelper.isTCPActivePacket(ndcPacket)) {
                    //todo 远程连接激活
                    handleTCPActive(ndcPacket);
                } else if (NDCPacketHelper.isTCPInActivePacket(ndcPacket)) {
                    //todo 远程连接激活
                    handleTCPInActive(ndcPacket);
                } else if (NDCPacketHelper.isTCPDataPacket(ndcPacket)) {
                    //todo 数据包
                    handleDataPackage(ndcPacket);
                } else {
                    log.warn("未知的数据包类型:{}", ndcPacket.getType());
                }
            };

            //inActive回调
            ChannelRead0Consumer<NDCPacket> inActiveCallback = (ctx, msg) -> {
                //todo inActive回调
            };


            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

            //创建处理器
            ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    //NDC协议处理
                    pipeline.addLast(new NDCPCodec());

                    //创建ndc客户端处理器
                    NDCClientHandler ndcClientHandler = new NDCClientHandler(activeCallback, readCallback, inActiveCallback);

                    //NDC Package 处理
                    pipeline.addLast(ndcClientHandler);

                }
            };

            //设置处理器
            bootstrap.handler(channelInitializer);

            String host = ndcClientConfiguration.getServerHost();
            int port = ndcClientConfiguration.getServerPort();

            // Start the client.
            ChannelFuture channelFuture = bootstrap.connect(host, port);
            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    GlobalBeanContext.NDC_CLIENT = this;
                    log.info("NDC客户端启动成功：{}:{}", host, port);
                    ndcClientConfiguration.getStartedCallback().run();
                } else {
                    log.error("NDC客户端启动失败：{}:{}", host, port);
                    ndcClientConfiguration.getFailCallback().accept(new RuntimeException("NDC客户端启动失败"));
                }
            });
            this.clientChannel = channelFuture.channel();
            // 阻塞直到客户端连接关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            ndcClientConfiguration.getFailCallback().accept(e);
        } finally {
            //判定是否重连
            if (ndcClientConfiguration.reconnectThisTime()) {
                //todo 再次启动

                ndcClientConfiguration.getProcessingCallback().run();

                int timeoutSecond = ndcClientConfiguration.getReconnectInterval();
                log.error("连接断开，等待{}秒，进行第{}次尝试重连", timeoutSecond, retryTimes++);
                Thread thread = Thread.currentThread();
                ndcClientConfiguration.setWaitingThread(thread);
                try {
                    TimeUnit.SECONDS.sleep(timeoutSecond);
                } catch (InterruptedException e) {
                    log.warn("等待重连被中断");
                }

                if (ndcClientConfiguration.getRetryBreak()) {
                    //todo 重试中断
                    ndcClientConfiguration.getStopCallback().run();
                } else {
                    start(ndcClientConfiguration);
                }

            } else {
                stop();
            }

        }


    }

    private void handleTCPInActive(NDCPacket ndcPacket) {
        TCPDataTransport tcpDataTransport = ndcPacket.getObject(TCPDataTransport.class);
        String clientServiceId = tcpDataTransport.getClientServiceId();
        VirtualTCPService virtualTCPService = ndcClientSessionMap.get(clientServiceId);
        if (virtualTCPService == null) {
            log.warn("未找到对应的服务{}", clientServiceId);
        } else {
            //异步处理
            Runnable runnable = () -> {
                virtualTCPService.stopServiceClient(tcpDataTransport);
            };
            GlobalBeanContext.EVENT_BUS.post(runnable);
        }
    }

    /**
     * @param ndcPacket
     */
    public void handleTCPActive(NDCPacket ndcPacket) {
        TCPDataTransport tcpDataTransport = ndcPacket.getObject(TCPDataTransport.class);
        log.debug("收到打开本地连接请求包,远程会话id：{}", tcpDataTransport.getAppServerSessionId());
        String clientServiceId = tcpDataTransport.getClientServiceId();
        VirtualTCPService virtualTCPService = ndcClientSessionMap.get(clientServiceId);
        if (virtualTCPService == null) {
            log.warn("未找到对应的服务{}", clientServiceId);
        } else {
            //todo 打开本地服务端


            Runnable runnable = () -> {
                //todo 异步处理

                //打开本地服务端
                virtualTCPService.openLocalServiceClient(tcpDataTransport, tcpClient -> {
                    //todo 打开本地服务端成功回调

                    //设置客户端会话id
                    String clientServiceSessionId = tcpClient.getClientServiceSessionId();
                    tcpDataTransport.setClientServiceSessionId(clientServiceSessionId);

                    //写出客户端启动成功消息
                    NDCPacket response = NDCPacketBuilder.tcpActivePacket(tcpDataTransport);
                    serverContext.writeAndFlush(response);
                });

            };

            //异步处理
            GlobalBeanContext.EVENT_BUS.post(runnable);
        }
    }

    private void handleDataPackage(NDCPacket ndcPacket) {
        TCPDataTransport tcpDataTransport = ndcPacket.getObject(TCPDataTransport.class);
        String clientServiceId = tcpDataTransport.getClientServiceId();
        VirtualTCPService virtualTCPService = ndcClientSessionMap.get(clientServiceId);
        if (virtualTCPService == null) {
            log.warn("未找到对应的服务{}", clientServiceId);
        } else {
            //异步处理
            Runnable runnable = () -> {
                virtualTCPService.receiveDataFromRemote(tcpDataTransport);
            };
            GlobalBeanContext.EVENT_BUS.post(runnable);
        }
    }


    /**
     * 处理打开通道
     *
     * @param ndcPacket
     */
    private void handleOpenChannel(NDCPacket ndcPacket) {
        ChannelOpen channelOpen = ndcPacket.getObject(ChannelOpen.class);
        log.debug("得到服务器{}打开通道确认消息，准备发送缓冲区数据包：{}", channelOpen.getNdcServerId(), tobeSendPackage.size());
        //发送缓冲区报文
        tobeSendPackage.forEach(tobeSend -> {
            VirtualTCPService virtualTCPService = tobeSend.getObject(VirtualTCPService.class);
            writePackage(tobeSend, () -> {
                ndcClientSessionMap.put(virtualTCPService.getServiceId(), virtualTCPService);
            });
        });
    }

    public void registerService(VirtualTCPService virtualTCPService) {
        tobeSendPackage.add(NDCPacketBuilder.registerServicePacket(virtualTCPService));
    }

    public void writePackage(NDCPacket ndcPacket) {
        writePackage(ndcPacket, () -> {
            //todo do nothing

        });
    }

    public void writePackage(NDCPacket ndcPacket, Runnable callback) {
        serverContext.writeAndFlush(ndcPacket);
        callback.run();
    }


}
