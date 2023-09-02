package com.view.jndc.core.v2.componet.netty.handler;

import com.view.jndc.core.v2.model.jndc.JNDCData;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class WriteableHandler<T> extends SimpleChannelInboundHandler<T> {

    public abstract void write(JNDCData jndcData);
}
