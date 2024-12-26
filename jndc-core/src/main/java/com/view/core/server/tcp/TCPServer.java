package com.view.core.server.tcp;


import com.view.core.component.SupportEnvironment;
import com.view.core.model.TCPDataTransport;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Slf4j
public class TCPServer {

    private Map<String, ChannelHandlerContext> sessionMap = new ConcurrentHashMap<>();

    private TCPServerConfiguration tcpServerConfiguration;

    private SupportEnvironment supportEnvironment;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private String description;


    /**
     * 启动服务
     */
    public void start(TCPServerConfiguration tcpServerConfiguration) {
        this.tcpServerConfiguration = tcpServerConfiguration;

        TCPServer tcpServer = this;
        int port = tcpServerConfiguration.getPort();


        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new ByteArrayDecoder());
                        pipeline.addLast(new ByteArrayEncoder());
                        pipeline.addLast(new ByteServerHandler(tcpServer, tcpServerConfiguration));
                    }
                });

        try {
            serverBootstrap.bind(port).sync().addListener(future -> {
                if (future.isSuccess()) {
                    log.info("TCP服务启动成功，端口：{}", port);
                    tcpServerConfiguration.getStartSuccessCallBack().accept(tcpServer);
                } else {
                    log.error("TCP服务启动失败，端口：{}", port);
                    tcpServerConfiguration.getStartFailCallBack().accept(tcpServer);
                }
            }).channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("TCP服务启动失败", e);
        }
    }

    /**
     * 停止服务
     */
    @Override
    public void stop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }

        tcpServerConfiguration.getStopCallBack().accept(this);
    }


    /**
     * 接收数据
     *
     * @param tcpDataTransport
     */
    public void receiveData(TCPDataTransport tcpDataTransport) {
        Map<String, ByteServerHandler> sessionMap = getSessionMap();
        String appServerSessionId = tcpDataTransport.getAppServerSessionId();
        ByteServerHandler byteServerHandler = sessionMap.get(appServerSessionId);
        if (byteServerHandler != null) {
            ChannelHandlerContext channelHandlerContext = byteServerHandler.getChannelHandlerContext();
            byte[] data = tcpDataTransport.getData();
            if (data != null) {
                channelHandlerContext.writeAndFlush(data);
            }
        }
    }

    /**
     * 通知客服务端已经就绪
     *
     * @param tcpDataTransport
     */
    public void noticeActiveCompleted(TCPDataTransport tcpDataTransport) {
        Map<String, ByteServerHandler> sessionMap = getSessionMap();
        String appServerSessionId = tcpDataTransport.getAppServerSessionId();
        ByteServerHandler byteServerHandler = sessionMap.get(appServerSessionId);
        if (byteServerHandler != null) {
            byteServerHandler.noticeActiveCompleted();
        }
    }

    /**
     * 取消注册
     *
     * @param appServerSessionId
     */
    public void unRegisterSession(String appServerSessionId) {
        Map<String, ByteServerHandler> sessionMap = getSessionMap();
        sessionMap.remove(appServerSessionId);
    }


}
