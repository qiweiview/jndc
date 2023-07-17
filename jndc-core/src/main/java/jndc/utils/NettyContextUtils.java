package jndc.utils;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;


public class NettyContextUtils {

    /**
     * 获取隧道指纹
     *
     * @param channelHandlerContext
     * @return
     */
    public static String getFingerprintFromContext(ChannelHandlerContext channelHandlerContext) {
        InetSocketAddress socketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
        //todo ip+端口
        return socketAddress.getHostString() + socketAddress.getPort();
    }
}
