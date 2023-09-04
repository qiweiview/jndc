package com.view.jndc.core.v2.componet.server;

import com.view.jndc.core.v2.componet.SpaceManager;
import com.view.jndc.core.v2.componet.netty.CustomChannel;
import com.view.jndc.core.v2.enum_value.HandlerType;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * jndc server core functions
 */
@Slf4j
public class JNDCServer extends SpaceManager {
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();


    public void start(int port) {
        ServerBootstrap b = new ServerBootstrap();
        b.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new CustomChannel(HandlerType.SERVER_HANDLER.value));

        b.bind(port).addListener(x -> {
            if (x.isSuccess()) {
                log.info("核心服务: jndc://启动成功");
            } else {
                log.error("核心服务: jndc://启动失败");
            }

        });
    }
}
