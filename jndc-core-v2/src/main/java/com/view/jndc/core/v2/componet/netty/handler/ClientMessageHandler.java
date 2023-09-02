package com.view.jndc.core.v2.componet.netty.handler;

import com.view.jndc.core.v2.model.jndc.JNDCData;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientMessageHandler extends WriteableHandler<JNDCData> {
    public static final String NAME = "ClientMessageHandler";

    private volatile ChannelHandlerContext context;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
        synchronized (this) {
            notifyAll();
        }
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, JNDCData jndcData) throws Exception {
        byte type = jndcData.getType();

        log.info("客户端接收到消息");

    }

    @Override
    public void write(JNDCData jndcData) {
        if (context == null) {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    log.error("等待异常", e);
                }
            }
        }
        context.writeAndFlush(jndcData);
    }
}
