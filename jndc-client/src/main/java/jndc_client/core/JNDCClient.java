package jndc_client.core;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.socket.nio.NioSocketChannel;
import jndc.core.NDCPCodec;
import jndc.core.NettyComponentConfig;
import jndc.core.SecreteCodec;
import jndc.core.UniqueBeanManage;
import jndc_client.start.ClientStart;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JNDCClient {

    private EventLoopGroup group = NettyComponentConfig.getNioEventLoopGroup();

    private JNDCClientMessageHandle jndcClientMessageHandle;

    private Thread managerThread;

    static {
        String tag = "\n" +
                "       _   _   _ _____   _____             _____ _      _____ ______ _   _ _______ \n" +
                "      | | | \\ | |  __ \\ / ____|           / ____| |    |_   _|  ____| \\ | |__   __|\n" +
                "      | | |  \\| | |  | | |       ______  | |    | |      | | | |__  |  \\| |  | |   \n" +
                "  _   | | | . ` | |  | | |      |______| | |    | |      | | |  __| | . ` |  | |   \n" +
                " | |__| | | |\\  | |__| | |____           | |____| |____ _| |_| |____| |\\  |  | |   \n" +
                "  \\____/  |_| \\_|_____/ \\_____|           \\_____|______|_____|______|_| \\_|  |_|   \n" +
                "                                                                                   \n" +
                "客户端编号: " + ClientStart.CLIENT_ID + "\n";
        log.info(tag);
    }

    /**
     * 重试
     */
    public void tryReconnect() {
        //延迟5秒
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (managerThread) {
            releaseOldResource();
            managerThread.notify();
        }
    }

    /**
     * 释放旧资源
     */
    public void releaseOldResource() {
        group.shutdownGracefully();
        group = NettyComponentConfig.getNioEventLoopGroup();
        log.info("重置工作线程...");
    }

    /**
     * 启动
     */
    public void start() {
        managerThread = new Thread(() -> {
            while (true) {
                Thread currentThread = Thread.currentThread();
                synchronized (currentThread) {
                    try {
                        createClient(group);
                        currentThread.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        //启动管理线程
        managerThread.start();

    }

    /**
     * 创建客户端
     */
    private void createClient(EventLoopGroup group) {
        log.debug("do once connect...");

        Bootstrap b = new Bootstrap();
        JNDCClient jndcClient = this;
        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addFirst(NDCPCodec.NAME, new NDCPCodec());

                pipeline.addAfter(NDCPCodec.NAME, SecreteCodec.NAME, new SecreteCodec());
                jndcClientMessageHandle = new JNDCClientMessageHandle(jndcClient);

                //set current handler
                JNDCClientConfigCenter jndcClientConfigCenter = UniqueBeanManage.getBean(JNDCClientConfigCenter.class);
                jndcClientConfigCenter.setCurrentHandler(jndcClientMessageHandle);


                pipeline.addAfter(SecreteCodec.NAME, JNDCClientMessageHandle.NAME, jndcClientMessageHandle);

            }
        };

        b.group(group)
                .channel(NioSocketChannel.class)//
                .option(ChannelOption.SO_KEEPALIVE, true)//tcp keep alive
                .handler(channelInitializer);

        JNDCClientConfigCenter jndcClientConfigCenter = UniqueBeanManage.getBean(JNDCClientConfigCenter.class);

        JNDCClientConfig clientConfig = UniqueBeanManage.getBean(JNDCClientConfig.class);

        InetSocketAddress serverIpSocketAddress = clientConfig.getServerIpSocketAddress();
        ChannelFuture connect = b.connect(serverIpSocketAddress);
        connect.addListeners(x -> {
            if (x.isSuccess()) {
                //todo connect successFully

                //set success tag
                jndcClientConfigCenter.successToConnectToServer();

                log.info("连接 jndc 服务 : " + clientConfig.getServerIpSocketAddress());
            } else {
                //todo connect fail
                log.info("连接 jndc 服务--->" + serverIpSocketAddress + "失败 , 重试");

                //set fail tag
                jndcClientConfigCenter.failToConnectToServer();

                //重试连接
                tryReconnect();
            }

        });
    }


}
