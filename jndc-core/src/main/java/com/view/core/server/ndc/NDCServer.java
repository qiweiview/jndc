package com.view.core.server.ndc;


import com.view.core.component.SupportEnvironment;
import com.view.core.model.ChannelOpen;
import com.view.core.protocol.NDCPCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Data
@Slf4j
public class NDCServer {
    private SupportEnvironment supportEnvironment = new SupportEnvironment();

    //配置
    private NDCServerConfiguration ndcServerConfiguration;

    private String ndcServerId;

    private NioEventLoopGroup bossGroup;

    private NioEventLoopGroup workerGroup;

    private Channel serverChannel;

    //key:ndcClientId
    private Map<String, ChannelOpen> ndcClientSessionMap = new ConcurrentHashMap<>();



    public static final String CLIENT_ID = "CLIENT_ID";

    public void stop() {
        if (serverChannel != null && serverChannel.isOpen()) {
            serverChannel.close();
            log.info("NDC服务关闭");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            ndcServerConfiguration.getStopCallback().run();
        }

    }


    public void start(NDCServerConfiguration ndcServerConfiguration) {

        this.ndcServerConfiguration = ndcServerConfiguration;
        //设置配置
        ndcServerConfiguration.check();

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        String host = ndcServerConfiguration.getHost();
        int port = ndcServerConfiguration.getPort();
        Runnable startedCallback = ndcServerConfiguration.getStartedCallback();
        Consumer<Exception> failCallback = ndcServerConfiguration.getFailCallback();


        try {

            //创建ndc服务端处理器
            ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    //NDC协议处理
                    pipeline.addLast(new NDCPCodec());

                    NDCServerHandler ndcServerHandler = new NDCServerHandler(ndcServerConfiguration);

                    //NDC Packet 处理器
                    pipeline.addLast(ndcServerHandler);
                }
            };

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer);


            log.info("起动NDC服务，{}：{}", host, port);
            ChannelFuture channelFuture = b.bind(host, port).sync();
            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    supportEnvironment.NDC_SERVER = this;
                    startedCallback.run();
                    log.info("NDC服务启动成功，{}：{}", host, port);
                } else {
                    log.error("NDC服务启动失败，{}：{}", host, port);
                    failCallback.accept(new RuntimeException("NDC服务启动失败"));
                }
            });
            serverChannel = channelFuture.channel();
            // 阻塞直到服务器关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            failCallback.accept(e);
        } finally {
            stop();
        }
    }


}
