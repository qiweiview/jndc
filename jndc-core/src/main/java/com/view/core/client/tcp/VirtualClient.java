package com.view.core.client.tcp;

import lombok.Data;

/**
 * 对于本地服务，来自远程的虚拟客户端
 */
@Data
public abstract class VirtualClient {


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
