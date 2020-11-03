package jndc.example;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import jndc.core.NettyComponentConfig;
import jndc.utils.InetUtils;
import jndc.utils.LogPrint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;


public class ReconnectClient {
    private  static final Logger logger = LoggerFactory.getLogger(ReconnectClient.class);
    
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
                logger.debug("connect fail , retry connect");
                eventExecutors.schedule(() -> {
                    createNewOne(eventExecutors);
                }, 1L, TimeUnit.SECONDS);
            }else {
                logger.debug("connect success");
            }

        });
    }

    public static class Inbount extends ChannelInboundHandlerAdapter {

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            EventLoop eventExecutors = ctx.channel().eventLoop();
            eventExecutors.schedule(() -> {
                logger.debug("connection interrupted retry connect");
                createNewOne(eventExecutors);
            }, 1L, TimeUnit.SECONDS);

        }

//        @Override
//        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//            if (cause instanceof IOException){
//                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
//                logger.debug("throw error"+cause);
//            }
//        }
    }


}
