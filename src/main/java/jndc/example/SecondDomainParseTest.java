package jndc.example;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jndc.core.NettyComponentConfig;
import jndc.utils.InetUtils;
import jndc.utils.LogPrint;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class SecondDomainParseTest {

    public static void main(String[] args) {

        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();

                pipeline.addFirst(new SecondDomainHandle());

            }
        };

        InetSocketAddress unresolved = new InetSocketAddress(InetUtils.localInetAddress, 88);


        ServerBootstrap b = new ServerBootstrap();
        b.group(NettyComponentConfig.getNioEventLoopGroup())
                .channel(NioServerSocketChannel.class)//
                .localAddress(unresolved)//　
                .childHandler(channelInitializer);

        b.bind().addListener(x -> {
            if (x.isSuccess()) {
                LogPrint.info("success");
            } else {
                Object object = x.get();

                LogPrint.err("fail");
            }

        });
    }

    public static class SecondDomainHandle extends ChannelInboundHandlerAdapter{

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Channel channel = ctx.channel();
            InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
            String hostName = socketAddress.getHostName();
            ctx.close();
        }
    }
}
