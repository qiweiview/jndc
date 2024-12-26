package com.view.core.server.ndc;

import com.view.core.client.ndc.NDCClientInfo;
import com.view.core.component.SupportEnvironment;
import com.view.core.model.ChannelOpen;
import com.view.core.model.TCPDataTransport;
import com.view.core.model.event_bus.ChannelOperation;
import com.view.core.model.event_bus.ServiceOperation;
import com.view.core.model.local_service.LocalService;
import com.view.core.protocol.NDCPCodec;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketBuilder;
import com.view.core.protocol.NDCPacketHelper;
import com.view.core.protocol.callback.ChannelRead0Consumer;
import com.view.core.protocol.callback.ChannelRead0Function;
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
    private SupportEnvironment supportEnvironment = new SupportEnvironment();

    //配置
    private NDCServerConfiguration ndcServerConfiguration;

    private String ndcServerId;

    private NioEventLoopGroup bossGroup;

    private NioEventLoopGroup workerGroup;

    private Channel serverChannel;

    //key:ndcClientId
    private Map<String, ChannelOpen> ndcClientSessionMap = new ConcurrentHashMap<>();



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

        this.ndcServerConfiguration = ndcServerConfiguration;
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
            ChannelRead0Function<NDCPacket, SessionContext> activeCallback = (ctx, msg) -> {
                //todo active回调
                SessionContext sessionContext = SessionContext.of(ctx);
                try {
                    SessionContext apply = ndcServerConfiguration.getConnectActiveCallback().apply(sessionContext);
                    return apply;
                } catch (Exception e) {
                    log.error("连接激活回调异常", e);
                }
                return sessionContext;
            };

            //read回调
            ChannelRead0Consumer<NDCPacket> readCallback = (ctx, msg) -> {
                //todo read回调


                //获取消息
                NDCPacket ndcPacket = msg[0];


                //判断
                if (NDCPacketHelper.isOpenChannelPacket(ndcPacket)) {
                    //todo 通道打开
                    handleOpenChannel(ctx, ndcPacket);
                } else if (NDCPacketHelper.isServiceRegisterPacket(ndcPacket)) {
                    //todo 服务注册消息
                    handleServiceRegister(ctx, ndcPacket);
                } else if (NDCPacketHelper.isServiceUnRegisterPacket(ndcPacket)) {
                    //todo 服务取消注册
                    handleServiceUnRegister(ctx, ndcPacket);
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
            ChannelRead0Consumer<NDCPacket> inActiveCallback = (ctx, msg) -> {
                //todo inActive回调

                Channel channel = ctx.channel();
                SessionContext sessionContext = (SessionContext) channel.attr(AttributeKey.valueOf(NDCServerHandler.SESSION_CONTEXT)).get();

                try {
                    ndcServerConfiguration.getConnectInActiveCallback().accept(sessionContext);
                } catch (Exception e) {
                    log.error("连接中断回调异常", e);
                } finally {
                    //todo 移除上下文
                    channel.attr(AttributeKey.valueOf(NDCServerHandler.SESSION_CONTEXT)).remove();
                }

                String clientId = getClientId(ctx);
                if (clientId == null) {
                    log.error("未绑定客户端编号");

                }
                log.info("连接关闭，客户端编号:{}", clientId);

                ChannelOperation channelOperation = ChannelOperation.ofInactive(clientId);
                //【异步处理】连接中断事件
                supportEnvironment.EVENT_BUS.post(channelOperation);

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
                    supportEnvironment.NDC_SERVER = this;
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

    private void handleServiceUnRegister(ChannelHandlerContext ctx, NDCPacket ndcPacket) {
        LocalService localService = ndcPacket.getObject(LocalService.class);
        //设置所属客户端
        String clientId = getClientId(ctx);
        if (clientId == null) {
            log.error("未绑定客户端编号");
            return;
        }
        localService.setNdcClientId(clientId);
        ServiceOperation serviceOperation = ServiceOperation.ofWithdraw(localService);
        serviceOperation.setNdcServerId(ndcServerId);
        //异步处理
        supportEnvironment.EVENT_BUS.post(serviceOperation);
    }

    /**
     * @param ctx
     * @param ndcPacket
     */
    private void handleTCPActiveFinished(ChannelHandlerContext ctx, NDCPacket ndcPacket) {
        Runnable runnable = () -> {
            //todo 异步处理
            TCPDataTransport tcpDataTransport = ndcPacket.getObject(TCPDataTransport.class);
            supportEnvironment.APP_CENTER.noticeActiveCompleted(tcpDataTransport);
        };

        supportEnvironment.EVENT_BUS.post(runnable);
    }

    private void handleTCPDataPackage(ChannelHandlerContext ctx, NDCPacket ndcPacket) {
        TCPDataTransport tcpDataTransport = ndcPacket.getObject(TCPDataTransport.class);
        log.debug("收到数据包：{}:{}，延迟：{}", ndcPacket.getLocalAddress(), ndcPacket.getLocalPort(), ndcPacket.packageTimeout());
        supportEnvironment.EVENT_BUS.post(tcpDataTransport);
    }

    private void handleServiceRegister(ChannelHandlerContext ctx, NDCPacket ndcPacket) {
        LocalService localService = ndcPacket.getObject(LocalService.class);
        //设置所属客户端
        String clientId = getClientId(ctx);
        if (clientId == null) {
            log.error("未绑定客户端编号");
            return;
        }
        localService.setNdcClientId(clientId);
        ServiceOperation serviceOperation = ServiceOperation.ofDeploy(localService);
        serviceOperation.setNdcServerId(ndcServerId);
        //异步处理
        supportEnvironment.EVENT_BUS.post(serviceOperation);
    }

    /**
     * 处理打开通道
     *
     * @param ctx
     * @param ndcPacket
     */
    private void handleOpenChannel(ChannelHandlerContext ctx, NDCPacket ndcPacket) {
        //todo 绑定channel

        ChannelOpen channelOpen = ndcPacket.getObject(ChannelOpen.class);
        String ndcClientId = channelOpen.getNdcClientId();

        //通道绑定事件
        Runnable runnable = () -> {
            //todo 异步处理

            //绑定channel
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

        supportEnvironment.EVENT_BUS.post(runnable);

        Channel channel = ctx.channel();
        SessionContext sessionContext = (SessionContext) channel.attr(AttributeKey.valueOf(NDCServerHandler.SESSION_CONTEXT)).get();

        //通道打开事件
        Runnable runnable1 = () -> {
            sessionContext.setClientUniqueId(ndcClientId);
            ndcServerConfiguration.getOpenChannelCallback().accept(sessionContext);
        };
        supportEnvironment.EVENT_BUS.post(runnable1);


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


}
