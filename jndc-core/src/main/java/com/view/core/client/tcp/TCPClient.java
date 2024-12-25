package com.view.core.client.tcp;

import com.view.core.client.ControllableClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
@Data
public class TCPClient extends ControllableClient {
    private TCPClientConfiguration tcpClientConfiguration;

    private ByteClientHandler byteClientHandler;

    private EventLoopGroup workerGroup;


    public void start(TCPClientConfiguration tcpClientConfiguration) {
        tcpClientConfiguration.check();

        TCPClient tcpClient = this;

        String host = tcpClientConfiguration.getHost();
        int port = tcpClientConfiguration.getPort();
        Consumer<TCPClient> startedCallback = tcpClientConfiguration.getStartSuccessCallBack();
        Consumer<TCPClient> startFailCallBack = tcpClientConfiguration.getStartFailCallBack();


        workerGroup = new NioEventLoopGroup();


        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                pipeline.addLast(new ByteArrayDecoder());
                pipeline.addLast(new ByteArrayEncoder());

                byteClientHandler = new ByteClientHandler(tcpClient, tcpClientConfiguration);

                pipeline.addLast(byteClientHandler);

            }
        });


        try {
            // Start the client.
            bootstrap.connect(host, port)
                    .addListener(future -> {
                        if (future.isSuccess()) {
                            log.info("TCP客户端启动成功：{}:{}", host, port);
                            startedCallback.accept(tcpClient);
                        } else {
                            log.error("TCP客户端启动失败：{}:{}", host, port);
                            startFailCallBack.accept(tcpClient);
                        }
                    }).channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("TCP客户端启动失败：{}:{}", host, port, e);
        }
    }

    private void write(byte[] bytes) {
        if (byteClientHandler != null) {
            byteClientHandler.write(bytes);
        } else {
            log.warn("byteArrayHandler is null");
        }
    }

    @Override
    public void sendData(byte[] data) {
        write(data);
    }

    @Override
    public void stop() {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }

        if (tcpClientConfiguration != null) {
            String host = tcpClientConfiguration.getHost();
            int port = tcpClientConfiguration.getPort();
            log.debug("TCP客户端关闭{}:{}", host, port);
        }
    }


}
