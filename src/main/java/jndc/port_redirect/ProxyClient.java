package jndc.port_redirect;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;

public class ProxyClient extends ChannelInboundHandlerAdapter {
    private ChannelHandlerContext innerChannelHandlerContext;
    private ChannelHandlerContext outerChannelHandlerContext;

    public ProxyClient(ChannelHandlerContext outerChannelHandlerContext) {
        this.outerChannelHandlerContext = outerChannelHandlerContext;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.innerChannelHandlerContext=ctx;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.outerChannelHandlerContext.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        this.outerChannelHandlerContext.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        EventLoop eventExecutors = outerChannelHandlerContext.channel().eventLoop();
        this.outerChannelHandlerContext.fireChannelInactive();
    }

    public void write(Object o){
        this.innerChannelHandlerContext.write(o);
    }

    public void writeAndFlush(Object o){
        this.innerChannelHandlerContext.writeAndFlush(o);
    }


}
