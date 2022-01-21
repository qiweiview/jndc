package jndc.utils;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;


public class NettyContextUtils {

    public static String getFingerprintFromContext(ChannelHandlerContext channelHandlerContext) {
        InetSocketAddress socketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
        return socketAddress.getHostString() + socketAddress.getPort();
    }
}
