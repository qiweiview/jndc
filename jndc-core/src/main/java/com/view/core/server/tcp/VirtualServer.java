package com.view.core.server.tcp;

import lombok.Data;

import java.util.function.Consumer;

/**
 * 对于远程客户端，来自本地的虚拟服务
 */
@Data
public abstract class VirtualServer {
    private Consumer<byte[]> dataConsumer;

    private String clientId;


    /**
     * 接收到消息
     *
     * @param msg
     */
    public abstract void channelRead0(byte[] msg);

    /**
     * 客户端断开连接
     */
    public abstract void channelInactive();

    /**
     * 客户端连接
     */
    public abstract void channelActive();
}
