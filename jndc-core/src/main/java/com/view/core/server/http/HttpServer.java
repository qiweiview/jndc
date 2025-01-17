package com.view.core.server.http;

import com.view.core.server.ControllableServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Data
@Slf4j
public class HttpServer extends ControllableServer {
    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private String websocketPath = "/websocket";

    private Channel serverChannel;

    private Integer maxContentLength = 10 * 1024 * 1024;

    private HttpServerConfiguration httpServerConfiguration;


    public void start(HttpServerConfiguration configuration) {
        Runnable startedCallback = configuration.getStartCallBack();
        Consumer<Exception> failCallback = configuration.getFailCallback();

        SslContext sslContext = configuration.getSslContext();
        int port = configuration.getPort();
        String host = configuration.getHost();
        this.httpServerConfiguration = configuration;

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        try {
            ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<>() {
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
                    pipeline.addLast(new CustomerHttpServerHandler(configuration));


                }
            };

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer);


            log.info("起动服务" + (sslContext == null ? "http" : "https") + "://127.0.0.1:" + port);

            ChannelFuture channelFuture = b.bind(host, port).sync();
            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    startedCallback.run();
                    log.info("http服务启动成功，{}：{}", host, port);
                } else {
                    log.error("http服务启动失败，{}：{}", host, port);
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

    @Override
    public void stop() {
        if (serverChannel != null && serverChannel.isOpen()) {
            serverChannel.close();
            log.info("NDC服务关闭");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            httpServerConfiguration.getStopCallback().run();
        }

    }
}
