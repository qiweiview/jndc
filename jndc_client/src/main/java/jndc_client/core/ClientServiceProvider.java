package jndc_client.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import jndc.core.NDCMessageProtocol;
import jndc.core.NettyComponentConfig;
import jndc.utils.InetUtils;
import jndc.utils.UniqueInetTagProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * a local service provider
 */
public class ClientServiceProvider {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private int port;
    private String serviceIp;
    private EventLoopGroup eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();//much client use the same EventLoopGroup
    private Map<String, ClientTCPDataHandle> faceTCPMap = new ConcurrentHashMap<>();//store tcp


    public ClientServiceProvider(int port, String serviceIp) {
        this.port = port;
        this.serviceIp = serviceIp;
    }


    public void receiveMessage(NDCMessageProtocol ndcMessageProtocol) {
        InetAddress remoteInetAddress = ndcMessageProtocol.getRemoteInetAddress();
        int remotePort = ndcMessageProtocol.getRemotePort();

        String client = UniqueInetTagProducer.get4Client(remoteInetAddress, remotePort);
        ClientTCPDataHandle clientTCPDataHandle = faceTCPMap.get(client);
        if (clientTCPDataHandle == null) {

            logger.debug("start local netty client for:" + client);
            //block create
            clientTCPDataHandle = startInnerBootstrap(ndcMessageProtocol);
            faceTCPMap.put(client, clientTCPDataHandle);
        }

        //can replace with Arrays.compare in jdk 9
        if (Arrays.equals(NDCMessageProtocol.ACTIVE_MESSAGE, ndcMessageProtocol.getData())) {
            //todo ignore active message

            logger.debug("get active message");
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
            logger.debug("local app connect success " + byStringIpAddress + ":" + port);
        } catch (InterruptedException e) {
            logger.error("connect to " + inetSocketAddress + "fail cause" + e);
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
            logger.info("中断所有本地连接...");
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
            logger.error("无法获取访问者：" + uniqueTag+" 对应本地连接");
        } else {
            clientTCPDataHandle.releaseRelatedResources();
        }
    }
}
