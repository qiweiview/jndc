package jndc.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jndc.core.NDCPCodec;
import jndc.core.NettyComponentConfig;
import jndc.server.JNDCServerMessageHandle;
import jndc.utils.LogPrint;

import java.net.InetSocketAddress;

public class JNDCServer {
    public static final Integer SERVER_PORT=81;



    public static void main(String[] args) {
        EventLoopGroup eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();
        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<>() {

            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addFirst(NDCPCodec.NAME, new NDCPCodec());
                pipeline.addAfter(NDCPCodec.NAME, JNDCServerMessageHandle.NAME, new JNDCServerMessageHandle());
            }
        };

        ServerBootstrap b = new ServerBootstrap();
        b.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)//
                .localAddress(new InetSocketAddress(SERVER_PORT))//　
                .childHandler(channelInitializer);

        ChannelFuture bind = b.bind().addListener(x -> {
            LogPrint.log("bind admin port:"+SERVER_PORT);
        });
        ChannelFuture channelFuture = bind.channel().closeFuture();


    }


}
