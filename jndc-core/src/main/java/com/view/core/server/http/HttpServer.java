package com.view.core.server.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
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
public class HttpServer {
    private String websocketPath = "/websocket";

    private SslContext sslContext;

    private Integer maxContentLength = 10 * 1024 * 1024;

    public void start(int port) {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
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
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
