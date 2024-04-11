package com.view.core.server.tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ByteServerHandler extends SimpleChannelInboundHandler<byte[]> {

    private VirtualServer virtualServer;
    private ChannelHandlerContext ctx;

    private String clientId;

    public ByteServerHandler(VirtualServer virtualServer) {
        this.virtualServer = virtualServer;

    }


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

        if (this.virtualServer == null) {
            log.error("virtualServer is null");
        } else {
            this.virtualServer.setClientId(clientId);

            this.virtualServer.setDataConsumer((x) -> {
                ctx.writeAndFlush(x);
            });
            this.virtualServer.channelActive();
        }


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
        if (this.virtualServer == null) {
            log.error("virtualServer is null");
        } else {
            this.virtualServer.channelInactive();
        }
        super.channelInactive(ctx);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        if (this.virtualServer == null) {
            log.error("virtualServer is null");
        } else {
            this.virtualServer.channelRead0(msg);
        }
    }
}
