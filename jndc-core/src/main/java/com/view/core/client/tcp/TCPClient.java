package com.view.core.client.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TCPClient {
    private ByteClientHandler byteClientHandler;


    public void start(String host, int port) {


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

                    pipeline.addLast(new ByteArrayDecoder());
                    pipeline.addLast(new ByteArrayEncoder());

                    byteClientHandler = new ByteClientHandler();

                    pipeline.addLast(byteClientHandler);

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
        }
    }

    public void write(byte[] bytes) {
        if (byteClientHandler != null) {
            byteClientHandler.write(bytes);
        } else {
            log.warn("byteArrayHandler is null");
        }
    }
}
