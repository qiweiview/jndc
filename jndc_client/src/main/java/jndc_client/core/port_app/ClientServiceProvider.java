package jndc_client.core.port_app;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import jndc.core.NDCMessageProtocol;
import jndc.core.NettyComponentConfig;
import jndc.utils.InetUtils;
import jndc.utils.UniqueInetTagProducer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * a local service provider
 */
@Data
@Slf4j
public class ClientServiceProvider implements Serializable {

    private final String pId = UUID.randomUUID().toString();
    private int port;
    private String serviceIp;
    private EventLoopGroup eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();//much client use the same EventLoopGroup
    private Map<String, ClientTCPDataHandle> faceTCPMap = new ConcurrentHashMap<>();//store tcp


    public ClientServiceProvider(int port, String serviceIp) {
        this.port = port;
        this.serviceIp = serviceIp;
    }


    /**
     * 客户端端口管理器接收消息
     *
     * @param ndcMessageProtocol
     */
    public void receiveMessage(NDCMessageProtocol ndcMessageProtocol) {
        InetAddress remoteInetAddress = ndcMessageProtocol.getRemoteAddress();
        int remotePort = ndcMessageProtocol.getRemotePort();

        //哈希表路由
        String client = UniqueInetTagProducer.get4Client(remoteInetAddress, remotePort);
        ClientTCPDataHandle clientTCPDataHandle = faceTCPMap.get(client);
        if (clientTCPDataHandle == null) {

            log.debug("start local netty client for:" + client);
            //block create
            clientTCPDataHandle = startInnerBootstrap(ndcMessageProtocol);
            faceTCPMap.put(client, clientTCPDataHandle);
        }

        //can replace with Arrays.compare in jdk 9
        if (Arrays.equals(NDCMessageProtocol.ACTIVE_MESSAGE, ndcMessageProtocol.getData())) {
            //todo ignore active message

            log.debug("get active message");
            return;
        }

        clientTCPDataHandle.receiveMessage(Unpooled.copiedBuffer(ndcMessageProtocol.getData()));

    }


    /**
     * start a netty client to localApp
     *
     * @return
     */
    private ClientTCPDataHandle startInnerBootstrap(NDCMessageProtocol ndcMessageProtocol) {
        ClientTCPDataHandle clientTCPDataHandle = new ClientTCPDataHandle(ndcMessageProtocol);

        Bootstrap b = new Bootstrap();
        ChannelInitializer channelInitializer = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addFirst(ClientTCPDataHandle.NAME, clientTCPDataHandle);
            }
        };

        b.group(eventLoopGroup)//much client use the same EventLoopGroup
                .channel(NioSocketChannel.class)//
                .handler(channelInitializer);

        InetAddress byStringIpAddress = InetUtils.getByStringIpAddress(serviceIp);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(byStringIpAddress, port);
        ChannelFuture connect = b.connect(inetSocketAddress);

        try {
            connect.sync();
            log.debug("local app connect success " + byStringIpAddress + ":" + port);
        } catch (InterruptedException e) {
            log.error("connect to " + inetSocketAddress + "fail cause" + e);
        }

        return clientTCPDataHandle;
    }


    /**
     * 中断所有本地连接
     */
    public void releaseAllRelatedResources() {
        synchronized (ClientServiceProvider.class) {
            faceTCPMap.forEach((k, v) -> {
                v.releaseRelatedResources();
            });
            faceTCPMap = new ConcurrentHashMap<>();
            log.info("中断所有本地连接...");
        }
    }


    /**
     * 释放提供给访问者的本地连接
     *
     * @param uniqueTag
     */
    public void releaseRelatedResources(String uniqueTag) {
        ClientTCPDataHandle clientTCPDataHandle = faceTCPMap.remove(uniqueTag);
        if (clientTCPDataHandle == null) {
            log.error("无法获取访问者：" + uniqueTag + " 对应本地连接");
        } else {
            clientTCPDataHandle.releaseRelatedResources();
        }
    }
}
