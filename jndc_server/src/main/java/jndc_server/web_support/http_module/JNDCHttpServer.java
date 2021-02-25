package jndc_server.web_support.http_module;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import jndc.core.NettyComponentConfig;
import jndc.core.UniqueBeanManage;
import jndc_server.core.JNDCServerConfig;
import jndc_server.core.app.ServerApp;
import jndc_server.web_support.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * jndc server core functions
 */
public class JNDCHttpServer implements ServerApp {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private EventLoopGroup eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();

    @Override
    public void start() {
        JNDCServerConfig serverConfig = UniqueBeanManage.getBean(JNDCServerConfig.class);

        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();

                String http = "http";//HttpServerCodec
                String oag = "oag";//HttpObjectAggregator


                pipeline.addLast(http, new HttpServerCodec());
                pipeline.addAfter(http, oag, new HttpObjectAggregator(2 * 1024 * 1024));//限制缓冲最大值为2mb
                JNDCRequestDecoder jndcRequestDecoder = new JNDCRequestDecoder();
                //ignore ws header
                jndcRequestDecoder.setIgnoreWsHeader(true);
                pipeline.addAfter(oag, JNDCRequestDecoder.NAME,jndcRequestDecoder );
                pipeline.addAfter(JNDCRequestDecoder.NAME, HttpRouteHandler.NAME, new HttpRouteHandler());


            }
        };

        ServerBootstrap b = new ServerBootstrap();
        b.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)//
                .localAddress(serverConfig.getHttpInetSocketAddress())//　
                .childHandler(channelInitializer);

        b.bind().addListener(x -> {
            if (x.isSuccess()) {
                logger.info("bind http  : " + serverConfig.getHttpInetSocketAddress() + " success");
            } else {
                logger.error("bind http : " + serverConfig.getHttpInetSocketAddress() + " fail,cause");
            }

        });
    }
}
