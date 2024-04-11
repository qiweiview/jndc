package com.view.core.client.tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ByteClientHandler extends SimpleChannelInboundHandler<byte[]> {
    private VirtualClient virtualClient;

    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;

        if (virtualClient == null) {
            log.error("virtualClient is null");
        } else {
            virtualClient.channelActive();
        }
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        if (virtualClient == null) {
            log.error("virtualClient is null");
        } else {
            virtualClient.channelRead0(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (virtualClient == null) {
            log.error("virtualClient is null");
        } else {
            virtualClient.channelInactive();
        }
        super.channelInactive(ctx);
    }

    public void write(byte[] bytes) {
        if (ctx != null) {
            ctx.writeAndFlush(bytes);
        } else {
            log.warn("ChannelHandlerContext is null");
        }
    }
}
