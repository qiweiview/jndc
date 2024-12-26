package com.view.core.server.ndc;

import com.view.core.protocol.NDCPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class NDCServerHandler extends SimpleChannelInboundHandler<NDCPacket> {
    public static final String SESSION_CONTEXT = "SESSION_CONTEXT";

    private NDCServerConfiguration configuration;

    private SessionContext sessionContext;

    public NDCServerHandler(NDCServerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        configuration.getConnectActiveCallback().accept(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            log.warn("连接中断：{}", cause.getMessage());
        } else {
            log.error("捕获异常：", cause);
        }

        //获取异常后关闭连接
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NDCPacket msg) throws Exception {
        log.debug("server收到消息：{}", msg);
        readCallBack.accept(ctx, msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        inactiveCallBack.accept(ctx);
    }


}
