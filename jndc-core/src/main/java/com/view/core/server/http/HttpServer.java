package com.view.core.server.http;

import com.view.core.server.ControllableServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class HttpServer extends ControllableServer {
    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private String websocketPath = "/websocket";

    private SslContext sslContext;

    private Integer maxContentLength = 10 * 1024 * 1024;

    private int port;

    public void start(int port) {
        start(port, () -> {
            //todo 服务启动回调
        });
    }

    public void start(int port, Runnable startCallBack) {
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

                        //添加ssl支持
                        if (sslContext != null) {
                            pipeline.addLast(sslContext.newHandler(ch.alloc()));
                        }

                        // 添加HttpServerCodec用于处理HTTP请求
                        pipeline.addLast(new HttpServerCodec());

                        // 添加HttpObjectAggregator，将HTTP消息的多个部分聚合成完整的HTTP消息
                        pipeline.addLast(new HttpObjectAggregator(maxContentLength));

                        // 添加支持WebSocket的Handler
                        pipeline.addLast(new WebSocketServerProtocolHandler(websocketPath));

                        //处理websocket消息
                        pipeline.addLast(new CustomerWebsocketServerHandler());

                        //处理http消息
                        pipeline.addLast(new CustomerHttpServerHandler());


                    }
                });

        log.info("起动服务" + (sslContext == null ? "http" : "https") + "://127.0.0.1:" + port);
        try {
            serverBootstrap.bind(port).sync().addListener(future -> {
                if (future.isSuccess()) {
                    log.info("HTTP服务启动成功，端口：{}", port);
                    startCallBack.run();
                } else {
                    log.error("HTTP服务启动失败，端口：{}", port);
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

        log.debug("http服务{}端口监听关闭成功", port);
    }
}
