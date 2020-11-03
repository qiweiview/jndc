package jndc.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import jndc.core.NDCPCodec;
import jndc.core.NettyComponentConfig;
import jndc.core.SecreteCodec;
import jndc.core.UniqueBeanManage;
import jndc.core.config.ClientConfig;
import jndc.core.config.ServerConfig;
import jndc.core.config.UnifiedConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.concurrent.TimeUnit;


public class JNDCClient {
    private   final Logger logger = LoggerFactory.getLogger(getClass());

    private static int FAIL_LIMIT = 5;

    private static int RETRY_INTERVAL = 5;

    private int failTimes = 0;

    private EventLoopGroup group = NettyComponentConfig.getNioEventLoopGroup();

    private JNDCClientMessageHandle jndcClientMessageHandle;


    public JNDCClient() {
    }

    public void sendRegisterToServer(int localPort, int serverPort){
        if (jndcClientMessageHandle==null){
            logger.error("The client has not connected to the server ");
        }else {
            jndcClientMessageHandle.sendRegisterToServer(localPort,serverPort);
        }

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

                pipeline.addAfter(NDCPCodec.NAME, SecreteCodec.NAME,new SecreteCodec());
                jndcClientMessageHandle=new JNDCClientMessageHandle(jndcClient);
                pipeline.addAfter(SecreteCodec.NAME, JNDCClientMessageHandle.NAME,jndcClientMessageHandle );

            }
        };

        b.group(group)
                .channel(NioSocketChannel.class)//
                .handler(channelInitializer);


        UnifiedConfiguration bean = UniqueBeanManage.getBean(UnifiedConfiguration.class);
        ClientConfig clientConfig = bean.getClientConfig();

        ChannelFuture connect = b.connect(clientConfig.getInetSocketAddress());
        connect.addListeners(x -> {
            if (!x.isSuccess()) {
                final EventLoop eventExecutors = connect.channel().eventLoop();
                eventExecutors.schedule(() -> {
                    failTimes++;
                    if (failTimes > FAIL_LIMIT) {
                        logger.error("exceeded the failure limit");
                        group.shutdownGracefully();
                        return;
                    }
                    logger.info("connect fail , try re connect");
                    createClient(eventExecutors);
                }, RETRY_INTERVAL, TimeUnit.SECONDS);
            } else {
                logger.info("connect success to "+clientConfig.getInetSocketAddress());
            }

        });
    }



}
