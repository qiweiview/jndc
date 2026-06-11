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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端服务
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

        //can replace with Arrays.compare in jdk 9
        if (Arrays.equals(NDCMessageProtocol.ACTIVE_MESSAGE, ndcMessageProtocol.getData())) {
            log.debug("get active message");
            return;
        }

        //哈希表路由
        String client = UniqueInetTagProducer.get4Client(remoteInetAddress, remotePort);
        ClientTCPDataHandle clientTCPDataHandle = faceTCPMap.get(client);
        if (clientTCPDataHandle == null) {
            log.debug("start local netty client for:" + client);
            // 异步创建本地连接，数据在连接建立后自动发送
            startInnerBootstrap(ndcMessageProtocol, client);
            return;
        }

        // 连接已就绪，直接发送数据
        if (clientTCPDataHandle.getChannelHandlerContext() != null) {
            clientTCPDataHandle.receiveMessage(Unpooled.copiedBuffer(ndcMessageProtocol.getData()));
        } else {
            // 连接尚未建立完成，数据丢弃（极端竞态场景）
            log.warn("本地连接尚未就绪，丢弃数据: " + client);
        }
    }


    /**
     * 异步启动本地连接，连接成功后自动发送首条数据
     *
     * @param ndcMessageProtocol 首条待发送的消息
     * @param clientTag          连接标识
     */
    private void startInnerBootstrap(NDCMessageProtocol ndcMessageProtocol, String clientTag) {
        ClientTCPDataHandle clientTCPDataHandle = new ClientTCPDataHandle(ndcMessageProtocol);

        Bootstrap b = new Bootstrap();
        ChannelInitializer channelInitializer = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addFirst(ClientTCPDataHandle.NAME, clientTCPDataHandle);
            }
        };

        b.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(channelInitializer);

        InetAddress byStringIpAddress = InetUtils.getByStringIpAddress(serviceIp);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(byStringIpAddress, port);
        ChannelFuture connect = b.connect(inetSocketAddress);

        // 预注册到 map，避免重复创建
        faceTCPMap.put(clientTag, clientTCPDataHandle);

        connect.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.debug("local app connect success " + byStringIpAddress + ":" + port);
                // 连接成功，发送首条数据
                if (!Arrays.equals(NDCMessageProtocol.ACTIVE_MESSAGE, ndcMessageProtocol.getData())) {
                    clientTCPDataHandle.receiveMessage(Unpooled.copiedBuffer(ndcMessageProtocol.getData()));
                }
            } else {
                log.error("connect to " + inetSocketAddress + " fail: " + future.cause());
                // 连接失败，清理 handle
                faceTCPMap.remove(clientTag);
                clientTCPDataHandle.releaseRelatedResources();
            }
        });
    }


    /**
     * 中断所有本地连接
     */
    public void releaseAllRelatedResources() {
        synchronized (this) {
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
