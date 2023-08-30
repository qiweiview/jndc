package com.view.jndc.core.v2.componet.client;

import com.view.jndc.core.v2.componet.netty.CustomChannel;
import com.view.jndc.core.v2.model.jndc.JNDCData;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JNDCClient {

    private CustomChannel customChannel;

    public void start(String host, int port) {

        Bootstrap b = new Bootstrap();


        customChannel = new CustomChannel();


        b.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)//
                .option(ChannelOption.SO_KEEPALIVE, true)//tcp keep alive
                .handler(customChannel);

        ChannelFuture connect = b.connect(host, port);
        connect.addListeners(x -> {
            if (x.isSuccess()) {
                //todo 连接成功


                log.info("连接成功");
            } else {
                //todo 连接失败

                log.error("连接成功");
            }

        });
    }

    public void write(JNDCData jndcData) {
        customChannel.write(jndcData);
    }
}
