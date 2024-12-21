package com.view.core.server.ndc;

import com.view.core.client.ndc.NDCClientInfo;
import com.view.core.component.GlobalBeanContext;
import com.view.core.model.ChannelOpen;
import com.view.core.model.TCPDataTransport;
import com.view.core.model.VirtualTCPService;
import com.view.core.model.event_bus.ChannelOperation;
import com.view.core.model.event_bus.ServiceOperation;
import com.view.core.protocol.NDCPCodec;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketBuilder;
import com.view.core.protocol.NDCPacketHelper;
import com.view.core.protocol.callback.ChannelRead0CallBack;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Data
@Slf4j
public class NDCServer {
    private NDCServerConfiguration ndcServerConfiguration;

    private String ndcServerId;

    private NioEventLoopGroup bossGroup;

    private NioEventLoopGroup workerGroup;

    private Channel serverChannel;

    //key:ndcClientId
    private Map<String, NDCClientInfo> ndcClientSessionMap = new ConcurrentHashMap<>();

    public static final String CLIENT_ID = "CLIENT_ID";

    public void stop() {
        if (serverChannel != null && serverChannel.isOpen()) {
            serverChannel.close();
            log.info("NDC服务关闭");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            ndcServerConfiguration.getStopCallback().run();
        }

    }


