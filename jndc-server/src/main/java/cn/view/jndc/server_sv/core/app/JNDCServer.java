package cn.view.jndc.server_sv.core.app;

import cn.view.jndc.server_sv.core.filter.CustomRulesFilter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jndc.core.NDCPCodec;
import jndc.core.NettyComponentConfig;
import jndc.core.SecreteCodec;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * jndc server core functions
 */
@Slf4j
public class JNDCServer implements ServerApp {
    private EventLoopGroup eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();

    private CustomRulesFilter customRulesFilter;

    @Override
    public void start() {

        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();


                pipeline.addFirst(NDCPCodec.NAME, new NDCPCodec());
                pipeline.addAfter(NDCPCodec.NAME, SecreteCodec.NAME, new SecreteCodec());
                pipeline.addAfter(SecreteCodec.NAME, CustomRulesFilter.NAME, customRulesFilter);
                pipeline.addAfter(CustomRulesFilter.NAME, JNDCServerMessageHandle.NAME, new JNDCServerMessageHandle());
            }
        };

        InetSocketAddress unresolved = InetSocketAddress.createUnresolved("0.0.0.0", 777);

        ServerBootstrap b = new ServerBootstrap();
        b.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)//
                .localAddress(unresolved)//　
                .childHandler(channelInitializer);

        b.bind().addListener(x -> {
            if (x.isSuccess()) {
                log.info("核心服务: jndc://启动成功");
            } else {
                log.error("核心服务: jndc://启动失败");
            }

        });
    }
}
