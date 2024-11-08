package com.view.core.client.tcp;

import com.view.core.client.ControllableClient;
import com.view.core.model.DataSlot;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Data
public class TCPClient extends ControllableClient {
    private ByteClientHandler byteClientHandler;

    private EventLoopGroup workerGroup;

    private List<DataSlot<byte[]>> slots = new ArrayList<>();

    public void start(String host, int port, Runnable callBack) {
        TCPClient tcpClient = this;

        workerGroup = new NioEventLoopGroup();


        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                pipeline.addLast(new ByteArrayDecoder());
                pipeline.addLast(new ByteArrayEncoder());

                byteClientHandler = new ByteClientHandler(tcpClient);

                pipeline.addLast(byteClientHandler);

            }
        });


        try {
            // Start the client.
            bootstrap.connect(host, port)
                    .addListener(future -> {
                        if (future.isSuccess()) {
                            callBack.run();
                            log.debug("TCP客户端启动成功：{}:{}", host, port);
                        } else {
                            log.error("TCP客户端启动失败：{}:{}", host, port);
                        }
                    }).channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("TCP客户端启动失败：{}:{}", host, port, e);
        }
    }

    private void write(byte[] bytes) {
        if (byteClientHandler != null) {
            byteClientHandler.write(bytes);
        } else {
            log.warn("byteArrayHandler is null");
        }
    }

    @Override
    public void receiveData(byte[] data) {
        write(data);
    }

    @Override
    public void stop() {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    public void addSlot(Consumer<byte[]> o) {
        DataSlot<byte[]> dataSlot = new DataSlot<>(o);
        slots.add(dataSlot);
    }
}
