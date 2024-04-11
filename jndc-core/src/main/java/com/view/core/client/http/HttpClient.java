package com.view.core.client.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

@Slf4j
public class HttpClient {

    private Integer maxContentLength = 10 * 1024 * 1024;

    public void start(String urlString) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            URL url = new URL(urlString);
            String protocol = url.getProtocol();
            String host = url.getHost();
            int port = url.getPort();
            if (port == -1) {
                port = (protocol.equals("https") || protocol.equals("wss")) ? 443 : 80;
            }
            String path = url.getPath();


            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    if (protocol.equalsIgnoreCase("https") || protocol.equalsIgnoreCase("wss")) {
                        // 添加SSLContext以支持HTTPS
                        SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
                        pipeline.addLast(sslContext.newHandler(ch.alloc()));
                    }

                    // 添加HttpServerCodec用于处理HTTP请求
                    pipeline.addLast("codec", new HttpClientCodec());

                    // 添加HttpObjectAggregator，将HTTP消息的多个部分聚合成完整的HTTP消息
                    pipeline.addLast("aggregator", new HttpObjectAggregator(maxContentLength));

                    DefaultHttpHeaders entries = new DefaultHttpHeaders();
                    entries.set("host", "127.0.0.1");

                    //创建WebSocketClientProtocolConfig
                    WebSocketClientProtocolConfig config = WebSocketClientProtocolConfig.newBuilder()
                            .webSocketUri(path)
                            .version(WebSocketVersion.V13)
                            .customHeaders(entries)
                            .maxFramePayloadLength(1280000)
                            .subprotocol(null)
                            .allowExtensions(true)
                            .build();

                    // 添加WebSocket协议处理器
                    pipeline.addLast(new WebSocketClientProtocolHandler(config));

                    // 添加处理器处理FullHttpResponse
                    pipeline.addLast(new CustomerHttpClientHandler(path));

                    // 添加处理器处理处理WebSocketFrame
                    pipeline.addLast(new CustomerWebsocketClientHandler());
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
