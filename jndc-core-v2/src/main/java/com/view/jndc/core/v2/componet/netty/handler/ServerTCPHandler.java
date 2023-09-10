package com.view.jndc.core.v2.componet.netty.handler;


import com.view.jndc.core.v2.utils.ByteBufUtil4V;
import com.view.jndc.core.v2.utils.UniqueIdExtractor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 端口连接事件处理器
 */
@Slf4j
public class ServerTCPHandler extends ChannelInboundHandlerAdapter {


    public static final String NAME = "ServerTCPHandler";

    private ChannelHandlerContext ctx;

    private TCPOperationCallback TCPOperationCallback;


    public ServerTCPHandler(TCPOperationCallback TCPOperationCallback) {
        this.TCPOperationCallback = TCPOperationCallback;
    }

    /**
     * tcp 连接连通
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String id = UniqueIdExtractor.get4Server(socketAddress);
        TCPOperationCallback.active(id);

    }

    /**
     * 数据包到达
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = ByteBufUtil4V.readWithRelease(byteBuf);
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String id = UniqueIdExtractor.get4Server(socketAddress);
        TCPOperationCallback.dataRead(id, bytes);
    }


    /**
     * tcp 连接断开
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String id = UniqueIdExtractor.get4Server(socketAddress);
        TCPOperationCallback.inActive(id);
    }


    /**
     * 写入消息
     *
     * @param bytes
     */
    public void write(byte[] bytes) {
        ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
        this.ctx.writeAndFlush(byteBuf);
    }

    /**
     * 关闭往端口监听器建立的连接
     */
    public void releaseRelatedResources() {
        if (this.ctx != null) {
            //关闭往端口监听器建立的连接
            this.ctx.close();
            log.debug("close face tcp " + this.ctx.channel().remoteAddress());
            //释放引用
            this.ctx = null;
        }

    }


}
