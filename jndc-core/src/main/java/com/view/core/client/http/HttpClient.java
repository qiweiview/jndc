package com.view.core.client.http;

import com.view.core.client.ControllableClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Slf4j
public class HttpClient extends ControllableClient {

    private Integer maxContentLength = 5 * 1024;

    public void start(String urlString) {
        int port;
        String host;
        String path;
        boolean useSsl = false;
        boolean isWebSocket = false;

        //判断是http还是websocket
        if (urlString.startsWith("http")) {
            try {
                URL url = new URL(urlString);
                path = url.getPath();
                port = url.getPort();
                if (port == -1) {
                    if (url.getProtocol().equalsIgnoreCase("https")) {
                        port = 443;
                        useSsl = true;
                    } else {
                        port = 80;
                    }
                }
                host = url.getHost();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        } else if (urlString.startsWith("ws")) {
            isWebSocket = true;
            try {
                URI uri = new URI(urlString);
                path = uri.getPath();
                port = uri.getPort();
                if (port == -1) {
                    if (uri.getScheme().equalsIgnoreCase("wss")) {
                        port = 443;
                        useSsl = true;
                    } else {
                        port = 80;
                    }
                }
                host = uri.getHost();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("不支持的协议");
        }

        EventLoopGroup workerGroup = new NioEventLoopGroup();




            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap.group(workerGroup);
            clientBootstrap.channel(NioSocketChannel.class);
            clientBootstrap.option(ChannelOption.SO_KEEPALIVE, true);

            boolean finalUseSsl = useSsl;
            String finalPath = path;

            boolean finalIsWebSocket = isWebSocket;
            clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    if (finalUseSsl) {
                        // 添加SSLContext以支持HTTPS
                        SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
                        pipeline.addLast(sslContext.newHandler(ch.alloc()));
                    }

                    // 添加HttpServerCodec用于处理HTTP请求
                    pipeline.addLast(new HttpClientCodec());

                    // 添加HttpObjectAggregator，将HTTP消息的多个部分聚合成完整的HTTP消息
                    pipeline.addLast(new HttpObjectAggregator(maxContentLength));


                    if (finalIsWebSocket) {
                        //todo 处理websocket消息

                        WebSocketClientProtocolHandler wsHandler = new WebSocketClientProtocolHandler(
                                WebSocketClientHandshakerFactory.newHandshaker(
                                        new URI(urlString),
                                        WebSocketVersion.V13,
                                        null,
                                        false,
                                        HttpHeaders.EMPTY_HEADERS,
                                        65536));


                        // 添加WebSocket协议处理器
                        pipeline.addLast(wsHandler);

                        // 添加处理器处理FullHttpResponse
                        pipeline.addLast(new CustomerWebsocketClientHandler());
                    } else {
                        //todo 处理http消息

                        // 添加处理器处理FullHttpResponse
                        pipeline.addLast(new CustomerHttpClientHandler(finalPath));
                    }

                }
            });


        try {
            // Start the client.
            ChannelFuture f = clientBootstrap.connect(host, port).sync(); // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void receiveData(byte[] data) {

    }

    @Override
    public void stop() {

    }
}
