package com.view.core.server.tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ByteServerHandler extends SimpleChannelInboundHandler<byte[]> {


    private ChannelHandlerContext ctx;

    private String clientId;




    /**
     * 客户端连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;

        //获取客户端唯一凭证
        this.clientId = ctx.channel().id().asLongText();

        log.info("客户端唯一凭证:{}", clientId);




        super.channelActive(ctx);
    }

    /**
     * 客户端断开连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        super.channelInactive(ctx);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {

    }
}
