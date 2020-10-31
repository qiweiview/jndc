package jndc.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import jndc.core.NDCPCodec;
import jndc.core.NettyComponentConfig;
import jndc.test.ServerTest;
import jndc.utils.InetUtils;
import jndc.utils.LogPrint;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;


public class JNDCClient {

    private String name;

    private static int FAIL_LIMIT = 5;

    private static int RETRY_INTERVAL = 5;

    private int failTimes = 0;

    private EventLoopGroup group = NettyComponentConfig.getNioEventLoopGroup();


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
                pipeline.addAfter(NDCPCodec.NAME, JNDCClientMessageHandle.NAME, new JNDCClientMessageHandle(jndcClient));

            }
        };

        b.group(group)
                .channel(NioSocketChannel.class)//
                .handler(channelInitializer);

        InetSocketAddress localInetAddress = InetUtils.getLocalInetAddress(ServerTest.SERVER_PORT);
        ChannelFuture connect = b.connect(localInetAddress);
        connect.addListeners(x -> {
            if (!x.isSuccess()) {
                final EventLoop eventExecutors = connect.channel().eventLoop();
                eventExecutors.schedule(() -> {
                    failTimes++;
                    if (failTimes > FAIL_LIMIT) {
                        LogPrint.log("exceeded the failure limit");
                        group.shutdownGracefully();
                        return;
                    }
                    LogPrint.log("connect fail , try re connect");
                    createClient(eventExecutors);
                }, RETRY_INTERVAL, TimeUnit.SECONDS);
            } else {
                LogPrint.log("connect success");
            }

        });
    }



}
