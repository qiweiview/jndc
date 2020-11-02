package jndc.example;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import jndc.core.NettyComponentConfig;
import jndc.utils.InetUtils;
import jndc.utils.LogPrint;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;


public class ReconnectClient {

    public static final Integer SERVER_PORT=81;

    public static void main(String[] args) {
        EventLoopGroup group = NettyComponentConfig.getNioEventLoopGroup();
        createNewOne(group);
    }

    public static void createNewOne(EventLoopGroup group) {
        Bootstrap b = new Bootstrap();
        ChannelInitializer channelInitializer = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addFirst(new Inbount());

            }
        };

        b.group(group)
                .channel(NioSocketChannel.class)//
                .handler(channelInitializer);

        InetSocketAddress localInetAddress = InetUtils.getLocalInetAddress(EchoServer.SERVER_PORT);
        ChannelFuture connect = b.connect(localInetAddress);
        connect.addListeners(x -> {
            if (!x.isSuccess()) {
                final EventLoop eventExecutors = connect.channel().eventLoop();
                LogPrint.log("connect fail , retry connect");
                eventExecutors.schedule(() -> {
                    createNewOne(eventExecutors);
                }, 1L, TimeUnit.SECONDS);
            }else {
                LogPrint.log("connect success");
            }

        });
    }

    public static class Inbount extends ChannelInboundHandlerAdapter {

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            EventLoop eventExecutors = ctx.channel().eventLoop();
            eventExecutors.schedule(() -> {
                LogPrint.log("connection interrupted retry connect");
                createNewOne(eventExecutors);
            }, 1L, TimeUnit.SECONDS);

        }

//        @Override
//        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//            if (cause instanceof IOException){
//                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
//                LogPrint.log("throw error"+cause);
//            }
//        }
    }


}
