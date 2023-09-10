package com.view.jndc.core.v2.componet.server;

import com.view.jndc.core.v2.componet.netty.handler.ServerTCPHandler;
import com.view.jndc.core.v2.componet.netty.handler.TCPOperationCallback;
import com.view.jndc.core.v2.model.json_object.ServiceRegister;
import com.view.jndc.core.v2.utils.UniqueIdExtractor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * port bind context
 */
@Data
@Slf4j
public class ServiceProxy {

    private String proxyId;

    private int port;

    private ServiceRegister serviceRegister;

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private Map<String, ServerTCPHandler> sourceMap = new ConcurrentHashMap<>();//store tcp

    /**
     * @param sourceId 连接者编号
     * @param data
     */
    public void write(String sourceId, byte[] data) {
        ServerTCPHandler serverTCPHandler = sourceMap.get(sourceId);
        if (serverTCPHandler == null) {
            log.error("无法找到调用者");
        } else {
            serverTCPHandler.write(data);
        }

    }

    public void start(int port) {
        this.proxyId = UniqueIdExtractor.generate();
        this.port = port;

        //定义接收数据逻辑

        TCPOperationCallback tcpOperationCallback = new TCPOperationCallback() {
            @Override
            public void active(String sourceId) {
                if (serviceRegister == null) {
                    log.error("未绑定任何服务");
                } else {
                    serviceRegister.handleActive(proxyId, sourceId);
                }
            }

            @Override
            public void dataRead(String sourceId, byte[] data) {
                if (serviceRegister == null) {
                    log.error("未绑定任何服务");
                } else {
                    serviceRegister.handleRequest(proxyId, sourceId, data);
                }
            }

            @Override
            public void inActive(String sourceId) {
                if (serviceRegister == null) {
                    log.error("未绑定任何服务");
                } else {
                    serviceRegister.handleInActive(proxyId, sourceId);
                }
            }
        };


        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                ServerTCPHandler serverTCPHandler = new ServerTCPHandler(tcpOperationCallback);
                pipeline.addFirst(ServerTCPHandler.NAME, serverTCPHandler);
            }
        };


        ServerBootstrap b = new ServerBootstrap();
        b.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(channelInitializer);

        b.bind(getPort()).addListener(x -> {
            if (x.isSuccess()) {
                log.info("监听端口" + getPort() + "成功");
            } else {

                log.error("监听端口" + getPort() + "失败,原因：" + x.cause().getMessage());
            }

        });
    }


    public void proxyTo(ServiceRegister serviceRegister) {
        this.serviceRegister = serviceRegister;

    }


    /**
     * 解除绑定
     */
    public void unbind() {
        serviceRegister = null;
    }
}
