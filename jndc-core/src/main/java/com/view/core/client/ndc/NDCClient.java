package com.view.core.client.ndc;

import com.view.core.model.VirtualService;
import com.view.core.protocol.NDCPCodec;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketBuilder;
import com.view.core.protocol.callback.ChannelRead0CallBack;
import com.view.core.utils.RuntimeUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NDCClient {

    private final String clientId = RuntimeUtils.getRuntimeUniqueId();
    private List<NDCPacket> afterActive = new ArrayList<>();
    private volatile ChannelHandlerContext serverContext;

    private String host;
    private int port;


    public void start(String host, int port) {
        if (this.host == null) {
            this.host = host;
        }

        if (this.port == 0) {
            this.port = port;
        }

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {


            ChannelRead0CallBack activeCallback = (ctx, msg) -> {
                //todo active回调

                serverContext = ctx;

                afterActive.forEach(ndcPacket -> {
                    ctx.writeAndFlush(ndcPacket);
                });

            };

            ChannelRead0CallBack<NDCPacket> readCallback = (ctx, msg) -> {
                //todo read回调

                //获取消息
                NDCPacket ndcPacket = msg[0];
                log.info("client收到消息：{}", ndcPacket);

            };

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
                    .connect(host, port)
                    .sync();//同步等待连接成功

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();

            log.error("连接断开，等待15秒，尝试重连");
            try {
                TimeUnit.SECONDS.sleep(15);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            //再次启动
            start(this.host, this.port);
        }
    }

    public void registerService(VirtualService virtualService) {
        if (serverContext == null) {
            virtualService.setBelongClient(clientId);
            afterActive.add(NDCPacketBuilder.registerServicePacket(virtualService));
        } else {
            serverContext.writeAndFlush(NDCPacketBuilder.registerServicePacket(virtualService));
        }

    }
}
