package com.view.core.server.ndc;

import com.view.core.protocol.NDCPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NDCServerHandler extends SimpleChannelInboundHandler<NDCPacket> {

    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("有客户端连接：{}", ctx.channel().remoteAddress());
        this.ctx = ctx;

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NDCPacket msg) throws Exception {
        log.info("server收到消息：{}", msg);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("捕获异常：", cause);
        ctx.close();
    }


}
