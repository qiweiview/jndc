package com.view.core.protocol.callback;

import io.netty.channel.ChannelHandlerContext;

public interface ChannelRead0Function<T, R> {

    public R accept(ChannelHandlerContext ctx, T... t);
}
