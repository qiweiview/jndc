package jndc_server.core;

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
import jndc.core.data_store_support.DBWrapper;
import jndc.utils.ApplicationExit;
import jndc.utils.LogPrint;
import jndc_server.databases_object.ServerPortBind;
import jndc_server.web_support.core.FrontProjectLoader;
import jndc_server.web_support.core.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;

public class JNDCServer {
    private static final String MANAGEMENT_PROJECT = "management";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private EventLoopGroup eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();


    public JNDCServer() {
    }


    public void initBelongServerOnly() {
        ScheduledTaskCenter ipChecker = UniqueBeanManage.getBean(ScheduledTaskCenter.class);
        ipChecker.start();
    }


    public void createServer() {
        //do server init
        initBelongServerOnly();


        JNDCServerConfig serverConfig = UniqueBeanManage.getBean(JNDCServerConfig.class);

        //reset service bind state
        DBWrapper<ServerPortBind> dbWrapper = DBWrapper.getDBWrapper(ServerPortBind.class);
        dbWrapper.customExecute("update server_port_bind set portEnable = 0", null);

        //deploy the server management api
        WebServer serverTest = new WebServer();
        serverTest.start();//start


        // confirm whether to deploy default static project
        // the management project will be deploy in managementApiPort
//        if (serverConfig.isDeployFrontProject()) {
//            //load inner front file
//            String web = serverConfig.getFrontProjectPath();
//            if (!web.endsWith(File.separator)) {
//                web += File.separator;
//            }
//            FrontProjectLoader.jndcStaticProject = FrontProjectLoader.loadProject(web);
//            LogPrint.info("deploy front management project");
//        }

        String runtimeDir = serverConfig.getRuntimeDir() + File.separator + MANAGEMENT_PROJECT + File.separator;

        if (!new File(runtimeDir).exists()) {
            LogPrint.err("can not found the management project in \"" + runtimeDir + "\" please check later...");
            ApplicationExit.exit();
        }
        FrontProjectLoader.jndcStaticProject = FrontProjectLoader.loadProject(runtimeDir);
        LogPrint.debug("deploy front management project");


        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();

                pipeline.addFirst(IPFilter.NAME, IPFilter.STATIC_INSTANCE);
                pipeline.addAfter(IPFilter.NAME, NDCPCodec.NAME, new NDCPCodec());
                pipeline.addAfter(NDCPCodec.NAME, SecreteCodec.NAME, new SecreteCodec());
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
