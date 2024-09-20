package com.view.core.protocol.callback;

import io.netty.channel.ChannelHandlerContext;

public interface ChannelRead0CallBack<T> {

    public void accept(ChannelHandlerContext ctx, T... t);
}
