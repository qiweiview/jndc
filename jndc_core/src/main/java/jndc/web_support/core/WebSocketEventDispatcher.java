package jndc.web_support.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public interface WebSocketEventDispatcher {

    void afterHandshake(Channel channel);

    void handleTextFrame(ChannelHandlerContext channelHandlerContext, String text);

    void handleChannelInactive(ChannelHandlerContext channelHandlerContext);
}
