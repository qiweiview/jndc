package com.view.core.client.ndc;

import com.view.core.model.ChannelOpen;
import com.view.core.model.TCPDataTransport;
import com.view.core.model.VirtualTCPService;
import com.view.core.protocol.NDCPCodec;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketBuilder;
import com.view.core.protocol.NDCPacketHelper;
import com.view.core.protocol.callback.ChannelRead0CallBack;
import com.view.core.utils.RuntimeUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NDCClient {
    private final String ndcClientId = RuntimeUtils.getRuntimeUniqueId();
    private List<NDCPacket> tobeSendPackage = new ArrayList<>();
    private volatile ChannelHandlerContext serverContext;
    private NDCClientConfiguration ndcClientConfiguration;
    private int retryTimes = 0;


    private Map<String, ChannelHandlerContext> ndcClientSessionMap = new HashMap<>();


    public void start(NDCClientConfiguration ndcClientConfiguration) {
        if (this.ndcClientConfiguration == null) {
            this.ndcClientConfiguration = ndcClientConfiguration;
        }

        ndcClientConfiguration.printConfiguration();


        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            //active回调
            ChannelRead0CallBack activeCallback = (ctx, msg) -> {
                //todo active回调

                //重置重试次数
                retryTimes = 0;

                //设置服务端上下文
                serverContext = ctx;

                //发送开通通道包
                ChannelOpen channelOpen = new ChannelOpen();
                channelOpen.setNdcClientId(ndcClientId);
                NDCPacket openChannelPacket = NDCPacketBuilder.openChannelPacket(channelOpen);
                ctx.writeAndFlush(openChannelPacket);


            };

            //read回调
            ChannelRead0CallBack<NDCPacket> readCallback = (ctx, msg) -> {
                //todo read回调

                //获取消息
                NDCPacket ndcPacket = msg[0];
                log.debug("client收到消息：{}", ndcPacket);

                //判断
                if (NDCPacketHelper.isOpenChannelPacket(ndcPacket)) {
                    ChannelOpen channelOpen = ndcPacket.getObject(ChannelOpen.class);
                    log.info("得到服务器{}打开通道确认消息，准备发送缓冲区数据包：{}", channelOpen.getNdcServerId(), tobeSendPackage.size());
                    //发送缓冲区报文
                    tobeSendPackage.forEach(tobeSend -> {
                        ctx.writeAndFlush(tobeSend);
                    });
                } else if (NDCPacketHelper.isTCPActivePacket(ndcPacket)) {
                    TCPDataTransport tcpDataTransport = ndcPacket.getObject(TCPDataTransport.class);
                    log.info("收到打开本地连接请求包,远程会话id：{}", tcpDataTransport.getAppServerSessionId());
                } else if (NDCPacketHelper.isTCPDataPacket(ndcPacket)) {
                    TCPDataTransport tcpDataTransport = ndcPacket.getObject(TCPDataTransport.class);
                    log.info("收到远程写入数据{}", new String(tcpDataTransport.getData()));
                } else {
                    log.warn("未知的数据包类型:{}", ndcPacket.getType());
                }
            };

            //inActive回调
            ChannelRead0CallBack inActiveCallback = (ctx, msg) -> {
                //todo inActive回调
            };


            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);

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
            b.handler(channelInitializer);

            // Start the client.
            ChannelFuture f = b
                    .connect(ndcClientConfiguration.getHost(), ndcClientConfiguration.getPort())
                    .sync();//同步等待连接成功

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
            int timeoutSecond = ndcClientConfiguration.getTimeoutSecond();
            log.error("连接断开，等待{}秒，进行第{}次尝试重连", timeoutSecond, retryTimes++);
            try {
                TimeUnit.SECONDS.sleep(timeoutSecond);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            //再次启动
            start(ndcClientConfiguration);
        }
    }

    public void registerService(VirtualTCPService virtualTCPService) {
        if (serverContext == null) {
            tobeSendPackage.add(NDCPacketBuilder.registerServicePacket(virtualTCPService));
        } else {
            serverContext.writeAndFlush(NDCPacketBuilder.registerServicePacket(virtualTCPService));
        }

    }


}
