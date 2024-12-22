package com.view.core.protocol.callback;

import io.netty.channel.ChannelHandlerContext;

public interface ChannelRead0Consumer<T> {

    public void accept(ChannelHandlerContext ctx, T... t);
}
