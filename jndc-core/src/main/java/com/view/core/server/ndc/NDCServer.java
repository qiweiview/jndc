package com.view.core.server.ndc;

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
import com.view.core.utils.RuntimeUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class NDCServer {
    private final String ndcServerId = RuntimeUtils.getRuntimeUniqueId();

    //key:ndcClientId
    private Map<String, ChannelHandlerContext> ndcClientSessionMap = new HashMap<>();

    public static final String CLIENT_ID = "CLIENT_ID";

    public void start(int port) {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
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

                //判断
                if (NDCPacketHelper.isOpenChannelPacket(ndcPacket)) {
                    //todo 通道打开

                    //绑定channel
                    ChannelOpen channelOpen = ndcPacket.getObject(ChannelOpen.class);
                    String ndcClientId = channelOpen.getNdcClientId();
                    channelBind(ndcClientId, ctx);

                    ndcClientSessionMap.put(ndcClientId, ctx);

                    //发送响应
                    channelOpen.setNdcServerId(ndcServerId);
                    ctx.writeAndFlush(NDCPacketBuilder.openChannelPacket(channelOpen));
                } else if (NDCPacketHelper.isServiceRegisterPacket(ndcPacket)) {
                    //todo 服务注册消息
                    VirtualTCPService virtualTCPService = ndcPacket.getObject(VirtualTCPService.class);
                    //设置所属客户端
                    String clientId = getClientId(ctx);
                    virtualTCPService.setNdcClientId(clientId);
                    ServiceOperation serviceOperation = ServiceOperation.ofDeploy(virtualTCPService);
                    serviceOperation.setNdcServerId(ndcServerId);
                    //异步处理
                    GlobalBeanContext.EVENT_BUS.post(serviceOperation);
                } else if (NDCPacketHelper.isTCPDataPacket(ndcPacket)) {
                    log.info("数据类型消息：{}", msg);
                    TCPDataTransport tcpDataTransport = ndcPacket.getObject(TCPDataTransport.class);
                    GlobalBeanContext.EVENT_BUS.post(tcpDataTransport);
                } else if (NDCPacketHelper.isChannelHeartBeat(ndcPacket)) {
                    log.debug("心跳消息：{}", msg);
                } else {
                    log.warn("未知的数据包类型:{}", ndcPacket.getType());
                }
            };


            //inActive回调
            ChannelRead0CallBack inActiveCallback = (ctx, msg) -> {
                //todo inActive回调

                String clientId = getClientId(ctx);
                log.info("连接关闭，客户端编号:{}", clientId);

                ChannelOperation channelOperation = ChannelOperation.ofInactive(clientId);
                //【异步处理】连接中断事件
                GlobalBeanContext.EVENT_BUS.post(channelOperation);

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

            log.info("起动NDC服务，端口：{}", port);
            b.bind(port).addListener(future -> {
                if (future.isSuccess()) {
                    GlobalBeanContext.NDC_SERVER = this;
                    log.info("NDC服务启动成功，端口：{}", port);
                } else {
                    log.error("NDC服务启动失败，端口：{}", port);
                }
            }).channel().closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
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
            throw new RuntimeException("未绑定clientId");
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
        ChannelHandlerContext channelHandlerContext = ndcClientSessionMap.get(ndcClientId);
        if (channelHandlerContext != null) {
            channelHandlerContext.writeAndFlush(ndcPacket);
            log.info("向NDC客户端{}发送消息", new String(ndcPacket.getData()));
        }
    }
}
