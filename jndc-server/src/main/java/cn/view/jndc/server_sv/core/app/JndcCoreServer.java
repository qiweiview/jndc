package cn.view.jndc.server_sv.core.app;

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
import jndc_server.core.filter.CustomRulesFilter;
import lombok.extern.slf4j.Slf4j;

/**
 * jndc server core functions
 */
@Slf4j
public class JndcCoreServer implements ServerApp{
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
                log.info("核心服务: jndc://" + serverConfig.getInetSocketAddress() + " 启动成功");
            } else {
                log.error("核心服务: jndc://" + serverConfig.getInetSocketAddress() + " 启动失败");
            }

        });
    }
}
