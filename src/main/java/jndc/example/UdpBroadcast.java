package jndc.example;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import jndc.utils.InetUtils;
import jndc.utils.LogPrint;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class UdpBroadcast {

    private static EventLoopGroup group;
    private static Bootstrap bootstrap;


    public static void main(String[] args) throws Exception {

        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {

            }
        };

        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(channelInitializer);

        InetSocketAddress inetSocketAddress = new InetSocketAddress("255.255.255.255", 13);
        ChannelFuture bind = bootstrap.bind(0);
        bind.addListeners(x -> {
            if (x.isSuccess()) {
                String msg= InetAddress.getLocalHost() +"use jndc";
                DatagramPacket datagramPacket = new DatagramPacket(Unpooled.copiedBuffer(msg.getBytes()), inetSocketAddress);
                bind.channel().writeAndFlush(datagramPacket);
                LogPrint.info("udp success");
            } else {
                LogPrint.err("udp fail");
            }
        });
    }


}
