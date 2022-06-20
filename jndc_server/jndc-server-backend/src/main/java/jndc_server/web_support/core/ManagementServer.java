package jndc_server.web_support.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import jndc.core.NettyComponentConfig;
import jndc.core.UniqueBeanManage;
import jndc.utils.ApplicationExit;
import jndc.utils.InetUtils;
import jndc.utils.LogPrint;
import jndc.utils.PathUtils;
import jndc.web_support.core.*;
import jndc_server.config.JNDCServerConfig;
import jndc_server.config.ServeManageConfig;
import jndc_server.core.app.ServerApp;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * web management server
 */
@Slf4j
public class ManagementServer implements ServerApp {
    private EventLoopGroup eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();


    @Override
    public void start() {
        JNDCServerConfig serverConfig = UniqueBeanManage.getBean(JNDCServerConfig.class);
        int manageCenterPort = serverConfig.getManageConfig().getManagementApiPort();
        InetAddress localInetAddress = InetUtils.localInetAddress;
        InetSocketAddress inetSocketAddress = new InetSocketAddress(localInetAddress, manageCenterPort);


        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel channel) throws Exception {

                ChannelPipeline pipeline = channel.pipeline();

                String http = "http";//HttpServerCodec
                String oag = "oag";//HttpObjectAggregator
                String ws = "ws";//WebSocketServerProtocolHandler


                ServeManageConfig manageConfig = serverConfig.getManageConfig();
                if (manageConfig.isUseSsl()) {
                    SSLContext serverSSLContext = manageConfig.getServerSSLContext();
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
        if (serverConfig.getManageConfig().isAdminEnable()) {
            //todo 启动管理页面
            scanFrontProject();
        } else {
            log.info("忽略静态页面部署");
        }


    }

    /**
     * 扫描管理页文件
     */
    public void scanFrontProject() {


        String runTimePath = PathUtils.getRunTimePath();

        String runtimeDir = runTimePath + File.separator + ".." + File.separator + "compare_dist";
//        String runtimeDir ="D:\\NewWorkSpace\\Tools\\jndc\\jndc_server\\target\\jndc_server\\compare_dist";
        log.info("扫描管理页--->" + runtimeDir);


        if (!new File(runtimeDir).exists()) {
            LogPrint.err("管理页目录不存在--->" + runtimeDir);
            ApplicationExit.exit();
        }

        FrontProjectLoader.jndcStaticProject = FrontProjectLoader.loadProject(runtimeDir);

    }

}
