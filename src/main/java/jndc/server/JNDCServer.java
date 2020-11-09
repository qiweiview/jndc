package jndc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jndc.core.*;
import jndc.core.config.ServerConfig;
import jndc.core.config.UnifiedConfiguration;
import jndc.utils.LogPrint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.core.WebServer;

import java.net.InetSocketAddress;

public class JNDCServer {
    private   final Logger logger = LoggerFactory.getLogger(getClass());
    private EventLoopGroup eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();


    public JNDCServer() {
    }



    public void createServer() {

        UnifiedConfiguration bean = UniqueBeanManage.getBean(UnifiedConfiguration.class);
        ServerConfig serverConfig = bean.getServerConfig();


        if (serverConfig.isManageCenterEnable()){
            //openManageCenter
            WebServer serverTest =new WebServer();
            serverTest.start();//start
        }


        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();

                pipeline.addFirst(IPFilter.NAME,IPFilter.STATIC_INSTANCE);
                pipeline.addAfter(IPFilter.NAME,NDCPCodec.NAME, new NDCPCodec());
                pipeline.addAfter(NDCPCodec.NAME,SecreteCodec.NAME,new SecreteCodec());
                pipeline.addAfter(SecreteCodec.NAME, JNDCServerMessageHandle.NAME, new JNDCServerMessageHandle());
            }
        };

        ServerBootstrap b = new ServerBootstrap();
        b.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)//
                .localAddress(serverConfig.getInetSocketAddress())//　
                .childHandler(channelInitializer);

        b.bind().addListener(x -> {
            if (x.isSuccess()) {
                logger.info("bind admin : " + serverConfig.getInetSocketAddress() + " success");
            } else {
                logger.error("bind admin : " + serverConfig.getInetSocketAddress() + " fail");
            }

        });



    }


}
