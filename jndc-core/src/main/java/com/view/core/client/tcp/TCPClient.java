package com.view.core.client.tcp;

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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Slf4j
@Data
public class TCPClient {
    private TCPClientConfiguration tcpClientConfiguration;

    private ByteClientHandler byteClientHandler;

    private EventLoopGroup workerGroup;


    public void start(TCPClientConfiguration tcpClientConfiguration) {
        tcpClientConfiguration.check();
        this.tcpClientConfiguration = tcpClientConfiguration;

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


    public void writeAndFlush(byte[] data) {
        Thread thread = Thread.currentThread();
        if (byteClientHandler == null||byteClientHandler.getCtx()==null) {
            synchronized (thread) {
                if (byteClientHandler == null||byteClientHandler.getCtx()==null) {
                    List<Thread> waitingActiveThead = tcpClientConfiguration.getWaitingActiveThead();
                    waitingActiveThead.add(thread);
                    long timeout = tcpClientConfiguration.getConnectTimeout();
                    try {
                        log.warn("等待{}秒本地客户端建立", timeout);
                        thread.wait(timeout);
                    } catch (InterruptedException e) {
                        log.error("等待{}秒本地客户端建立超时:{}", timeout, e.getMessage());
                        throw new RuntimeException("等待本地客户端建立超时");
                    }
                }
            }
        }

        byteClientHandler.write(data);
    }
}
