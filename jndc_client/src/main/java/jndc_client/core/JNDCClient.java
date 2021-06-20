package jndc_client.core;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import jndc.core.NDCPCodec;
import jndc.core.NettyComponentConfig;
import jndc.core.SecreteCodec;
import jndc.core.UniqueBeanManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


public class JNDCClient {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static int FAIL_LIMIT = -1;

    private static int RETRY_INTERVAL = 5;

    private int failTimes = 0;

    private EventLoopGroup group = NettyComponentConfig.getNioEventLoopGroup();

    private JNDCClientMessageHandle jndcClientMessageHandle;

    private Thread managerThread;


    public void tryReconnect() {
        //延迟5秒
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (managerThread) {
            managerThread.notify();
        }
    }

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

    private void createClient(EventLoopGroup group) {
        logger.info("do once connect...");

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

        ChannelFuture connect = b.connect(clientConfig.getServerIpSocketAddress());
        connect.addListeners(x -> {
            if (x.isSuccess()) {
                //todo connect successFully

                //set success tag
                jndcClientConfigCenter.successToConnectToServer();

                logger.info("connect success to the jndc server : " + clientConfig.getServerIpSocketAddress());
            } else {
                //todo connect fail
                logger.info("connect fail , try re connect");

                //set fail tag
                jndcClientConfigCenter.failToConnectToServer();

                //重试连接
                tryReconnect();

//                //run retry operation once on 5 second later
//                eventExecutors.schedule(() -> {
//                    failTimes++;
//
//                    if (FAIL_LIMIT != -1 && failTimes > FAIL_LIMIT) {//always be false,so always retry
//                        logger.error("exceeded the failure limit");
//                        ApplicationExit.exit();
//                    }
//
//                    logger.info("connect fail , try re connect");
//                    createClient(eventExecutors);
//                }, RETRY_INTERVAL, TimeUnit.SECONDS);


            }

        });
    }


}
