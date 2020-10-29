package jndc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jndc.core.NDCPCodec;
import jndc.utils.LogPrint;

import java.net.InetSocketAddress;

public class JNDCServer {
    public void start() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
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
                .channel(NioServerSocketChannel.class)////　❸ 指定所使用的NIO传输Channel
                .localAddress(new InetSocketAddress(80))//　❹ 使用指定的端口设置套接字地址
                .childHandler(channelInitializer);

        ChannelFuture bind = b.bind().addListener(x -> {
            LogPrint.log("bind");
        });
    }
}
