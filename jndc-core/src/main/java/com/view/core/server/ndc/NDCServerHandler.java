package com.view.core.server.ndc;

import com.view.core.protocol.NDCPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

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
        Channel channel = ctx.channel();

        ServerCallbackContext serverCallbackContext = new ServerCallbackContext();
        serverCallbackContext.setContext(ctx);
        serverCallbackContext.setNdcServer(ndcServer);
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        serverCallbackContext.setRemote(socketAddress);

        configuration.getConnectActiveCallback().accept(serverCallbackContext);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NDCPacket msg) throws Exception {
        Channel channel = ctx.channel();

        ServerCallbackContext serverCallbackContext = new ServerCallbackContext();
        serverCallbackContext.setContext(ctx);
        serverCallbackContext.setNdcServer(ndcServer);
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        serverCallbackContext.setRemote(socketAddress);

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
