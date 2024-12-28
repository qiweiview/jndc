package com.view.core.server.ndc;

import com.view.core.protocol.NDCPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NDCServerHandler extends SimpleChannelInboundHandler<NDCPacket> {

    private NDCServerConfiguration configuration;

    private NDCServer ndcServer;

    public NDCServerHandler(NDCServer ndcServer, NDCServerConfiguration configuration) {
        this.ndcServer = ndcServer;
        this.configuration = configuration;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        configuration.getConnectActiveCallback().accept(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NDCPacket msg) throws Exception {

        ServerCallbackContext serverCallbackContext = new ServerCallbackContext();
        serverCallbackContext.setContext(ctx);
        serverCallbackContext.setNdcServer(ndcServer);
        configuration.getDataReadCallback().accept(msg, serverCallbackContext);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ServerCallbackContext serverCallbackContext = new ServerCallbackContext();
        serverCallbackContext.setContext(ctx);
        serverCallbackContext.setNdcServer(ndcServer);
        configuration.getConnectInActiveCallback().accept(serverCallbackContext);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
