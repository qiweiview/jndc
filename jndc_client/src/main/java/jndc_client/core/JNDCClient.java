package jndc_client.core;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import jndc.core.NDCPCodec;
import jndc.core.NettyComponentConfig;
import jndc.core.SecreteCodec;
import jndc.core.UniqueBeanManage;
import jndc.utils.ApplicationExit;
import jndc_client.gui_support.GuiStart;
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




    public JNDCClient() {
    }


    public void createClient() {
        createClient(group);
    }

    public void createClient(EventLoopGroup group) {
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

        JNDCClientConfigCenter jndcClientConfigCenter =UniqueBeanManage.getBean(JNDCClientConfigCenter.class);

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

                //set fail tag
                jndcClientConfigCenter.failToConnectToServer();

                final EventLoop eventExecutors = connect.channel().eventLoop();

                //run retry operation once on 5 second later
                eventExecutors.schedule(() -> {
                    failTimes++;

                    if (FAIL_LIMIT != -1 && failTimes > FAIL_LIMIT) {//always be false,so always retry
                        logger.error("exceeded the failure limit");
                        ApplicationExit.exit();
                    }

                    logger.info("connect fail , try re connect");
                    createClient(eventExecutors);
                }, RETRY_INTERVAL, TimeUnit.SECONDS);
            }

        });
    }


}
