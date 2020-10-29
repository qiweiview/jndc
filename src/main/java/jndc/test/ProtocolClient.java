package jndc.test;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import jndc.client.JNDCClientMessageHandle;
import jndc.core.NDCPCodec;
import jndc.core.NettyComponentConfig;
import jndc.utils.InetUtils;
import jndc.utils.LogPrint;

import java.net.InetSocketAddress;


public class ProtocolClient {

    public static void main(String[] args) {
        EventLoopGroup group = NettyComponentConfig.getNioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        ChannelInitializer channelInitializer = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addFirst(NDCPCodec.NAME, new NDCPCodec());
                pipeline.addAfter(NDCPCodec.NAME, JNDCClientMessageHandle.NAME, new JNDCClientMessageHandle());

            }
        };

        b.group(group)
                .channel(NioSocketChannel.class)//
                .handler(channelInitializer);

        InetSocketAddress localInetAddress = InetUtils.getLocalInetAddress(RemoteServer.SERVER_PORT);
        ChannelFuture connect = b.connect(localInetAddress);
        connect.addListeners(x->{
            try {
                Object object = x.get();
                LogPrint.log("connect to server success");
            }catch (Exception e){
                LogPrint.log("connect to server fail cause:"+e);
            }

        });
        connect.channel().closeFuture().addListener(x -> {
            group.shutdownGracefully().addListener(y -> {
                LogPrint.log("关闭");
            });
        });
    }


}
