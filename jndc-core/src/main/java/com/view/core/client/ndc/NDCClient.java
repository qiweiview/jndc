package com.view.core.client.ndc;


import com.view.core.model.local_service.LocalService;
import com.view.core.protocol.NDCPCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Data
@Slf4j
public class NDCClient {
    private ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    private Channel clientChannel;

    private EventLoopGroup workerGroup;

    private ChannelHandlerContext serverContext;

    private NDCClientConfiguration ndcClientConfiguration;

    private int retryTimes = 0;


    private Map<String, LocalService> ndcClientSessionMap = new ConcurrentHashMap<>();


    public void stop() {
        //停止重试
        if (ndcClientConfiguration != null) {
            ndcClientConfiguration.doBreakOperation();
        }
        if (clientChannel != null && clientChannel.isOpen()) {
            clientChannel.close();
            log.info("NDC客户端关闭");
            workerGroup.shutdownGracefully();
            ndcClientConfiguration.getStopCallback().run();
        }

    }

    /**
     * 重置客户端
     */
    public void resetClientForReconnect() {
        retryTimes = 0;
        ndcClientSessionMap = new ConcurrentHashMap<>();
        clientChannel = null;
        serverContext = null;
        if (ndcClientConfiguration != null) {
            ndcClientConfiguration.resetRetryBreak();
        }
    }

    public void start(NDCClientConfiguration ndcClientConfiguration) {
        NDCClient ndcClient = this;

        //重置客户端
        resetClientForReconnect();


        //设置配置，适配重连
        if (this.ndcClientConfiguration == null) {
            this.ndcClientConfiguration = ndcClientConfiguration;
        }

        //检查配置
        ndcClientConfiguration.check();

        //打印配置
        ndcClientConfiguration.printConfiguration();


        workerGroup = new NioEventLoopGroup();

        try {


            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

            //创建处理器
            ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    //NDC协议处理
                    pipeline.addLast(new NDCPCodec());

                    //创建ndc客户端处理器
                    NDCClientHandler ndcClientHandler = new NDCClientHandler(ndcClient, ndcClientConfiguration);

                    //NDC Package 处理
                    pipeline.addLast(ndcClientHandler);

                }
            };

            //设置处理器
            bootstrap.handler(channelInitializer);

            String host = ndcClientConfiguration.getServerHost();
            int port = ndcClientConfiguration.getServerPort();

            // Start the client.
            ChannelFuture channelFuture = bootstrap.connect(host, port);
            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    log.info("NDC客户端启动成功：{}:{}", host, port);
                    ndcClientConfiguration.getStartedCallback().run();
                } else {
                    log.error("NDC客户端启动失败：{}:{}", host, port);
                    ndcClientConfiguration.getStartFailCallback().accept(new RuntimeException("NDC客户端启动失败"));
                }
            });
            this.clientChannel = channelFuture.channel();
            // 阻塞直到客户端连接关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            ndcClientConfiguration.getStartFailCallback().accept(e);
        } finally {
            //判定是否重连
            if (ndcClientConfiguration.reconnectThisTime()) {
                //todo 再次启动

                int timeoutSecond = ndcClientConfiguration.getReconnectInterval();
                log.error("连接断开，等待{}秒，进行第{}次尝试重连", timeoutSecond, retryTimes++);
                Thread thread = Thread.currentThread();
                ndcClientConfiguration.setWaitingThread(thread);
                try {
                    TimeUnit.SECONDS.sleep(timeoutSecond);
                } catch (InterruptedException e) {
                    log.warn("等待重连被中断");
                }

                if (ndcClientConfiguration.getRetryBreak()) {
                    //todo 重试中断
                    ndcClientConfiguration.getStopCallback().run();
                } else {
                    start(ndcClientConfiguration);
                }

            } else {
                stop();
            }

        }
    }
}
