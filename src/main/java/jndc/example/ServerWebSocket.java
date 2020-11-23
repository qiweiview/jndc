package jndc.example;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.ImmediateEventExecutor;
import jndc.utils.LogPrint;


import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;


public class ServerWebSocket {
    private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private final EventLoopGroup group = new NioEventLoopGroup();
    private Channel channel;

    public void start(InetSocketAddress address) { //引导服务器


        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addFirst("A",new HttpServerCodec());
                pipeline.addAfter("A","B",new HttpObjectAggregator(64 * 1024));
                pipeline.addAfter("B","C",new WebSocketServerProtocolHandler("/ws"));
                pipeline.addAfter("C","D",new TextWebSocketFrameHandler(channelGroup));
            }
        };


        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(channelInitializer);
        ChannelFuture future = bootstrap.bind(address);
        future.syncUninterruptibly();
        channel = future.channel();
    }




    public static void main(String[] args) throws Exception {

        int port = 888;
        final ServerWebSocket endpoint = new ServerWebSocket();
        endpoint.start(new InetSocketAddress(port));


        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("close by vvvvvvvvvvvvvv")));

    }

    public static class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

        private final ChannelGroup group;

        public TextWebSocketFrameHandler(ChannelGroup group) {
            this.group = group;
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {//重写userEventTriggered()方法以处理自定义事件
            LogPrint.info(evt.getClass());
            if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete){
                WebSocketServerProtocolHandler.HandshakeComplete handshakeComplete= (WebSocketServerProtocolHandler.HandshakeComplete) evt;
                LogPrint.info(handshakeComplete.selectedSubprotocol());
                group.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined"));
                group.add(ctx.channel());
            }

        }

        @Override
        public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
            group.writeAndFlush(msg.retain()); //❸增加消息的引用计数，并将它写到ChannelGroup 中所有已经连接的客户端
        }

    }

    public static class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
        private final String wsUri;




        public HttpRequestHandler(String wsUri) {
            this.wsUri = wsUri;
        }

        @Override
        public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
            //request.p
            ctx.fireChannelRead(request.retain());
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
