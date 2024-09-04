package com.view.core.client.ndc;

import com.view.core.protocol.NDCPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NDCClientHandler extends SimpleChannelInboundHandler<NDCPacket> {
    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        log.info("连接成功：{}", ctx.channel().remoteAddress());
        sendHeartBeat();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("捕获异常：", cause);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NDCPacket msg) throws Exception {
        log.info("client收到消息：{}", msg);
    }

    /**
     * 发送心跳
     */
    public void sendHeartBeat() {
        NDCPacket ndcPacket = NDCPacket.of(
                NDCPacket.BLANK_ADDRESS,
                NDCPacket.BLANK_ADDRESS,
                NDCPacket.UN_USED_PORT,
                NDCPacket.UN_USED_PORT,
                NDCPacket.UN_USED_PORT,
                NDCPacket.CHANNEL_HEART_BEAT);
        ndcPacket.setData(NDCPacket.BLANK_DATA);

        ctx.writeAndFlush(ndcPacket);
    }
}
