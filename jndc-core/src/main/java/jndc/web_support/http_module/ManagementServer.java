package jndc.web_support.http_module;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import jndc.core.NDCApp;
import jndc.core.NettyComponentConfig;
import jndc.utils.ApplicationExit;
import jndc.utils.InetUtils;
import jndc.utils.LogPrint;
import jndc.web_support.config.ServeManageConfig;
import jndc.web_support.core.*;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * 管理端服务器
 */
@Slf4j
public class ManagementServer implements NDCApp<ServeManageConfig> {
    private EventLoopGroup eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();


    @Override
    public void start(ServeManageConfig serverConfig) {


        //部署端口
        int manageCenterPort = serverConfig.getManagementApiPort();
        InetAddress localInetAddress = InetUtils.localInetAddress;
        InetSocketAddress inetSocketAddress = new InetSocketAddress(localInetAddress, manageCenterPort);


        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel channel) throws Exception {

                ChannelPipeline pipeline = channel.pipeline();

                String http = "http";//HttpServerCodec
                String oag = "oag";//HttpObjectAggregator
                String ws = "ws";//WebSocketServerProtocolHandler


                if (serverConfig.isUseSsl()) {
                    SSLContext serverSSLContext = serverConfig.getServerSSLContext();
                    SSLEngine sslEngine = serverSSLContext.createSSLEngine();
                    sslEngine.setUseClientMode(false);//设置为服务器模式
                    pipeline.addFirst(CustomSslHandler.NAME, new CustomSslHandler(sslEngine));
                }


                pipeline.addLast(http, new HttpServerCodec());

                //http缓冲器
                pipeline.addAfter(http, oag, new HttpObjectAggregator(2 * 1024 * 1024));//限制缓冲最大值为2mb

                //鉴权规则
                pipeline.addAfter(oag, AuthTokenChecker.NAME, new AuthTokenChecker());

                //http请求解码器
                pipeline.addAfter(AuthTokenChecker.NAME, JNDCRequestDecoder.NAME, new JNDCRequestDecoder());

                //http业务内容处理器
                pipeline.addAfter(JNDCRequestDecoder.NAME, WebContentHandler.NAME, new WebContentHandler());

                //websocket报文处理器
                pipeline.addAfter(WebContentHandler.NAME, ws, new WebSocketServerProtocolHandler("/ws"));

                //websocket通道注册处理器
                pipeline.addAfter(ws, WebSocketHandle.NAME, new WebSocketHandle());

            }
        };

        ServerBootstrap b = new ServerBootstrap();
        b.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)//
                .localAddress(inetSocketAddress)//　
                .childHandler(channelInitializer);

        b.bind().addListener(x -> {
            if (x.isSuccess()) {
                log.info("管理端服务 http://" + inetSocketAddress + " 成功");
            } else {
                log.error("管理端服务 http://" + inetSocketAddress + " 失败");
            }

        });

        //判断是否扫扫描前端项目目录
        if (serverConfig.isAdminEnable()) {
            //todo 启动管理页面
            scanFrontProject(serverConfig.getAdminProjectPath());
        } else {
            log.info("忽略静态页面部署");
        }


    }

    /**
     * 扫描管理页文件
     */
    public void scanFrontProject(String runtimeDir) {


        log.info("扫描静态资源页面--->" + runtimeDir);


        if (!new File(runtimeDir).exists()) {
            LogPrint.err("静态资源页面不存在--->" + runtimeDir);
            ApplicationExit.exit();
        }

        FrontProjectLoader.jndcStaticProject = FrontProjectLoader.loadProject(runtimeDir);

    }

}
