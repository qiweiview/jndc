package com.view.core.server.tcp;


import com.view.core.model.TCPDataTransport;
import com.view.core.server.ControllableServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Data
@Slf4j
public class TCPServer extends ControllableServer {
    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private int port;

    public void start(int port) {
        start(port, () -> {
            //todo 服务启动回调
        });
    }

    public void start(int port, Runnable startCallBack) {
        TCPServer tcpServer = this;
        this.port = port;

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new ByteArrayDecoder());
                        pipeline.addLast(new ByteArrayEncoder());
                        pipeline.addLast(new ByteServerHandler(tcpServer));


                    }
                });

        try {
            serverBootstrap.bind(port).sync().addListener(future -> {
                if (future.isSuccess()) {
                    log.info("TCP服务启动成功，端口：{}", port);
                    startCallBack.run();
                } else {
                    log.error("TCP服务启动失败，端口：{}", port);
                }
            }).channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("TCP服务启动失败", e);
        }
    }

    @Override
    public void stop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        log.info("tcp服务{}端口监听关闭成功", port);
    }


    public void receiveData(TCPDataTransport tcpDataTransport) {
        Map<String, ChannelHandlerContext> sessionMap = getSessionMap();
        String appServerSessionId = tcpDataTransport.getAppServerSessionId();
        ChannelHandlerContext ctx = sessionMap.get(appServerSessionId);
        if (ctx != null) {
            ctx.writeAndFlush(tcpDataTransport.getData());
        }
    }
}
