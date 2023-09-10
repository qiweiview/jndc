package com.view.jndc.core.v2.componet.client;

import com.view.jndc.core.v2.componet.SpaceManager;
import com.view.jndc.core.v2.componet.netty.handler.ClientTCPDataHandle;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class TCPClient extends SpaceManager {


    public void start(String host, int port) {


        Bootstrap b = new Bootstrap();

        ChannelInitializer channelInitializer = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addFirst(ClientTCPDataHandle.NAME, new ClientTCPDataHandle());
            }
        };


        b.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)//
                .option(ChannelOption.SO_KEEPALIVE, true)//tcp keep alive
                .handler(channelInitializer);

        InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);
        ChannelFuture connect = b.connect(inetSocketAddress);

        try {
            connect.sync();
        } catch (InterruptedException e) {
            log.error("连接" + inetSocketAddress + "异常：" + e.getMessage());
        }
    }

}
