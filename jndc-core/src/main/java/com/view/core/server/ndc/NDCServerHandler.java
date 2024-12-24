package com.view.core.server.ndc;

import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.callback.ChannelRead0Consumer;
import com.view.core.protocol.callback.ChannelRead0Function;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class NDCServerHandler extends SimpleChannelInboundHandler<NDCPacket> {
    public static final String SESSION_CONTEXT = "SESSION_CONTEXT";

    private SessionContext sessionContext;

    private ChannelRead0Function<NDCPacket, SessionContext> activeCallBack;

    private ChannelRead0Consumer<NDCPacket> readCallBack;

    private ChannelRead0Consumer<NDCPacket> inactiveCallBack;

    public NDCServerHandler(ChannelRead0Function<NDCPacket, SessionContext> activeCallBack,
                            ChannelRead0Consumer<NDCPacket> readCallBack,
                            ChannelRead0Consumer<NDCPacket> inactiveCallBack) {
        this.activeCallBack = activeCallBack;
        this.readCallBack = readCallBack;
        this.inactiveCallBack = inactiveCallBack;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("连接成功：{}", ctx.channel().remoteAddress());

        //创建会话上下文
        sessionContext = activeCallBack.accept(ctx);
        ctx.channel().attr(AttributeKey.valueOf(SESSION_CONTEXT)).set(sessionContext);
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
