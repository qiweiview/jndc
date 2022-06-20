package jndc.web_support.http_module;

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
import jndc_server.config.JNDCServerConfig;
import jndc_server.config.ServerRuntimeConfig;
import jndc_server.core.app.ServerApp;
import jndc_server.web_support.core.CustomSslHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * jndc server core functions
 */
@Slf4j
public class JNDCHttpServer implements ServerApp {
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

                if (serverConfig.getWebConfig().isUseSsl()) {
                    SSLContext serverSSLContext = serverConfig.getWebConfig().getServerSSLContext();
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
                .localAddress(serverConfig.getHttpInetSocketAddress())//　
                .childHandler(channelInitializer);

        b.bind().addListener(x -> {
            String protocol = "http";
            if (serverConfig.getWebConfig().isUseSsl()) {
                protocol = "https";
            }
            if (x.isSuccess()) {
                log.info("启动web服务 " + protocol + "://" + serverConfig.getHttpInetSocketAddress() + " 成功");
            } else {
                log.error("启动web服务 " + protocol + "://" + serverConfig.getHttpInetSocketAddress() + " 失败");
            }

        });

        //the page for "route not found"
        loadRouteNotFoundPage();

    }

    public void loadRouteNotFoundPage() {
        JNDCServerConfig serverConfig = UniqueBeanManage.getBean(JNDCServerConfig.class);
        File file = new File(serverConfig.getWebConfig().getNotFoundPage());
        if (file.exists()) {
            try {
                String s = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                ServerRuntimeConfig.ROUTE_NOT_FOUND_CONTENT = s;
                log.info("使用外部配置404页面：" + file.getName());
            } catch (IOException e) {
                log.error("加载404页面失败 ,cause:" + e);
            }
        } else {
            log.info("没有找到配置的404页面,使用默认页面");
        }

    }
}
