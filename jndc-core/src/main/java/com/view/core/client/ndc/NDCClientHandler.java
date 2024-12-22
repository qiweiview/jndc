package com.view.core.client.ndc;

import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.callback.ChannelRead0Consumer;
import com.view.core.protocol.callback.ChannelRead0Function;
import com.view.core.server.ndc.SessionContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NDCClientHandler extends SimpleChannelInboundHandler<NDCPacket> {
    private ChannelRead0Function<NDCPacket, SessionContext> active;
    private ChannelRead0Consumer<NDCPacket> read;
    private ChannelRead0Consumer<NDCPacket> inactive;

    public NDCClientHandler(ChannelRead0Function<NDCPacket, SessionContext> active,
                            ChannelRead0Consumer<NDCPacket> read,
                            ChannelRead0Consumer<NDCPacket> inactive) {
        this.active = active;
        this.read = read;
        this.inactive = inactive;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("连接成功：{}", ctx.channel().remoteAddress());
        active.accept(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("捕获异常：", cause);
        //获取异常后关闭连接
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NDCPacket msg) throws Exception {
        log.debug("client收到消息：{}", msg);
        read.accept(ctx, msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        inactive.accept(ctx);
    }


}
