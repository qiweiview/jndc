package jndc.core;

import io.netty.channel.nio.NioEventLoopGroup;

/**
 * 组件统一替换
 */
public class NettyComponentConfig {

    public static NioEventLoopGroup getNioEventLoopGroup(){
        return new NioEventLoopGroup();
    }
}
