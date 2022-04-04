package jndc_server.core.app;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jndc.core.NDCPCodec;
import jndc.core.NettyComponentConfig;
import jndc.core.SecreteCodec;
import jndc.core.UniqueBeanManage;
import jndc_server.config.JNDCServerConfig;
import jndc_server.core.JNDCServerMessageHandle;
import jndc_server.core.filter.CustomRulesFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * jndc server core functions
 */
public class JndcCoreServer implements ServerApp{
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private EventLoopGroup eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();

    @Override
    public void start() {
        JNDCServerConfig serverConfig = UniqueBeanManage.getBean(JNDCServerConfig.class);

        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();


                pipeline.addFirst(NDCPCodec.NAME, new NDCPCodec());
                pipeline.addAfter(NDCPCodec.NAME, SecreteCodec.NAME, new SecreteCodec());
                pipeline.addAfter(SecreteCodec.NAME, CustomRulesFilter.NAME, CustomRulesFilter.STATIC_INSTANCE);
                pipeline.addAfter(CustomRulesFilter.NAME, JNDCServerMessageHandle.NAME, new JNDCServerMessageHandle());
            }
        };

        ServerBootstrap b = new ServerBootstrap();
        b.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)//
                .localAddress(serverConfig.getInetSocketAddress())//　
                .childHandler(channelInitializer);

        b.bind().addListener(x -> {
            if (x.isSuccess()) {
                logger.info("配置服务: " + serverConfig.getInetSocketAddress() + " 启动成功");
            } else {
                logger.error("配置服务: " + serverConfig.getInetSocketAddress() + " 启动失败");
            }

        });
    }
}
