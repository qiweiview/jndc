package com.view.core.server.ndc;

import com.view.core.component.GlobalBeanContext;
import com.view.core.protocol.NDCPCodec;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.callback.ChannelRead0CallBack;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NDCServer {

    public void start(int port) {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ChannelRead0CallBack activeCallback = (ctx, msg) -> {
                //todo active回调


            };

            ChannelRead0CallBack<NDCPacket> readCallback = (ctx, msg) -> {
                //todo read回调

                //获取消息
                NDCPacket ndcPacket = msg[0];
                GlobalBeanContext.PACKAGE_HUB.publishOnServer(ndcPacket);
            };


            ChannelRead0CallBack inActiveCallback = (ctx, msg) -> {
                //todo inActive回调
            };


            //创建ndc服务端处理器
            ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    //NDC协议处理
                    pipeline.addLast(new NDCPCodec());


                    NDCServerHandler ndcServerHandler = new NDCServerHandler(activeCallback, readCallback, inActiveCallback);

                    //NDC Packet 处理器
                    pipeline.addLast(ndcServerHandler);


                }
            };

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer);

            log.info("起动NDC服务，端口：{}", port);
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
