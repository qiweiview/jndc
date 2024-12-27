package com.view.core.client.ndc;

import com.view.core.protocol.NDCPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NDCClientHandler extends SimpleChannelInboundHandler<NDCPacket> {
    private NDCClientConfiguration ndcClientConfiguration;

    public NDCClientHandler(NDCClientConfiguration ndcClientConfiguration) {
        this.ndcClientConfiguration = ndcClientConfiguration;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("连接成功：{}", ctx.channel().remoteAddress());
        ndcClientConfiguration.getConnectActiveCallback().accept(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("捕获异常：", cause);
        //获取异常后关闭连接
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NDCPacket msg) throws Exception {
        ClientCallbackContext clientCallbackContext = new ClientCallbackContext();
        clientCallbackContext.setContext(ctx);

        ndcClientConfiguration.getDataReadCallback().accept(msg, clientCallbackContext);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("连接断开：{}", ctx.channel().remoteAddress());
        ClientCallbackContext clientCallbackContext = new ClientCallbackContext();
        clientCallbackContext.setContext(ctx);
        ndcClientConfiguration.getConnectInActiveCallback().accept(clientCallbackContext);
    }


}
