package com.view.core.client.ndc;

import com.view.core.protocol.NDCPCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class NDCClient {
    private NDCClientHandler ndcClientHandler;

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


            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);

            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    // 添加HttpServerCodec用于处理HTTP请求
                    pipeline.addLast(new NDCPCodec());

                    ndcClientHandler = new NDCClientHandler();
                    pipeline.addLast(ndcClientHandler);

                }
            });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();

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
            start(this.host, this.port);
        }
    }

    /**
     * 发送心跳
     */
    public void sendHeartBeat() {
        //todo 发送心跳
        if (ndcClientHandler != null) {
            ndcClientHandler.sendHeartBeat();
        } else {
            log.error("ndcClientHandler is null");
        }
    }
}
