package com.view.core.client.ndc;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

import java.net.InetSocketAddress;

@Data
public class NDCClientInfo {
    private String ndcClientId;

    private String ip;

    private int port;

    private long connectTime;

    private ChannelHandlerContext channelHandlerContext;

    public void parseIpPort() {
        if (channelHandlerContext != null) {
            InetSocketAddress socketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
            ip = socketAddress.getAddress().getHostAddress();
            port = socketAddress.getPort();
        }
    }

    public String formatDescription() {
        return ndcClientId + " " + ip + ":" + port + " " + connectTime;
    }

}
