package com.view.jndc.core.v2.componet;

import com.view.jndc.core.v2.componet.netty.CustomChannel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * jndc server core functions
 */
@Slf4j
public class JNDCServer {
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    ;


    public void start() {


        InetSocketAddress unresolved = InetSocketAddress.createUnresolved("0.0.0.0", 777);

        ServerBootstrap b = new ServerBootstrap();
        b.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)//
                .localAddress(unresolved)//　
                .childHandler(new CustomChannel());

        b.bind().addListener(x -> {
            if (x.isSuccess()) {
                log.info("核心服务: jndc://启动成功");
            } else {
                log.error("核心服务: jndc://启动失败");
            }

        });
    }
}
