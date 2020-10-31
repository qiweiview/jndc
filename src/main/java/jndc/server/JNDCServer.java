package jndc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jndc.core.NDCPCodec;
import jndc.core.NettyComponentConfig;
import jndc.utils.LogPrint;

import java.net.InetSocketAddress;

public class JNDCServer {
    private int serverPort;
    private EventLoopGroup eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();


    public JNDCServer(int serverPort) {
        this.serverPort = serverPort;
    }



    public void createServer() {

        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addFirst(NDCPCodec.NAME, new NDCPCodec());
                pipeline.addAfter(NDCPCodec.NAME, JNDCServerMessageHandle.NAME, new JNDCServerMessageHandle());
            }
        };

        ServerBootstrap b = new ServerBootstrap();
        b.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)//
                .localAddress(new InetSocketAddress(serverPort))//　
                .childHandler(channelInitializer);

        b.bind().addListener(x -> {
            if (x.isSuccess()) {
                LogPrint.log("bind admin port:" + serverPort + "success");
            } else {
                LogPrint.log("bind admin port:" + serverPort + "fail");
            }

        });

    }


}
