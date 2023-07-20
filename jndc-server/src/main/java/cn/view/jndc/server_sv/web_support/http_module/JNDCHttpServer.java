package cn.view.jndc.server_sv.web_support.http_module;

import cn.view.jndc.server_sv.core.app.ServerApp;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import jndc.core.NettyComponentConfig;
import jndc.web_support.core.CustomSslHandler;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;


/**
 * jndc server core functions
 */
@Slf4j
public class JNDCHttpServer implements ServerApp {
    private EventLoopGroup eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();

    @Override
    public void start() {


        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();

                String http = "http";//HttpServerCodec
                String oag = "oag";//HttpObjectAggregator

                //todo fix
                if (false) {
                    SSLContext serverSSLContext = null;
                    SSLEngine sslEngine = serverSSLContext.createSSLEngine();
                    sslEngine.setUseClientMode(false);//设置为服务器模式
                    pipeline.addFirst(CustomSslHandler.NAME, new CustomSslHandler(sslEngine));
                }


                pipeline.addLast(http, new HttpServerCodec());
                pipeline.addAfter(http, oag, new HttpObjectAggregator(2 * 1024 * 1024));//限制缓冲最大值为2mb
                pipeline.addAfter(oag, HostRouteHandle.NAME, new HostRouteHandle());
            }
        };

        ServerBootstrap b = new ServerBootstrap();
        b.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)//
//                .localAddress(serverConfig.getHttpInetSocketAddress())//　
                .childHandler(channelInitializer);

        b.bind().addListener(x -> {
            String protocol = "http";
            if (false) {
                protocol = "https";
            }
            if (x.isSuccess()) {
                log.info(protocol + "启动web服务成功");
            } else {
                log.error(protocol + "启动web服务失败");
            }

        });

        //the page for "route not found"
        loadRouteNotFoundPage();

    }

    /**
     * 加载404页面
     */
    public void loadRouteNotFoundPage() {
        if (false) {
            ServerRuntimeConfig.ROUTE_NOT_FOUND_CONTENT = "404 page";
            log.info("使用外部配置404页面：");
        } else {
            log.info("没有找到配置的404页面,使用默认页面");
        }

    }
}