    public void start(NDCServerConfiguration ndcServerConfiguration) {

        //设置配置
        ndcServerConfiguration.check();

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        String host = ndcServerConfiguration.getHost();
        int port = ndcServerConfiguration.getPort();
        Runnable startedCallback = ndcServerConfiguration.getStartedCallback();
        Consumer<Exception> failCallback = ndcServerConfiguration.getFailCallback();


        try {


            //active回调
            ChannelRead0CallBack activeCallback = (ctx, msg) -> {
                //todo active回调
            };

            //read回调
            ChannelRead0CallBack<NDCPacket> readCallback = (ctx, msg) -> {
                //todo read回调


                //获取消息
                NDCPacket ndcPacket = msg[0];


                //流量统计

                String clientId = getClientId(ctx);
                if (clientId != null) {
                    GlobalBeanContext.GENERAL_CONTROL.addTraffic(clientId, ndcPacket.getDataSize());
                }


                //判断
                if (NDCPacketHelper.isOpenChannelPacket(ndcPacket)) {
                    //todo 通道打开
                    handleOpenChannel(ctx, ndcPacket);
                } else if (NDCPacketHelper.isServiceRegisterPacket(ndcPacket)) {
                    //todo 服务注册消息
                    handleServiceRegister(ctx, ndcPacket);
                } else if (NDCPacketHelper.isTCPDataPacket(ndcPacket)) {
                    //todo 数据包
                    handleTCPDataPackage(ctx, ndcPacket);
                } else if (NDCPacketHelper.isTCPActivePacket(ndcPacket)) {
                    //todo 通道开通响应
                    handleTCPActiveFinished(ctx, ndcPacket);
                } else {
                    log.warn("未知的数据包类型:{}", ndcPacket.getType());
                }
            };


            //inActive回调
            ChannelRead0CallBack inActiveCallback = (ctx, msg) -> {
                //todo inActive回调

                String clientId = getClientId(ctx);
                if (clientId == null) {
                    log.error("未绑定客户端编号");
                    return;
                }
                log.info("连接关闭，客户端编号:{}", clientId);

                ChannelOperation channelOperation = ChannelOperation.ofInactive(clientId);
                //【异步处理】连接中断事件
                GlobalBeanContext.EVENT_BUS.post(channelOperation);

                //移除
                ndcClientSessionMap.remove(clientId);
            };


            //创建ndc服务端处理器
            ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    //NDC协议处理
                    pipeline.addLast(new NDCPCodec());

                    NDCServerHandler ndcServerHandler = new NDCServerHandler(activeCallback, readCallback, inActiveCallback);

                    //NDC Packet 处理器
                    pipeline.addLast(ndcServerHandler);
                }
            };

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer);


            log.info("起动NDC服务，{}：{}", host, port);
            ChannelFuture channelFuture = b.bind(host, port).sync();
            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    GlobalBeanContext.NDC_SERVER = this;
                    startedCallback.run();
                    log.info("NDC服务启动成功，{}：{}", host, port);
                } else {
                    log.error("NDC服务启动失败，{}：{}", host, port);
                    failCallback.accept(new RuntimeException("NDC服务启动失败"));
                }
            });
            serverChannel = channelFuture.channel();
            // 阻塞直到服务器关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            failCallback.accept(e);
        } finally {
            stop();
        }
    }

    /**
     * @param ctx
     * @param ndcPacket
     */
    private void handleTCPActiveFinished(ChannelHandlerContext ctx, NDCPacket ndcPacket) {
        Runnable runnable = () -> {
            //todo 异步处理
            TCPDataTransport tcpDataTransport = ndcPacket.getObject(TCPDataTransport.class);
            GlobalBeanContext.APP_CENTER.noticeActiveCompleted(tcpDataTransport);
        };

        GlobalBeanContext.EVENT_BUS.post(runnable);
    }

    private void handleTCPDataPackage(ChannelHandlerContext ctx, NDCPacket ndcPacket) {
        TCPDataTransport tcpDataTransport = ndcPacket.getObject(TCPDataTransport.class);
        GlobalBeanContext.EVENT_BUS.post(tcpDataTransport);
    }

    private void handleServiceRegister(ChannelHandlerContext ctx, NDCPacket ndcPacket) {
        VirtualTCPService virtualTCPService = ndcPacket.getObject(VirtualTCPService.class);
        //设置所属客户端
        String clientId = getClientId(ctx);
        if (clientId == null) {
            log.error("未绑定客户端编号");
            return;
        }
        virtualTCPService.setNdcClientId(clientId);
        ServiceOperation serviceOperation = ServiceOperation.ofDeploy(virtualTCPService);
        serviceOperation.setNdcServerId(ndcServerId);
        //异步处理
        GlobalBeanContext.EVENT_BUS.post(serviceOperation);
    }

    private void handleOpenChannel(ChannelHandlerContext ctx, NDCPacket ndcPacket) {
        //绑定channel

        Runnable runnable = () -> {
            //todo 异步处理
            ChannelOpen channelOpen = ndcPacket.getObject(ChannelOpen.class);
            String ndcClientId = channelOpen.getNdcClientId();
            channelBind(ndcClientId, ctx);

            NDCClientInfo ndcClientInfo = new NDCClientInfo();
            ndcClientInfo.setChannelHandlerContext(ctx);
            ndcClientInfo.setNdcClientId(ndcClientId);
            ndcClientInfo.setConnectTime(System.currentTimeMillis());
            ndcClientInfo.parseIpPort();


            ndcClientSessionMap.put(ndcClientId, ndcClientInfo);

            //发送响应
            channelOpen.setNdcServerId(ndcServerId);
            ctx.writeAndFlush(NDCPacketBuilder.openChannelPacket(channelOpen));
        };

        GlobalBeanContext.EVENT_BUS.post(runnable);
    }

    /**
     * 获取绑定的clientId
     *
     * @param ctx
     * @return
     */
    private String getClientId(ChannelHandlerContext ctx) {
        //获取绑定的clientId
        Object o = ctx.channel().attr(AttributeKey.valueOf(CLIENT_ID)).get();
        if (o == null) {
            return null;
        }
        return o.toString();
    }

    /**
     * 绑定channel
     *
     * @param clientId
     * @param ctx
     */
    private void channelBind(String clientId, ChannelHandlerContext ctx) {
        ctx.channel().attr(AttributeKey.valueOf(CLIENT_ID)).set(clientId);
        log.info("客户端{}打开通道", clientId);
    }

    /**
     * 向通道写出消息
     *
     * @param ndcClientId
     * @param ndcPacket
     */
    public void write(String ndcClientId, NDCPacket ndcPacket) {
        NDCClientInfo ndcClientInfo = ndcClientSessionMap.get(ndcClientId);

        if (ndcClientInfo != null) {
            ndcClientInfo.getChannelHandlerContext().writeAndFlush(ndcPacket);
        }
    }


}
