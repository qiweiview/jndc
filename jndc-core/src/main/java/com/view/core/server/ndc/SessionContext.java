package com.view.core.server.ndc;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

import java.net.InetSocketAddress;

@Data
public class SessionContext {
    private Long acceptHistoryId;

    private String host;

    private int port;

    public static SessionContext of(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        SessionContext sessionContext = new SessionContext();
        sessionContext.setHost(socketAddress.getHostString());
        sessionContext.setPort(socketAddress.getPort());
        return sessionContext;
    }


}
