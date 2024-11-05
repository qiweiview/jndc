package com.view.core.client.ndc;

import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.callback.ChannelRead0CallBack;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NDCClientHandler extends SimpleChannelInboundHandler<NDCPacket> {
    private ChannelRead0CallBack active;
    private ChannelRead0CallBack<NDCPacket> read;
    private ChannelRead0CallBack inactive;

    public NDCClientHandler(ChannelRead0CallBack active, ChannelRead0CallBack read, ChannelRead0CallBack inactive) {
        this.active = active;
        this.read = read;
        this.inactive = inactive;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接成功：{}", ctx.channel().remoteAddress());
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
        log.debug("连接关闭：{}", ctx.channel().remoteAddress());
        inactive.accept(ctx);
    }
}
